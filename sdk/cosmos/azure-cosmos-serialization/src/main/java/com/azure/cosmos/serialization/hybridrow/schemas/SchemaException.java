// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serialization.hybridrow.schemas;

import java.io.Serializable;

public final class SchemaException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = -8290759858327074813L;

    public SchemaException() {
    }

    public SchemaException(String message) {
        super(message);
    }

    public SchemaException(String message, RuntimeException innerException) {
        super(message, innerException);
    }
}
