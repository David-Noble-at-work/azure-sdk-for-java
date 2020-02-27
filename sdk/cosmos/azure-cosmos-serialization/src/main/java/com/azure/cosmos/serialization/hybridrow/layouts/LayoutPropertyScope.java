// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serialization.hybridrow.layouts;

/**
 * Describes the layout of a PropertyScope.
 */
public abstract class LayoutPropertyScope extends LayoutTypeScope {
    /**
     * Initializes a new PropertyScope layout.
     *
     * @param code the code
     * @param immutable {@code true} if the PropertyScope field is immutable and {@code false}, if it is not.
     */
    protected LayoutPropertyScope(LayoutCode code, boolean immutable) {
        super(code, immutable, false, false, false, false, false);
    }
}
