// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.azure.cosmos.batch.unimplemented;

public class CosmosDiagnosticsContext {

    public CosmosDiagnosticScope createOverallScope(String name) {
        return new CosmosDiagnosticScope();
    }

    public CosmosDiagnosticScope createScope(String name) {
        return new CosmosDiagnosticScope();
    }
}
