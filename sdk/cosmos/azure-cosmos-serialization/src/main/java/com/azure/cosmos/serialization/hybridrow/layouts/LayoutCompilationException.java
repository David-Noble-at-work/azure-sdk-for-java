// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serialization.hybridrow.layouts;

import java.io.Serializable;

/**
 * The type Layout compilation exception.
 */
public final class LayoutCompilationException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = -4604449873607518058L;

    /**
     * Instantiates a new Layout compilation exception.
     */
    public LayoutCompilationException() {
    }

    /**
     * Instantiates a new Layout compilation exception.
     *
     * @param message the message
     */
    public LayoutCompilationException(String message) {
        super(message);
    }

    /**
     * Instantiates a new Layout compilation exception.
     *
     * @param message the message
     * @param cause the cause
     */
    public LayoutCompilationException(String message, RuntimeException cause) {
        super(message, cause);
    }
}
