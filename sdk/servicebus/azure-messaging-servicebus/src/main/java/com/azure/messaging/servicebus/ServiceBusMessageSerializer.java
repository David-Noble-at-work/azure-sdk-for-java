// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.messaging.servicebus;

import com.azure.core.amqp.implementation.MessageSerializer;
import com.azure.core.amqp.implementation.RequestResponseUtils;
import com.azure.core.util.logging.ClientLogger;
import com.azure.messaging.servicebus.implementation.Messages;
import org.apache.qpid.proton.Proton;
import org.apache.qpid.proton.amqp.Binary;
import org.apache.qpid.proton.amqp.Decimal128;
import org.apache.qpid.proton.amqp.Decimal32;
import org.apache.qpid.proton.amqp.Decimal64;
import org.apache.qpid.proton.amqp.Symbol;
import org.apache.qpid.proton.amqp.UnsignedByte;
import org.apache.qpid.proton.amqp.UnsignedInteger;
import org.apache.qpid.proton.amqp.UnsignedLong;
import org.apache.qpid.proton.amqp.UnsignedShort;
import org.apache.qpid.proton.amqp.messaging.AmqpSequence;
import org.apache.qpid.proton.amqp.messaging.AmqpValue;
import org.apache.qpid.proton.amqp.messaging.ApplicationProperties;
import org.apache.qpid.proton.amqp.messaging.Data;
import org.apache.qpid.proton.amqp.messaging.MessageAnnotations;
import org.apache.qpid.proton.amqp.messaging.Properties;
import org.apache.qpid.proton.amqp.messaging.Section;
import org.apache.qpid.proton.amqp.transaction.Declare;
import org.apache.qpid.proton.amqp.transaction.Discharge;
import org.apache.qpid.proton.message.Message;

import java.lang.reflect.Array;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Deserializes and serializes messages to and from Azure Service Bus.
 */
class ServiceBusMessageSerializer implements MessageSerializer {
    private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
    private static final String ENQUEUED_TIME_UTC_NAME = "x-opt-enqueued-time";
    private static final String SCHEDULED_ENQUEUE_TIME_NAME = "x-opt-scheduled-enqueue-time";
    private static final String SEQUENCE_NUMBER_NAME = "x-opt-sequence-number";
    private static final String LOCKED_UNTIL_NAME = "x-opt-locked-until";
    private static final String PARTITION_KEY_NAME = "x-opt-partition-key";
    private static final String VIA_PARTITION_KEY_NAME = "x-opt-via-partition-key";
    private static final String DEAD_LETTER_SOURCE_NAME = "x-opt-deadletter-source";
    private static final String REQUEST_RESPONSE_MESSAGES = "messages";
    private static final String REQUEST_RESPONSE_MESSAGE = "message";
    private static final int REQUEST_RESPONSE_OK_STATUS_CODE = 200;

    private final ClientLogger logger = new ClientLogger(ServiceBusMessageSerializer.class);

    /**
     * Gets the serialized size of the AMQP message.
     */
    @Override
    public int getSize(org.apache.qpid.proton.message.Message amqpMessage) {
        if (amqpMessage == null) {
            return 0;
        }

        int payloadSize = getPayloadSize(amqpMessage);

        final MessageAnnotations messageAnnotations = amqpMessage.getMessageAnnotations();
        final ApplicationProperties applicationProperties = amqpMessage.getApplicationProperties();

        int annotationsSize = 0;
        int applicationPropertiesSize = 0;

        if (messageAnnotations != null) {
            final Map<Symbol, Object> map = messageAnnotations.getValue();

            for (Map.Entry<Symbol, Object> entry : map.entrySet()) {
                final int size = sizeof(entry.getKey()) + sizeof(entry.getValue());
                annotationsSize += size;
            }
        }

        if (applicationProperties != null) {
            final Map<String, Object> map = applicationProperties.getValue();

            for (Map.Entry<String, Object> entry : map.entrySet()) {
                final int size = sizeof(entry.getKey()) + sizeof(entry.getValue());
                applicationPropertiesSize += size;
            }
        }

        return annotationsSize + applicationPropertiesSize + payloadSize;
    }

