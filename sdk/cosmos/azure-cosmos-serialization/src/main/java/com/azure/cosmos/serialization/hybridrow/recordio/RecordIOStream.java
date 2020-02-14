// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serialization.hybridrow.recordio;

import com.azure.cosmos.core.Out;
import com.azure.cosmos.serialization.hybridrow.Result;
import com.azure.cosmos.serialization.hybridrow.ResultValue;
import com.azure.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.cosmos.serialization.hybridrow.io.Segment;
import com.azure.cosmos.serialization.hybridrow.recordio.RecordIOParser.ProductionType;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import static com.azure.cosmos.base.Preconditions.checkNotNull;
import static com.azure.cosmos.base.Preconditions.checkState;

public final class RecordIOStream {

    private static final CompletableFuture<Result> SUCCESS = CompletableFuture.completedFuture(Result.SUCCESS);
    private static final int INITIAL_CAPACITY = 1024;

    /**
     * Reads an entire stream of records.
     *
     * @param inputStream The stream from which to read records.
     * @param visitRecord A function that is called once for each record.
     * <p>
     * The function receives a {@link ByteBuf} of the record body's row buffer. If the function returns an error then
     * the sequence is aborted.
     *
     * @return {@link Result#SUCCESS Success} if the {@code inputStream} is parsed without issue; an error {@link Result
     * result} otherwise.
     */
    public static @Nonnull
    CompletableFuture<Result> readRecordIOAsync(
        @Nonnull InputStream inputStream,
        @Nonnull Function<ByteBuf, Result> visitRecord) {
        return readRecordIOAsync(inputStream, visitRecord, null, null);
    }

    /**
     * Reads an entire stream of records.
     *
     * @param inputStream The stream from which to read records.
     * @param visitRecord A function that is called once for each record.
     * <p>
     * The function receives a {@link ByteBuf} containing the record body's row buffer. If the function returns an error
     * then the sequence is aborted.
     * @param visitSegment An (optional) function that is called once for each segment header.
     * <p>
     * If the function is not provided then segment headers are parsed but skipped over. Otherwise--if the function is
     * provided--it is passed a {@link ByteBuf} containing the segment header's row buffer. If the function returns an
     * error value then the sequence is aborted.
     *
     * @return {@link Result#SUCCESS Success} if the {@code inputStream} is parsed without issue; an error {@link Result
     * result} otherwise.
     */
    public static @Nonnull
    CompletableFuture<Result> readRecordIOAsync(
        @Nonnull final InputStream inputStream,
        @Nonnull final Function<ByteBuf, Result> visitRecord,
        @Nullable final Function<ByteBuf, Result> visitSegment) {
        return readRecordIOAsync(inputStream, visitRecord, visitSegment, null);
    }

    public static @Nonnull
    CompletableFuture<Result> readRecordIOAsync(
        @Nonnull InputStream inputStream,
        @Nonnull Function<ByteBuf, Result> visitRecord,
        @Nullable Function<ByteBuf, Result> visitSegment,
        @Nullable ByteBuf buffer) {

        checkNotNull(inputStream, "expected non-null inputStream");
        checkNotNull(visitRecord, "expected non-null visitRecord");

        buffer = buffer != null ? buffer : Unpooled.buffer(INITIAL_CAPACITY);
        Out<Integer> need = new Out<Integer>().set(0);
        RecordIOParser parser = null;

        try {
            do {
                if (buffer.writeBytes(inputStream, inputStream.available()) == 0) {
                    return CompletableFuture.completedFuture(Result.SUCCESS);
                }
            } while (buffer.readableBytes() < need.get());

            while (buffer.readableBytes() > 0) {

                // Loop around processing available data until we don't have anymore

                Out<ProductionType> productionType = new Out<>();
                Out<ByteBuf> record = new Out<>();
                Out<Integer> consumed = new Out<>();

                Result result = parser.process(buffer, productionType, record, need, consumed);

                if (result == Result.INSUFFICIENT_BUFFER) {
                    break;
                }

                if (result != Result.SUCCESS) {
                    return CompletableFuture.completedFuture(result);
                }

                if (productionType.get() == ProductionType.SEGMENT) {

                    checkState(record.get().readableBytes() >= 0, "expected readable bytes");
                    result = visitSegment != null ? visitSegment.apply(record.get()) : Result.SUCCESS;

                    if (result != Result.SUCCESS) {
                        return CompletableFuture.completedFuture(result);
                    }
                }

                if (productionType.get() == ProductionType.RECORD) {

                    checkState(record.get().readableBytes() >= 0, "expected readable bytes");
                    result = visitRecord.apply(record.get());

                    if (result != Result.SUCCESS) {
                        return CompletableFuture.completedFuture(result);
                    }
                }
            }

            checkState(buffer.readableBytes() == 0, "expected readableBytes == 0, not %s", buffer.readableBytes());

        } catch (IllegalStateException | IOException error) {
            CompletableFuture<Result> result = new CompletableFuture<>();
            result.completeExceptionally(error);
            return result;
        }

        return CompletableFuture.completedFuture(Result.SUCCESS);
    }

     /**
     * Writes a {@link Segment segment header} to an {@link OutputStream output stream}.
     *
     * @param outputStream The {@link OutputStream output stream}.
     * @param segment The {@link Segment segment header} to write.
     * @param producer A function that produces the bodies for the {@code segment}.
     * <p>
     * The {@code producer} function is called until either an error is encountered or the function produces an empty
     * body. An empty body terminates the {@code segment}.
     * <p>
     * If the {@code producer} function returns an error then the sequence is aborted.
     * <p>
     * <h4>Note:</h4>
     * The {@code buffer} provided should <em>not</em> be the same buffer used to process any rows as both blocks of
     * memory are used concurrently.
     *
     * @return Success if the stream is written without error, the error code otherwise.
     */
    public static CompletableFuture<Result> writeRecordIOAsync(
        @Nonnull final OutputStream outputStream,
        @Nonnull final Segment segment,
        @Nonnull final Producer producer) {

        checkNotNull(producer, "expected non-null producer");
        AsyncProducer asyncProducer = (Long index) -> CompletableFuture.completedFuture(producer.apply(index));

        return writeRecordIOAsync(outputStream, segment, asyncProducer);
    }

