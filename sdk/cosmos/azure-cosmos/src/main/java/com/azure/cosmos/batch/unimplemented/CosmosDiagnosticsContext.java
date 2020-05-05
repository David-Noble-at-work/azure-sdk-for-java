// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.batch.unimplemented;

import org.jetbrains.annotations.NotNull;

public final class CosmosDiagnosticsContext extends CosmosDiagnostics {

    public CosmosDiagnosticScope createOverallScope(@NotNull String name) {
        return new CosmosDiagnosticScope();
    }

    public CosmosDiagnosticScope createScope(@NotNull String name) {
        return new CosmosDiagnosticScope();
    }
}
