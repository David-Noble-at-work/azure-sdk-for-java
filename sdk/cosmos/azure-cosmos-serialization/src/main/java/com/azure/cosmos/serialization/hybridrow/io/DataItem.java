// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.serialization.hybridrow.io;

import com.azure.cosmos.base.Suppliers;
import com.azure.cosmos.core.Json;
import com.azure.cosmos.core.Utf8String;
import com.azure.cosmos.serialization.hybridrow.layouts.LayoutCode;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

import static com.azure.cosmos.base.Preconditions.checkNotNull;

/**
 * A path/type/value triplet representing a field in a HybridRow.
 */
public class DataItem {

    private final Supplier<String> name;
    @JsonProperty
    private final List<String> nodes;
    private final Supplier<String> path;
    @JsonProperty
    private final LayoutCode type;
    @JsonProperty
    private final Object value;

    /**
     * Instantiates a new Data item.
     *
     * @param nodes the nodes
     * @param name the name
     * @param type the type
     * @param value the value
     */
    @SuppressWarnings("UnstableApiUsage")
    DataItem(
        @NotNull final Collection<Utf8String> nodes,
        @NotNull final Utf8String name,
        @NotNull final LayoutCode type,
        @NotNull final Object value) {

        checkNotNull(nodes, "expected non-null nodes");
        checkNotNull(name, "expected non-null name");
        checkNotNull(type, "expected non-null type");
        checkNotNull(value, "expected non-null value");

        //noinspection ConstantConditions
        this.nodes = ImmutableList.<String>builderWithExpectedSize(nodes.size() + 1)
            .addAll(nodes.stream().map(Utf8String::toUtf16).iterator())
            .add(name.toUtf16())
            .build();

        this.type = type;
        this.value = value;

        this.name = Suppliers.memoize(() -> this.nodes.get(this.nodes.size() - 1));

        this.path = Suppliers.memoize(() -> {

            if (this.nodes.size() == 1) {
                return this.nodes.get(0);
            }

            StringBuilder builder = new StringBuilder(this.nodes.stream()
                .map(String::length)
                .reduce(this.nodes.size() - 1, Integer::sum)
            );

            int i;

            for (i = 0; i < this.nodes.size() - 1; ++i) {
                builder.append(this.nodes.get(i));
                if (this.nodes.get(i + 1).charAt(0) != '[') {
                    builder.append('.');
                }
            }

            return builder.append(this.nodes.get(i)).toString();
        });
    }

    /**
     * Name string.
     *
     * @return the string
     */
    public String name() {
        return this.name.get();
    }

    /**
     * Nodes list.
     *
     * @return the list
     */
    public List<String> nodes() {
        return this.nodes;
    }

    /**
     * Path string.
     *
     * @return the string
     */
    public String path() {
        return this.path.get();
    }

    @Override
    public String toString() {
        return Json.toString(this);
    }

    /**
     * Type layout code.
     *
     * @return the layout code
     */
    public LayoutCode type() {
        return this.type;
    }

    /**
     * Value object.
     *
     * @return the object
     */
    public Object value() {
        return this.value;
    }
}
