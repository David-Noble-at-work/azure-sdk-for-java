// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serialization.hybridrow.layouts;

import java.io.Serializable;

public final class LayoutCompilationException extends RuntimeException implements Serializable {

    private static final long serialVersionUID = -4604449873607518058L;

    public LayoutCompilationException() {
    }

    public LayoutCompilationException(String message) {
        super(message);
    }

    public LayoutCompilationException(String message, RuntimeException cause) {
        super(message, cause);
    }
}
