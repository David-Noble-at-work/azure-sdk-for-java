/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 *
 * Code generated by Microsoft (R) AutoRest Code Generator.
 */

package com.microsoft.azure.management.netapp.v2019_10_01;

import java.util.Collection;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.microsoft.rest.ExpandableStringEnum;

/**
 * Defines values for EndpointType.
 */
public final class EndpointType extends ExpandableStringEnum<EndpointType> {
    /** Static value src for EndpointType. */
    public static final EndpointType SRC = fromString("src");

    /** Static value dst for EndpointType. */
    public static final EndpointType DST = fromString("dst");

    /**
     * Creates or finds a EndpointType from its string representation.
     * @param name a name to look for
     * @return the corresponding EndpointType
     */
    @JsonCreator
    public static EndpointType fromString(String name) {
        return fromString(name, EndpointType.class);
    }

    /**
     * @return known EndpointType values
     */
    public static Collection<EndpointType> values() {
        return values(EndpointType.class);
    }
}
