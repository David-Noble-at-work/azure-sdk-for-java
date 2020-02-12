// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serialization.hybridrow.recordio;

import com.azure.cosmos.core.Out;
import com.azure.cosmos.serialization.hybridrow.Result;
import com.azure.cosmos.serialization.hybridrow.ReturnValue;
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
    CompletableFuture<Result> ReadRecordIOAsync(
        @Nonnull InputStream inputStream,
        @Nonnull Function<ByteBuf, Result> visitRecord) {
        return ReadRecordIOAsync(inputStream, visitRecord, null, null);
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
    CompletableFuture<Result> ReadRecordIOAsync(
        @Nonnull final InputStream inputStream,
        @Nonnull final Function<ByteBuf, Result> visitRecord,
        @Nullable final Function<ByteBuf, Result> visitSegment) {
        return ReadRecordIOAsync(inputStream, visitRecord, visitSegment, null);
    }

    public static @Nonnull
    CompletableFuture<Result> ReadRecordIOAsync(
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
     * @param produce A function that produces the bodies for the {@code segment}.
     * <p>
     * The {@code produce} function is called until either an error is encountered or the function produces an empty
     * body. An empty body terminates the {@code segment}.
     * <p>
     * If the {@code produce} function returns an error then the sequence is aborted.
     * <p>
     * <h4>Note:</h4>
     * The {@code buffer} provided should <em>not</em> be the same buffer used to process any rows as both blocks of
     * memory are used concurrently.
     *
     * @return Success if the stream is written without error, the error code otherwise.
     */
    public static CompletableFuture<Result> WriteRecordIOAsync(
        @Nonnull final OutputStream outputStream,
        @Nonnull final Segment segment,
        final ProduceFunc produce) {
        return WriteRecordIOAsync(outputStream, segment, produce, Unpooled.buffer());
    }

     /**
     * Writes a {@link Segment segment header} to an {@link OutputStream output stream}.
     *
     * @param outputStream The {@link OutputStream output stream}.
     * @param segment The {@link Segment segment header} to write.
     * @param produce A function that produces the bodies for the {@code segment}.
     * @param buffer
     * <p>
     * The {@code produce} function is called until either an error is encountered or the function produces an empty
     * body. An empty body terminates the {@code segment}.
     * <p>
     * If the {@code produce} function returns an error then the sequence is aborted.
     * <p>
     * <h4>Note:</h4>
     * The {@code buffer} provided should <em>not</em> be the same buffer used to process any rows as both blocks of
     * memory are used concurrently.
     *
     * @return Success if the stream is written without error, the error code otherwise.
     */
    public static CompletableFuture<Result> WriteRecordIOAsync(
        @Nonnull final OutputStream outputStream,
        @Nonnull final Segment segment,
        final ProduceFunc produce,
        ByteBuf buffer) {

        return RecordIOStream.WriteRecordIOAsync(
            outputStream, segment, index -> {
                ReadOnlyMemory<Byte> buffer;
                Out<ReadOnlyMemory<Byte>> tempOut_buffer = new Out<ReadOnlyMemory<Byte>>();
                buffer = tempOut_buffer.get();
                return new ValueTask<(Result, ReadOnlyMemory < Byte >) > ((r,buffer))
            }, buffer);
    }

    /**
     * Writes a {@link Segment segment header} to an {@link OutputStream output stream}.
     *
     * @param outputStream The {@link OutputStream output stream}.
     * @param segment The {@link Segment segment header} to write.
     * @param produce A function that produces the bodies for the {@code segment}.
     * <p>
     * The {@code produce} function is called until either an error is encountered or the function produces an empty
     * body. An empty body terminates the {@code segment}.
     * <p>
     * If the {@code produce} function returns an error then the sequence is aborted.
     * <p>
     * <h4>Note:</h4>
     * The {@code buffer} provided should <em>not</em> be the same buffer used to process any rows as both blocks of
     * memory are used concurrently.
     *
     * @return Success if the stream is written without error, the error code otherwise.
     */
    public static CompletableFuture<Result> WriteRecordIOAsync(
        @Nonnull final OutputStream outputStream,
        @Nonnull final Segment segment,
        @Nonnull final ProduceFuncAsync produce) {
        return WriteRecordIOAsync(outputStream, segment, produce, Unpooled.buffer());
    }

    public static CompletableFuture<Result> WriteRecordIOAsync(
        @Nonnull final OutputStream outputStream,
        @Nonnull final Segment segment,
        @Nonnull final ProduceFuncAsync produce,
        @Nonnull final ByteBuf buffer) {

        checkNotNull(outputStream, "expected non-null outputStream");
        checkNotNull(segment, "expected non-null segment");
        checkNotNull(produce, "expected non-null produce");
        checkNotNull(buffer, "expected non-null buffer");

        // Write a RecordIO stream.
        Memory<Byte> metadata;
        Out<Memory<Byte>> tempOut_metadata = new Out<Memory<Byte>>();
        Result r = RecordIOStream.FormatSegment(segment, buffer, tempOut_metadata);
        metadata = tempOut_metadata.get();
        if (r != Result.SUCCESS) {
            return r;
        }

        // TODO: C# TO JAVA CONVERTER: There is no equivalent to 'await' in Java:
        await stm.WriteAsync(metadata);

        long index = 0;
        while (true) {
            //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
            //ORIGINAL LINE: ReadOnlyMemory<byte> body;
            ReadOnlyMemory<Byte> body;
            // TODO: C# TO JAVA CONVERTER: Java has no equivalent to the C# deconstruction assignments:
            (r, body) =await produce (index++);
            if (r != Result.SUCCESS) {
                return r;
            }

            if (body.IsEmpty) {
                break;
            }

            Out<Memory<Byte>> tempOut_metadata2 = new Out<Memory<Byte>>();
            //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
            //ORIGINAL LINE: r = RecordIOStream.FormatRow(body, buffer, out metadata);
            r = RecordIOStream.FormatRow(body, buffer, tempOut_metadata2);
            metadata = tempOut_metadata2.get();
            if (r != Result.SUCCESS) {
                return r;
            }

            // Metadata and Body memory blocks should not overlap since they are both in
            // play at the same time. If they do this usually means that the same buffer
            // was incorrectly used for both. Check the buffer parameter passed to
            // WriteRecordIOAsync for metadata.
            checkState(!metadata.Span.Overlaps(body.Span));

            // TODO: C# TO JAVA CONVERTER: There is no equivalent to 'await' in Java:
            await stm.WriteAsync(metadata);
            // TODO: C# TO JAVA CONVERTER: There is no equivalent to 'await' in Java:
            await stm.WriteAsync(body);
        }

        return Result.SUCCESS;
    }

    /**
     * Compute and format a record header for the given record body.
     *
     * @param body The body whose record header should be formatted.
     * @param resizer The resizer to use in allocating a buffer for the record header.
     * @param block The byte sequence of the written row buffer.
     *
     * @return Success if the write completes without error, the error code otherwise.
     */
    //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
    //ORIGINAL LINE: private static Result FormatRow(ReadOnlyMemory<byte> body, MemorySpanResizer<byte> resizer, out
    // Memory<byte> block)
    private static Result FormatRow(ReadOnlyMemory<Byte> body, MemorySpanResizer<Byte> resizer,
                                    Out<Memory<Byte>> block) {
        RowBuffer row;
        Out<RowBuffer> tempOut_row = new Out<RowBuffer>();
        Result r = RecordIOFormatter.FormatRecord(body, tempOut_row);
        row = tempOut_row.get();
        if (r != Result.SUCCESS) {
            block.setAndGet(null);
            return r;
        }

        block.setAndGet(resizer.getMemory().Slice(0, row.Length));
        return Result.SUCCESS;
    }

    /**
     * Format a segment.
     *
     * @param segment The segment to format.
     * @param resizer The resizer to use in allocating a buffer for the segment.
     * @param block The byte sequence of the written row buffer.
     *
     * @return Success if the write completes without error, the error code otherwise.
     */
    private static Result FormatSegment(
        @Nonnull final Segment segment,
        @Nonnull final ByteBuf buffer,
        Out<Memory<Byte>> block) {

        RowBuffer row;
        Out<RowBuffer> tempOut_row =
            new Out<RowBuffer>();
        Result r = RecordIOFormatter.FormatSegment(segment, tempOut_row, resizer);
        row = tempOut_row.get();
        if (r != Result.SUCCESS) {
            block.setAndGet(null);
            return r;
        }

        block.setAndGet(resizer.getMemory().Slice(0, row.Length));
        return Result.SUCCESS;
    }

    /**
     * A function that produces RecordIO record bodies.
     * <p>
     * Record bodies are returned as memory blocks. It is expected that each block is a HybridRow, but any binary data
     * is allowed.
     *
     * @param index The 0-based index of the record within the segment to be produced.
     * @param buffer The byte sequence of the record body's row buffer.
     */
    @FunctionalInterface
    public interface ProduceFunc {
        Result invoke(long index, Out<ReadOnlyMemory<Byte>> buffer);
    }

    /**
     * A function that produces record bodies.
     * <p>
     * Record bodies are returned as memory blocks. It is expected that each block is a HybridRow, but any binary data
     * is allowed. The argument to {#apply} is the 0-based index of the record within the segment to be produced. The
     * {#apply} method produces a {@link ReturnValue} of type {@code byte[]}.
     */
    public interface ValueTask extends Function<Long, ReturnValue<byte[]>> {
    }
}