    /**
     * Creates the AMQP message represented by this {@code object}. Currently, only supports serializing
     * {@link ServiceBusMessage}.
     *
     * @param object Concrete object to deserialize.
     *
     * @return A new AMQP message for this {@code object}.
     *
     * @throws IllegalArgumentException if {@code object} is not an instance of {@link ServiceBusMessage}.
     */
    @Override
    public <T> org.apache.qpid.proton.message.Message serialize(T object) {
        Objects.requireNonNull(object, "'object' to serialize cannot be null.");

        if (!(object instanceof ServiceBusMessage)) {
            throw logger.logExceptionAsError(new IllegalArgumentException(
                "Cannot serialize object that is not ServiceBusMessage. Clazz: " + object.getClass()));
        }

        final ServiceBusMessage brokeredMessage = (ServiceBusMessage) object;
        final org.apache.qpid.proton.message.Message amqpMessage = Proton.message();
        final byte[] body = brokeredMessage.getBody();

        //TODO (conniey): support AMQP sequence and AMQP value.
        amqpMessage.setBody(new Data(new Binary(body)));

        if (brokeredMessage.getProperties() != null) {
            amqpMessage.setApplicationProperties(new ApplicationProperties(brokeredMessage.getProperties()));
        }

        if (brokeredMessage.getTimeToLive() != null) {
            amqpMessage.setTtl(brokeredMessage.getTimeToLive().toMillis());
        }

        if (amqpMessage.getProperties() == null) {
            amqpMessage.setProperties(new Properties());
        }

        amqpMessage.setMessageId(brokeredMessage.getMessageId());
        amqpMessage.setContentType(brokeredMessage.getContentType());
        amqpMessage.setCorrelationId(brokeredMessage.getCorrelationId());
        amqpMessage.setSubject(brokeredMessage.getLabel());
        amqpMessage.getProperties().setTo(brokeredMessage.getTo());
        amqpMessage.setReplyTo(brokeredMessage.getReplyTo());
        amqpMessage.setReplyToGroupId(brokeredMessage.getReplyToSessionId());
        amqpMessage.setGroupId(brokeredMessage.getSessionId());

        final Map<Symbol, Object> messageAnnotationsMap = new HashMap<>();
        if (brokeredMessage.getScheduledEnqueueTime() != null) {
            messageAnnotationsMap.put(Symbol.valueOf(SCHEDULED_ENQUEUE_TIME_NAME),
                Date.from(brokeredMessage.getScheduledEnqueueTime()));
        }

        final String partitionKey = brokeredMessage.getPartitionKey();
        if (partitionKey != null && !partitionKey.isEmpty()) {
            messageAnnotationsMap.put(Symbol.valueOf(PARTITION_KEY_NAME), brokeredMessage.getPartitionKey());
        }

        final String viaPartitionKey = brokeredMessage.getViaPartitionKey();
        if (viaPartitionKey != null && !viaPartitionKey.isEmpty()) {
            messageAnnotationsMap.put(Symbol.valueOf(VIA_PARTITION_KEY_NAME), viaPartitionKey);
        }

        amqpMessage.setMessageAnnotations(new MessageAnnotations(messageAnnotationsMap));

        return amqpMessage;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T deserialize(org.apache.qpid.proton.message.Message message, Class<T> clazz) {
        Objects.requireNonNull(message, "'message' cannot be null.");
        Objects.requireNonNull(clazz, "'clazz' cannot be null.");

        if (clazz == ServiceBusReceivedMessage.class) {
            return (T) deserializeMessage(message);
        } else if (clazz == List.class) {
            return (T) deserializeListOfMessages(message);
        } else {
            throw logger.logExceptionAsError(new IllegalArgumentException(
                "Deserialization only supports ServiceBusReceivedMessage."));
        }
    }

    private List<ServiceBusReceivedMessage> deserializeListOfMessages(Message amqpMessage) {
        //maintain the order of elements because last sequence number needs to be maintain.
        List<Message> listAmqpMessages = convertAmqpValueMessageToBrokeredMessage(amqpMessage);

        List<ServiceBusReceivedMessage> receivedMessageList = new ArrayList<>();
        for (Message oneAmqpMessage:listAmqpMessages) {
            ServiceBusReceivedMessage serviceBusReceivedMessage = deserializeMessage(oneAmqpMessage);
            receivedMessageList.add(serviceBusReceivedMessage);
        }

        return receivedMessageList;
    }

    private ServiceBusReceivedMessage deserializeMessage(org.apache.qpid.proton.message.Message amqpMessage) {
        final ServiceBusReceivedMessage brokeredMessage;
        final Section body = amqpMessage.getBody();
        if (body != null) {
            //TODO (conniey): Support other AMQP types like AmqpValue and AmqpSequence.
            if (body instanceof Data) {
                final Binary messageData = ((Data) body).getValue();
                final byte[] bytes = messageData.getArray();
                brokeredMessage = new ServiceBusReceivedMessage(bytes);
            } else {
                logger.warning(String.format(Messages.MESSAGE_NOT_OF_TYPE, body.getType()));
                brokeredMessage = new ServiceBusReceivedMessage(EMPTY_BYTE_ARRAY);
            }
        } else {
            logger.warning(String.format(Messages.MESSAGE_NOT_OF_TYPE, "null"));
            brokeredMessage = new ServiceBusReceivedMessage(EMPTY_BYTE_ARRAY);
        }

        // Application properties
        ApplicationProperties applicationProperties = amqpMessage.getApplicationProperties();
        if (applicationProperties != null) {
            brokeredMessage.getProperties().putAll(applicationProperties.getValue());
        }

        // Header
        brokeredMessage.setTimeToLive(Duration.ofMillis(amqpMessage.getTtl()));
        brokeredMessage.setDeliveryCount(amqpMessage.getDeliveryCount());

        // Properties
        final Object messageId = amqpMessage.getMessageId();
        if (messageId != null) {
            brokeredMessage.setMessageId(messageId.toString());
        }

        brokeredMessage.setContentType(amqpMessage.getContentType());
        final Object correlationId = amqpMessage.getCorrelationId();
        if (correlationId != null) {
            brokeredMessage.setCorrelationId(correlationId.toString());
        }

        final Properties properties = amqpMessage.getProperties();
        if (properties != null) {
            brokeredMessage.setTo(properties.getTo());
        }

        brokeredMessage.setLabel(amqpMessage.getSubject());
        brokeredMessage.setReplyTo(amqpMessage.getReplyTo());
        brokeredMessage.setReplyToSessionId(amqpMessage.getReplyToGroupId());
        brokeredMessage.setSessionId(amqpMessage.getGroupId());

        // Message Annotations
        final MessageAnnotations messageAnnotations = amqpMessage.getMessageAnnotations();
        if (messageAnnotations != null) {
            Map<Symbol, Object> messageAnnotationsMap = messageAnnotations.getValue();
            if (messageAnnotationsMap != null) {
                for (Map.Entry<Symbol, Object> entry : messageAnnotationsMap.entrySet()) {
                    final String key = entry.getKey().toString();
                    final Object value = entry.getValue();

                    switch (key) {
                        case ENQUEUED_TIME_UTC_NAME:
                            brokeredMessage.setEnqueuedTime(((Date) value).toInstant());
                            break;
                        case SCHEDULED_ENQUEUE_TIME_NAME:
                            brokeredMessage.setScheduledEnqueueTime(((Date) value).toInstant());
                            break;
                        case SEQUENCE_NUMBER_NAME:
                            brokeredMessage.setSequenceNumber((long) value);
                            break;
                        case LOCKED_UNTIL_NAME:
                            brokeredMessage.setLockedUntil(((Date) value).toInstant());
                            break;
                        case PARTITION_KEY_NAME:
                            brokeredMessage.setPartitionKey((String) value);
                            break;
                        case VIA_PARTITION_KEY_NAME:
                            brokeredMessage.setViaPartitionKey((String) value);
                            break;
                        case DEAD_LETTER_SOURCE_NAME:
                            brokeredMessage.setDeadLetterSource((String) value);
                            break;
                        default:
                            logger.info("Unrecognised key: {}, value: {}", key, value);
                            break;
                    }
                }
            }
        }

        // TODO (conniey): Set delivery tag and lock token. .NET does not expose delivery tag. Do we need it?

        // if (deliveryTag != null && deliveryTag.length == LOCK_TOKEN_SIZE) {
        //     UUID lockToken = Util.convertDotNetBytesToUUID(deliveryTag);
        //     brokeredMessage.setLockToken(lockToken);
        // } else {
        //     brokeredMessage.setLockToken(ZERO_LOCK_TOKEN);
        // }
        // brokeredMessage.setDeliveryTag(deliveryTag);

        return brokeredMessage;
    }

    private static int getPayloadSize(org.apache.qpid.proton.message.Message msg) {
        if (msg == null || msg.getBody() == null) {
            return 0;
        }

        final Section bodySection = msg.getBody();
        if (bodySection instanceof AmqpValue) {
            return sizeof(((AmqpValue) bodySection).getValue());
        } else if (bodySection instanceof AmqpSequence) {
            return sizeof(((AmqpSequence) bodySection).getValue());
        } else if (bodySection instanceof Data) {
            final Data payloadSection = (Data) bodySection;
            final Binary payloadBytes = payloadSection.getValue();
            return sizeof(payloadBytes);
        } else {
            return 0;
        }
    }

    @SuppressWarnings("rawtypes")
    private static int sizeof(Object obj) {
        if (obj == null) {
            return 0;
        }

        if (obj instanceof String) {
            return obj.toString().length() << 1;
        }

        if (obj instanceof Symbol) {
            return ((Symbol) obj).length() << 1;
        }

        if (obj instanceof Byte || obj instanceof UnsignedByte) {
            return Byte.BYTES;
        }

        if (obj instanceof Integer || obj instanceof UnsignedInteger) {
            return Integer.BYTES;
        }

        if (obj instanceof Long || obj instanceof UnsignedLong || obj instanceof Date) {
            return Long.BYTES;
        }

        if (obj instanceof Short || obj instanceof UnsignedShort) {
            return Short.BYTES;
        }

        if (obj instanceof Boolean) {
            return 1;
        }

        if (obj instanceof Character) {
            return 4;
        }

        if (obj instanceof Float) {
            return Float.BYTES;
        }

        if (obj instanceof Double) {
            return Double.BYTES;
        }

        if (obj instanceof UUID) {
            // UUID is internally represented as 16 bytes. But how does ProtonJ encode it? To be safe..
            // we can treat it as a string of 36 chars = 72 bytes. return 72;
            return 16;
        }

        if (obj instanceof Decimal32) {
            return 4;
        }

        if (obj instanceof Decimal64) {
            return 8;
        }

        if (obj instanceof Decimal128) {
            return 16;
        }

        if (obj instanceof Binary) {
            return ((Binary) obj).getLength();
        }

        if (obj instanceof Declare) {
            // Empty declare command takes up 7 bytes.
            return 7;
        }

        if (obj instanceof Discharge) {
            Discharge discharge = (Discharge) obj;
            return 12 + discharge.getTxnId().getLength();
        }

        if (obj instanceof Map) {
            // Size and Count each take a max of 4 bytes
            int size = 8;
            Map map = (Map) obj;
            for (Object value : map.keySet()) {
                size += sizeof(value);
            }

            for (Object value : map.values()) {
                size += sizeof(value);
            }

            return size;
        }

        if (obj instanceof Iterable) {
            // Size and Count each take a max of 4 bytes
            int size = 8;
            for (Object innerObject : (Iterable) obj) {
                size += sizeof(innerObject);
            }

            return size;
        }

        if (obj.getClass().isArray()) {
            // Size and Count each take a max of 4 bytes
            int size = 8;
            int length = Array.getLength(obj);
            for (int i = 0; i < length; i++) {
                size += sizeof(Array.get(obj, i));
            }

            return size;
        }

        throw new IllegalArgumentException(String.format(Locale.US,
            "Encoding Type: %s is not supported", obj.getClass()));
    }

    private List<Message> convertAmqpValueMessageToBrokeredMessage(Message amqpResponseMessage) {
        List<Message> messageList = new ArrayList<>();
        int statusCode = RequestResponseUtils.getResponseStatusCode(amqpResponseMessage);

        if (statusCode == REQUEST_RESPONSE_OK_STATUS_CODE) {
            Object responseBodyMap = ((AmqpValue) amqpResponseMessage.getBody()).getValue();
            if (responseBodyMap != null && responseBodyMap instanceof Map) {
                Object messages = ((Map) responseBodyMap).get(REQUEST_RESPONSE_MESSAGES);
                if (messages != null && messages instanceof Iterable) {
                    for (Object message : (Iterable) messages) {
                        if (message instanceof Map) {
                            Message responseMessage = Message.Factory.create();
                            Binary messagePayLoad = (Binary) ((Map) message)
                                .get(REQUEST_RESPONSE_MESSAGE);
                            responseMessage.decode(messagePayLoad.getArray(), messagePayLoad.getArrayOffset(),
                                messagePayLoad.getLength());

                            messageList.add(responseMessage);
                        }
                    }
                }
            }

        }
        return messageList;
    }
}
