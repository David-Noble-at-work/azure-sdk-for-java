// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serialization.hybridrow.recordio;

import com.azure.cosmos.core.Out;
import com.azure.cosmos.core.UtfAnyString;
import com.azure.cosmos.serialization.hybridrow.Result;
import com.azure.cosmos.serialization.hybridrow.io.RowReader;
import com.azure.cosmos.serialization.hybridrow.io.RowWriter;
import com.azure.cosmos.serialization.hybridrow.layouts.TypeArgument;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.azure.cosmos.implementation.base.Preconditions.checkNotNull;
import static com.azure.cosmos.implementation.base.Preconditions.checkState;

/**
 * Provides static read/write operations on a {@link RowReader}.
 */
public final class RecordSerializer {

    private RecordSerializer() {
    }

    // TODO (DANOBLE) Consider moving the methods of this type to the RowReader class.

    /**
     * Reads a HybridRow {@link Record record}.
     *
     * @param reader the reader from which the record will be read.
     * @param record the record, if the operation is successful.
     *
     * @return {@link Result#SUCCESS}, if the operation is successful; an error {@link Result} otherwise.
     *
     * @throws IllegalStateException if an illegal state is encountered while processing data from {@code reader}.
     */
    @NotNull
    public static Result read(@NotNull final RowReader reader, @NotNull final Out<Record> record) {

        Out<Long> value = new Out<>();
        long length = Long.MIN_VALUE;
        long crc32 = Long.MIN_VALUE;

        while (reader.read()) {

            String path = reader.path().toUtf16();
            checkState(path != null);
            Result result;

            // TODO (DANOBLE) Use Path tokens here

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

                    throw new IllegalStateException("unexpected value: " + path);
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

    /**
     * Writes a HybridRow record.
     *
     * @param writer the writer to which the record will be written.
     * @param record the record to be written.
     * @param typeArgument a {@link Nullable nullable} {@link TypeArgument type argument}.
     *
     * @return {@link Result#SUCCESS}, if the operation is successful; an error {@link Result} otherwise.
     */
    @NotNull
    public static Result write(
        @NotNull final RowWriter writer,
        @NotNull final Record record,
        @Nullable final TypeArgument typeArgument) {

        checkNotNull(writer, "expected non-null writer");
        checkNotNull(record, "expected non-null record");

        final Result result = writer.writeInt32(new UtfAnyString("length"), record.length());

        if (result != Result.SUCCESS) {
            return result;
        }

        return writer.writeUInt32(new UtfAnyString("crc32"), record.crc32());
    }
}
