// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serialization.hybridrow.recordio;

import com.azure.cosmos.core.Out;
import com.azure.cosmos.serialization.hybridrow.HybridRowHeader;
import com.azure.cosmos.serialization.hybridrow.HybridRowVersion;
import com.azure.cosmos.serialization.hybridrow.Result;
import com.azure.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.cosmos.serialization.hybridrow.io.RowReader;
import com.azure.cosmos.serialization.hybridrow.io.Segment;
import com.azure.cosmos.serialization.hybridrow.layouts.SystemSchema;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectArrayMap;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectMap;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.zip.CRC32;

import static com.azure.cosmos.implementation.base.Preconditions.checkNotNull;

/**
 * The type Record io parser.
 */
public final class RecordIOParser {

    private Record record;
    private Segment segment;
    private State state;

    public RecordIOParser() {
        this.segment = null;
        this.record = null;
        this.state = State.START;
    }
    /**
     * Processes one buffers worth of data possibly advancing the parser state
     *
     * @param buffer The buffer to consume
     * @param type Indicates the type of Hybrid Row produced in {@code record}
     * @param record If non-empty, then the body of the next record in the sequence
     * @param need The smallest number of bytes needed to advanced the parser state further.
     * <p>
     * It is recommended that this method not be called again until at least this number of bytes are available.
     *
     * @return {@link Result#SUCCESS} if no error has occurred; otherwise the {@link Result} of the last error
     * encountered during parsing.
     */
    @SuppressWarnings("unchecked")
    @NotNull
    public Result process(
        @NotNull final ByteBuf buffer,
        @NotNull final Out<ProductionType> type,
        @NotNull final Out<ByteBuf> record,
        @NotNull final Out<Integer> need) {

        checkNotNull(buffer, "expected non-null buffer");
        checkNotNull(type, "expected non-null type");
        checkNotNull(record, "expected non-null record");
        checkNotNull(need, "expected non-null need");

        final Out<?> out = new Out<>();
        RowReader reader;

        Result result = Result.SUCCESS;
        type.set(ProductionType.NONE);
        record.set(null);

        do {
            switch (this.state) {

                case START:

                    this.state = State.NEED_SEGMENT_LENGTH;
                    break;

                case NEED_SEGMENT_LENGTH:

                    final int minimalSegmentRowSize = HybridRowHeader.BYTES + RecordIOFormatter.SEGMENT_LAYOUT.size();

                    if (buffer.readableBytes() < minimalSegmentRowSize) {
                        need.set(minimalSegmentRowSize);
                        return Result.INSUFFICIENT_BUFFER;
                    }

                    reader = new RowReader(new RowBuffer(
                        buffer.slice(buffer.readerIndex(), minimalSegmentRowSize),
                        HybridRowVersion.V1,
                        SystemSchema.layoutResolver()));

                    result = SegmentSerializer.read(reader, (Out<Segment>) out);
                    this.segment = (Segment) out.get();

                    if (result != Result.SUCCESS) {
                        this.state = State.ERROR;
                        break;
                    }

                    this.state = State.NEED_SEGMENT;
                    break;

                case NEED_SEGMENT:

                    final int segmentLength = this.segment.length();

                    if (buffer.readableBytes() < segmentLength) {
                        need.set(segmentLength);
                        return Result.INSUFFICIENT_BUFFER;
                    }

                    reader = new RowReader(
                        new RowBuffer(
                            buffer.slice(buffer.readerIndex(), segmentLength),
                            HybridRowVersion.V1,
                            SystemSchema.layoutResolver()));

                    result = SegmentSerializer.read(reader, (Out<Segment>) out);
                    this.segment = (Segment) out.get();

                    if (result != Result.SUCCESS) {
                        this.state = State.ERROR;
                        break;
                    }

                    record.set(buffer.readSlice(segmentLength));
                    type.set(ProductionType.SEGMENT);
                    this.state = State.NEED_HEADER;
                    need.set(0);

                    return Result.SUCCESS;

                case NEED_HEADER:

                    if (buffer.readableBytes() < HybridRowHeader.BYTES) {
                        need.set(HybridRowHeader.BYTES);
                        return Result.INSUFFICIENT_BUFFER;
                    }

                    HybridRowHeader header = HybridRowHeader.decode(buffer);

                    if (header.version() != HybridRowVersion.V1) {
                        result = Result.INVALID_ROW;
                        this.state = State.ERROR;
                        break;
                    }

                    if (header.schemaId().equals(SystemSchema.RECORD_SCHEMA_ID)) {
                        this.state = State.NEED_RECORD;
                        break;
                    }

                    if (header.schemaId().equals(SystemSchema.SEGMENT_SCHEMA_ID)) {
                        this.state = State.NEED_SEGMENT;
                        break;
                    }

                    result = Result.INVALID_ROW;
                    this.state = State.ERROR;
                    break;

                case NEED_RECORD:

                    final int minimalRecordRowSize = HybridRowHeader.BYTES + RecordIOFormatter.RECORD_LAYOUT.size();

                    if (buffer.readableBytes() < minimalRecordRowSize) {
                        need.set(minimalRecordRowSize);
                        return Result.INSUFFICIENT_BUFFER;
                    }

                    buffer.markReaderIndex();

                    reader = new RowReader(
                        new RowBuffer(
                            buffer.readSlice(minimalRecordRowSize),
                            HybridRowVersion.V1,
                            SystemSchema.layoutResolver()));

                    result = RecordSerializer.read(reader, (Out<Record>) out);
                    this.record = (Record) out.get();

                    if (result != Result.SUCCESS) {
                        buffer.resetReaderIndex();
                        this.state = State.ERROR;
                        break;
                    }

                    this.state = State.NEED_ROW;
                    break;

                case NEED_ROW:

                    if (buffer.readableBytes() < this.record.length()) {
                        need.set(this.record.length());
                        return Result.INSUFFICIENT_BUFFER;
                    }

                    buffer.markReaderIndex();

                    record.set(buffer.readBytes(this.record.length()));
                    CRC32 crc32 = new CRC32();

                    if (record.get().hasArray()) {
                        crc32.update(record.get().array());
                    } else {
                        record.get().forEachByte(b -> {
                            crc32.update(b);
                            return true;
                        });
                    }

                    if (crc32.getValue() != this.record.crc32()) {
                        result = Result.INVALID_ROW;
                        this.state = State.ERROR;
                        buffer.resetReaderIndex();
                        break;
                    }

                    this.state = State.NEED_HEADER;
                    type.set(ProductionType.RECORD);
                    need.set(0);

                    return Result.SUCCESS;

                default:
                    assert false : "unexpected value: " + this.state;
            }

        } while (this.state != State.ERROR);

        need.set(0);
        return result;
    }

