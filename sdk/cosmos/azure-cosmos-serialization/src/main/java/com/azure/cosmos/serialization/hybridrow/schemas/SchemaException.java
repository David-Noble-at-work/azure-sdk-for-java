// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serialization.hybridrow.schemas;

import java.io.Serializable;

/**
 * The type Schema exception.
 */
public final class SchemaException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = -8290759858327074813L;

    /**
     * Instantiates a new Schema exception.
     */
    public SchemaException() {
    }

    /**
     * Instantiates a new Schema exception.
     *
     * @param message the message
     */
    public SchemaException(String message) {
        super(message);
    }

    /**
     * Instantiates a new Schema exception.
     *
     * @param message the message
     * @param innerException the inner exception
     */
    public SchemaException(String message, RuntimeException innerException) {
        super(message, innerException);
    }
}
