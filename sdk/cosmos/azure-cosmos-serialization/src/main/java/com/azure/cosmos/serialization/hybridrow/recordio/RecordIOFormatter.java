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

import javax.annotation.Nonnull;
import java.util.zip.CRC32;

public final class RecordIOFormatter {

    public static final Layout RECORD_LAYOUT = SystemSchema.layoutResolver().resolve(SystemSchema.RECORD_SCHEMA_ID);
    public static final Layout SEGMENT_LAYOUT = SystemSchema.layoutResolver().resolve(SystemSchema.SEGMENT_SCHEMA_ID);

    public static Result FormatRecord(
        @Nonnull ByteBuf body,
        RowBuffer rowBuffer) {

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

        Record record = new Record(readableBytes, crc32.getValue());
        final int estimatedSize = HybridRowHeader.BYTES + RecordIOFormatter.RECORD_LAYOUT.size() + readableBytes;

        return RecordIOFormatter.FormatObject(
            body,
            estimatedSize,
            RecordIOFormatter.RECORD_LAYOUT,
            record,
            RecordSerializer::write,
            rowBuffer);
    }

    public static Result FormatSegment(Segment segment, RowBuffer rowBuffer) {
        return FormatSegment(segment, rowBuffer);
    }

    public static Result FormatSegment(Segment segment, Out<RowBuffer> row, ISpanResizer<Byte> resizer) {
        //C# TO JAVA CONVERTER WARNING: Unsigned integer types have no direct equivalent in Java:
        //ORIGINAL LINE: resizer = resizer != null ? resizer : DefaultSpanResizer<byte>.Default;
        resizer = resizer != null ? resizer : DefaultSpanResizer < Byte >.Default;
        int estimatedSize =
            HybridRowHeader.BYTES + RecordIOFormatter.SEGMENT_LAYOUT.getSize() + segment.comment() == null ? null :
                segment.comment().length() != null ? segment.comment().length() : 0 + segment.sdl() == null ? null :
                    segment.sdl().length() != null ? segment.sdl().length() : 0 + 20;

        return RecordIOFormatter.FormatObject(resizer, estimatedSize, RecordIOFormatter.SEGMENT_LAYOUT,
            segment.clone(), SegmentSerializer.Write, row.clone());
    }

    private static <T> Result FormatObject(
        RowWriter.WriterFunc<T> writer,
        int initialCapacity,
        Layout layout,
        T object,
        Out<RowBuffer> rowBuffer) {

        Result result = RowWriter.writeBuffer(
            rowBuffer.setAndGet(new RowBuffer(initialCapacity)).initLayout(
                HybridRowVersion.V1,
                layout,
                SystemSchema.layoutResolver()),
            object, writer);

        if (result != Result.SUCCESS) {
            // TODO (DANOBLE) ensure that ByteBuf does not leak memory
            rowBuffer.set(null);
            return result;
        }

        return Result.SUCCESS;
    }
}
