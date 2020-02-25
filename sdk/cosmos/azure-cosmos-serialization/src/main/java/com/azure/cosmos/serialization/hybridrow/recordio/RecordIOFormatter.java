// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serialization.hybridrow.recordio;

import com.azure.cosmos.core.Out;
import com.azure.cosmos.serialization.hybridrow.HybridRowHeader;
import com.azure.cosmos.serialization.hybridrow.HybridRowVersion;
import com.azure.cosmos.serialization.hybridrow.Result;
import com.azure.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.cosmos.serialization.hybridrow.io.RowWriter;
import com.azure.cosmos.serialization.hybridrow.io.Segment;
import com.azure.cosmos.serialization.hybridrow.layouts.Layout;
import com.azure.cosmos.serialization.hybridrow.layouts.SystemSchema;
import io.netty.buffer.ByteBuf;

import org.jetbrains.annotations.NotNull;;
import java.util.zip.CRC32;

import static com.azure.cosmos.base.Preconditions.checkNotNull;

public final class RecordIOFormatter {

    public static final Layout RECORD_LAYOUT = SystemSchema.layoutResolver().resolve(SystemSchema.RECORD_SCHEMA_ID);
    public static final Layout SEGMENT_LAYOUT = SystemSchema.layoutResolver().resolve(SystemSchema.SEGMENT_SCHEMA_ID);

    public static Result formatRecord(
        @NotNull final ByteBuf body,
        @NotNull final Out<RowBuffer> rowBuffer) {

        final int readableBytes = body.readableBytes();
        final CRC32 crc32 = new CRC32();

        if (body.hasArray()) {
            crc32.update(body.array(), body.readerIndex(), body.readableBytes());
        } else {
            body.forEachByte(b -> {
                crc32.update(b);
                return true;
            });
        }

        final int initialCapacity = HybridRowHeader.BYTES + RecordIOFormatter.RECORD_LAYOUT.size() + readableBytes;
        final Record record = new Record(readableBytes, crc32.getValue());

        return formatObject(
            RecordSerializer::write,
            record,
            RecordIOFormatter.RECORD_LAYOUT,
            rowBuffer.setAndGet(new RowBuffer(initialCapacity)));
    }

    public static Result formatSegment(@NotNull final Segment segment, @NotNull final Out<RowBuffer> rowBuffer) {

        checkNotNull(segment, "expected non-null segment");
        checkNotNull(rowBuffer, "expected non-null rowBuffer");

        final String comment = segment.comment();
        final String sdl = segment.sdl();

        int initialCapacity = HybridRowHeader.BYTES + SEGMENT_LAYOUT.size()
            + (comment == null ? 0 : comment.length())
            + (sdl == null ? 0 : sdl.length())
            + 20;

        return formatObject(
            SegmentSerializer::write,
            segment,
            SEGMENT_LAYOUT,
            rowBuffer.setAndGet(new RowBuffer(initialCapacity)));
    }

    private static <T> Result formatObject(
        @NotNull final RowWriter.Writer<T> writer,
        @NotNull final T object,
        @NotNull final Layout layout,
        @NotNull final RowBuffer rowBuffer) {

        Result result = RowWriter.writeBuffer(
            rowBuffer.initLayout(HybridRowVersion.V1, layout, SystemSchema.layoutResolver()),
            object,
            writer);

        if (result != Result.SUCCESS) {
            // TODO (DANOBLE) ensure that ByteBuf does not leak memory
            return result;
        }

        return Result.SUCCESS;
    }
}
