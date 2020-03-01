// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.batch.unimplemented;

import javax.annotation.Nonnull;

public final class CosmosDiagnosticsContext extends CosmosDiagnostics {

    public CosmosDiagnosticScope createOverallScope(@Nonnull String name) {
        return new CosmosDiagnosticScope();
    }

    public CosmosDiagnosticScope createScope(@Nonnull String name) {
        return new CosmosDiagnosticScope();
    }
}