    /**
     * Writes a {@link Segment segment header} to an {@link OutputStream output stream}.
     *
     * @param outputStream The {@link OutputStream output stream}.
     * @param segment The {@link Segment segment header} to write.
     * @param producer A function that produces the bodies for the {@code segment}.
     * <p>
     * The {@code producer} function is called until either an error is encountered or the function produces an empty
     * body. An empty body terminates the {@code segment}.
     * <p>
     * If the {@code producer} function returns an error then the sequence is aborted.
     * <p>
     * <h4>Note:</h4>
     * The {@code buffer} provided should <em>not</em> be the same buffer used to process any rows as both blocks of
     * memory are used concurrently.
     *
     * @return Success if the stream is written without error, the error code otherwise.
     */
    public static CompletableFuture<Result> writeRecordIOAsync(
        @Nonnull final OutputStream outputStream,
        @Nonnull final Segment segment,
        @Nonnull final AsyncProducer producer) {

        checkNotNull(outputStream, "expected non-null outputStream");
        checkNotNull(segment, "expected non-null segment");
        checkNotNull(producer, "expected non-null producer");

        Out<ByteBuf> metadata = new Out<>();
        Result result = RecordIOStream.FormatSegment(segment, metadata);

        if (result != Result.SUCCESS) {
            return CompletableFuture.completedFuture(result);
        }

        try {
            metadata.get().readBytes(outputStream, metadata.get().readableBytes());
        } catch (IOException error) {
            CompletableFuture<Result> future = new CompletableFuture<>();
            future.completeExceptionally(error);
            return future;
        }

        CompletableFuture<?> future = null;
        long index = 0;

        // TODO (DANOBLE) figure out how to terminate this loop
        //  Do we need another producer to tell us in advance when the body is empty?

        while (true) {

            final long column = index;

            future = future == null
                ? writeResultValue(outputStream, producer.apply(column))
                : future.thenApplyAsync(
                    r1 -> writeResultValue(outputStream, producer.apply(column)).thenApplyAsync((Result r2) -> r2));

            index++;
        }

        return (CompletableFuture<Result>) future;
    }

    private static CompletableFuture<Result> writeResultValue(
        @Nonnull final OutputStream outputStream,
        @Nonnull final CompletableFuture<ResultValue<ByteBuf>> future) {

        return future.thenApplyAsync(resultValue -> {
            Result result = resultValue.getResult();

            if (result != Result.SUCCESS) {
                return result;
            }

            final ByteBuf body = resultValue.getValue();

            if (body.readableBytes() == 0) {
                return result;
            }

            final Out<ByteBuf> buffer = new Out<>();

            result = FormatRow(body, buffer);

            if (result != Result.SUCCESS) {
                return result;
            }

            try {
                buffer.get().readBytes(outputStream, buffer.get().readableBytes());
                body.readBytes(outputStream, body.readableBytes());
            } catch (IOException cause) {

            }

            return result;

        });
    }

    /**
     * Compute and format a record header for the given record body.
     *
     * @param body The body whose record header should be formatted.
     * @param buffer The byte sequence of the written row buffer.
     *
     * @return Success if the write completes without error, the error code otherwise.
     */
    private static Result FormatRow(ByteBuf body, Out<ByteBuf> buffer) {

        Out<RowBuffer> rowBuffer = new Out<>();
        Result result = RecordIOFormatter.formatRecord(body, rowBuffer);

        if (result != Result.SUCCESS) {
            buffer.set(null);
            return result;
        }

        buffer.setAndGet(rowBuffer.get().buffer());
        return Result.SUCCESS;
    }

    /**
     * Format a segment.
     *
     * @param segment The segment to format.
     * @param buffer The byte sequence of the written row buffer.
     *
     * @return Success if the write completes without error, the error code otherwise.
     */
    private static Result FormatSegment(
        @Nonnull final Segment segment,
        @Nonnull final Out<ByteBuf> buffer) {

        Out<RowBuffer> rowBuffer = new Out<>();
        Result result = RecordIOFormatter.formatSegment(segment, rowBuffer);

        if (result != Result.SUCCESS) {
            buffer.set(null);
            return result;
        }

        // TODO (DANOBLE) ensure the returned ByteBuf does not leak memory
        buffer.setAndGet(rowBuffer.get().buffer());
        return Result.SUCCESS;
    }

    /**
     * A function that produces record bodies.
     * <p>
     * Record bodies are returned as memory blocks. It is expected that each block is a HybridRow, but any binary data
     * is allowed. The argument to {#apply} is the 0based index of the reord within the segment to be produced. The
     * {#apply} method produces a {@link ResultValue} of type {@link ByteBuf}}.
     */
    @FunctionalInterface
    public interface Producer extends Function<Long, ResultValue<ByteBuf>> {
    }

    /**
     * A function that produces record bodies.
     * <p>
     * Record bodies are returned as memory blocks. It is expected that each block is a HybridRow, but any binary data
     * is allowed. The argument to {#apply} is the 0-based index of the record within the segment to be produced. The
     * {#apply} method produces a {@link CompletableFuture} of type {@link ResultValue} of type {@code ByteBuf}.
     */
    public interface AsyncProducer extends Function<Long, CompletableFuture<ResultValue<ByteBuf>>> {
    }
}
