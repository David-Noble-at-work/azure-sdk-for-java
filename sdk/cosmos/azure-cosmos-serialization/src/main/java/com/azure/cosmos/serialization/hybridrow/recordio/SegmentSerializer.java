// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serialization.hybridrow.recordio;

import com.azure.cosmos.core.Out;
import com.azure.cosmos.core.UtfAnyString;
import com.azure.cosmos.serialization.hybridrow.HybridRowVersion;
import com.azure.cosmos.serialization.hybridrow.Result;
import com.azure.cosmos.serialization.hybridrow.RowBuffer;
import com.azure.cosmos.serialization.hybridrow.io.RowReader;
import com.azure.cosmos.serialization.hybridrow.io.RowWriter;
import com.azure.cosmos.serialization.hybridrow.io.Segment;
import com.azure.cosmos.serialization.hybridrow.layouts.LayoutResolver;
import com.azure.cosmos.serialization.hybridrow.layouts.TypeArgument;
import io.netty.buffer.ByteBuf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.azure.cosmos.implementation.base.Preconditions.checkState;

/**
 * The type Segment serializer.
 */
public final class SegmentSerializer {

    private static final UtfAnyString COMMENT = new UtfAnyString("comment");
    private static final UtfAnyString LENGTH = new UtfAnyString("length");
    private static final UtfAnyString SDL = new UtfAnyString("sdl");

    /**
     * Read result.
     *
     * @param buffer the buffer
     * @param resolver the resolver
     * @param segment the segment
     *
     * @return the result
     */
    public static Result read(ByteBuf buffer, LayoutResolver resolver, Out<Segment> segment) {
        RowReader reader = new RowReader(new RowBuffer(buffer, HybridRowVersion.V1, resolver));
        return SegmentSerializer.read(reader, segment);
    }

    /**
     * Reads a HybridRow {@link Segment segment}.
     *
     * @param reader the reader from which the segment will be read.
     * @param segment the segment, if the operation is successful.
     *
     * @return {@link Result#SUCCESS}, if the operation is successful; an error {@link Result} otherwise.
     *
     * @throws IllegalStateException if an illegal state is encountered while processing data from {@code reader}.
     */
    public static Result read(RowReader reader, Out<Segment> segment) {

        segment.set(new Segment(null, null));

        final Out<String> comment = new Out<>();
        final Out<Integer> length = new Out<>();
        final Out<String> sdl = new Out<>();

        while (reader.read()) {

            final String path = reader.path().toUtf16();
            final Result result;

            checkState(path != null, "expected non-null path");

            // TODO: Use Path tokens here.

            switch (path) {

                case "length":

                    result = reader.readInt32(length);
                    segment.get().length(length.get());

                    if (result != Result.SUCCESS) {
                        return result;
                    }

                    if (reader.length() < segment.get().length()) {
                        // RowBuffer isn't big enough to contain the rest of the header so just return the length
                        return Result.SUCCESS;
                    }

                    break;

                case "comment":

                    result = reader.readString(comment);
                    segment.get().comment(comment.get());

                    if (result != Result.SUCCESS) {
                        return result;
                    }

                    break;

                case "sdl":

                    result = reader.readString(sdl);
                    segment.get().sdl(sdl.get());

                    if (result != Result.SUCCESS) {
                        return result;
                    }

                    break;

                default:
                    throw new IllegalStateException("Unexpected value: " + reader.path().toUtf16());
            }
        }

        return Result.SUCCESS;
    }

    /**
     * Write result.
     *
     * @param writer the writer
     * @param segment the segment
     * @param typeArgument the type argument
     *
     * @return the result
     */
    public static Result write(
        @NotNull final RowWriter writer,
        @NotNull final Segment segment,
        @Nullable final TypeArgument typeArgument) {

        Result result;

        if (segment.comment() != null) {
            result = writer.writeString(COMMENT, segment.comment());
            if (result != Result.SUCCESS) {
                return result;
            }
        }

        if (segment.sdl() != null) {
            result = writer.writeString(SDL, segment.sdl());
            if (result != Result.SUCCESS) {
                return result;
            }
        }

        // Defer writing the length until all other fields of the segment header are written. The length is then
        // computed based on the current size of the underlying RowBuffer. Because the length field is itself fixed,
        // writing the length can never change the length.

        final int length = writer.length();
        result = writer.writeInt32(LENGTH, length);

        if (result != Result.SUCCESS) {
            return result;
        }

        checkState(length == writer.length(), "expected length == %s, not %s",
            writer.length(),
            length);

        return Result.SUCCESS;
    }
}
