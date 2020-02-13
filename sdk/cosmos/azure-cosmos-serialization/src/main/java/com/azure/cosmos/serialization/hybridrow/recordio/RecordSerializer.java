// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serialization.hybridrow.recordio;

import com.azure.cosmos.core.Out;
import com.azure.cosmos.core.UtfAnyString;
import com.azure.cosmos.serialization.hybridrow.Result;
import com.azure.cosmos.serialization.hybridrow.io.RowReader;
import com.azure.cosmos.serialization.hybridrow.io.RowWriter;
import com.azure.cosmos.serialization.hybridrow.layouts.TypeArgument;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.azure.cosmos.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public final class RecordSerializer {

    @Nonnull
    public static Result read(@Nonnull final RowReader reader, @Nonnull final Out<Record> record) {

        Out<Long> value = new Out<>();
        long length = Long.MIN_VALUE;
        long crc32 = Long.MIN_VALUE;

        while (reader.read()) {

            String path = reader.path().toUtf16();
            checkState(path != null);
            Result result;

            // TODO: use Path tokens here

            switch (path) {

                case "length":

                    result = reader.readUInt32(value);

                    if (result != Result.SUCCESS) {
                        return result;
                    }

                    length = value.get();
                    break;

                case "crc32":

                    result = reader.readUInt32(value);

                    if (result != Result.SUCCESS) {
                        return result;
                    }

                    crc32 = value.get();
                    break;

                default:

                    throw new IllegalStateException("Unexpected value: " + path);
            }
        }

        checkState(0 <= length && length <= Integer.MAX_VALUE, "expected 0 <= length && length <= %s, not %s",
            Integer.MAX_VALUE,
            length);

        checkState(0 <= crc32 && crc32 <= 0xFFFFFFFFL, "expected 0 <= crc32 && crc32 <= %s",
            0xFFFFFFFFL,
            crc32);

        record.set(new Record((int) length, crc32));
        return Result.SUCCESS;
    }

    @Nonnull
    public static Result write(
        @Nonnull final RowWriter writer,
        @Nullable final TypeArgument typeArg,
        @Nonnull final Record record) {

        checkNotNull(writer, "expected non-null writer");
        checkNotNull(record, "expected non-null record");

        final Result result = writer.writeInt32(new UtfAnyString("length"), record.length());

        if (result != Result.SUCCESS) {
            return result;
        }

        return writer.writeUInt32(new UtfAnyString("crc32"), record.crc32());
    }
}