    /**
     * {@code true} if a valid segment has been parsed.
     *
     * @return {@code true} if a valid segment has been parsed.
     */
    public boolean haveSegment() {
        return this.state.ordinal() >= State.NEED_HEADER.ordinal();
    }

    /**
     * Current active {@link Segment segment} or {@code null}, if there is no current active segment.
     *
     * @return current active {@link Segment segment} or {@code null}, if there is no current active segment.
     */
    @Nullable
    public Segment segment() {
        return this.haveSegment() ? this.segment : null;
    }

    /**
     * Describes the type of Hybrid Rows produced by the parser.
     */
    public enum ProductionType {
        /**
         * No hybrid row was produced. The parser needs more data.
         */
        NONE,

        /**
         * A new segment row was produced.
         */
        SEGMENT,

        /**
         * A record in the current segment was produced.
         */
        RECORD;

        /**
         * The constant BYTES.
         */
        public static final int BYTES = Integer.BYTES;

        private static ProductionType[] mappings = {
            NONE, SEGMENT, RECORD
        };

        /**
         * From production type.
         *
         * @param value the ordinal value.
         *
         * @return the production type.
         */
        public static ProductionType from(int value) {
            return 0 <= value && value <= mappings.length ? mappings[value] : null;
        }
    }

    /**
     * The states for the internal state machine.
     * Note: numerical ordering of these states matters.
     */
    private enum State {
        /**
         * The Start.
         */
        START("Start: no buffers have yet been provided to the parser"),
        /**
         * The Error.
         */
        ERROR("Unrecoverable parse error encountered"),
        /**
         * The Need segment length.
         */
        NEED_SEGMENT_LENGTH("Parsing segment header length"),
        /**
         * The Need segment.
         */
        NEED_SEGMENT("Parsing segment header"),
        /**
         * The Need header.
         */
        NEED_HEADER("Parsing HybridRow header"),
        /**
         * The Need record.
         */
        NEED_RECORD("Parsing record header"),
        /**
         * The Need row.
         */
        NEED_ROW("Parsing row body");

        /**
         * The constant BYTES.
         */
        public static final int BYTES = Byte.SIZE;

        private static State[] mappings = {
            START, ERROR, NEED_SEGMENT_LENGTH, NEED_SEGMENT, NEED_HEADER, NEED_RECORD, NEED_ROW
        };

        private final String description;

        State(String description) {
            this.description = description;
        }

        /**
         * Description string.
         *
         * @return the string
         */
        public String description() {
            return this.description;
        }

        /**
         * From state.
         *
         * @param value the value
         *
         * @return the state
         */
        public static State from(byte value) {
            return 0x00 <= value && value <= mappings.length ? mappings[value] : null;
        }
    }
}
