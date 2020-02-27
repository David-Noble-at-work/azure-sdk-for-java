// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serialization.hybridrow.io;

/**
 * The type Segment.
 */
public final class Segment {

    private String comment;
    private int length;
    private String sdl;

    /**
     * Instantiates a new Segment.
     *
     * @param comment the comment
     * @param sdl the sdl
     */
    public Segment(String comment, String sdl) {
        this.comment = comment;
        this.sdl = sdl;
        this.length = 0;
    }

    /**
     * Comment string.
     *
     * @return the string
     */
    public String comment() {
        return this.comment;
    }

    /**
     * Comment segment.
     *
     * @param value the value
     *
     * @return the segment
     */
    public Segment comment(String value) {
        this.comment = value;
        return this;
    }

    /**
     * Length int.
     *
     * @return the int
     */
    public int length() {
        return this.length;
    }

    /**
     * Length segment.
     *
     * @param value the value
     *
     * @return the segment
     */
    public Segment length(int value) {
        this.length = value;
        return this;
    }

    /**
     * Sdl string.
     *
     * @return the string
     */
    public String sdl() {
        return this.sdl;
    }

    /**
     * Sdl segment.
     *
     * @param value the value
     *
     * @return the segment
     */
    public Segment sdl(String value) {
        this.sdl = value;
        return this;
    }
}
