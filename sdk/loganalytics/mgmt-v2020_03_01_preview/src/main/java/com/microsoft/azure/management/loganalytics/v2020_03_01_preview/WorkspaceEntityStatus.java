/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 *
 * Code generated by Microsoft (R) AutoRest Code Generator.
 */

package com.microsoft.azure.management.loganalytics.v2020_03_01_preview;

import java.util.Collection;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.microsoft.rest.ExpandableStringEnum;

/**
 * Defines values for WorkspaceEntityStatus.
 */
public final class WorkspaceEntityStatus extends ExpandableStringEnum<WorkspaceEntityStatus> {
    /** Static value Creating for WorkspaceEntityStatus. */
    public static final WorkspaceEntityStatus CREATING = fromString("Creating");

    /** Static value Succeeded for WorkspaceEntityStatus. */
    public static final WorkspaceEntityStatus SUCCEEDED = fromString("Succeeded");

    /** Static value Failed for WorkspaceEntityStatus. */
    public static final WorkspaceEntityStatus FAILED = fromString("Failed");

    /** Static value Canceled for WorkspaceEntityStatus. */
    public static final WorkspaceEntityStatus CANCELED = fromString("Canceled");

    /** Static value Deleting for WorkspaceEntityStatus. */
    public static final WorkspaceEntityStatus DELETING = fromString("Deleting");

    /** Static value ProvisioningAccount for WorkspaceEntityStatus. */
    public static final WorkspaceEntityStatus PROVISIONING_ACCOUNT = fromString("ProvisioningAccount");

    /** Static value Updating for WorkspaceEntityStatus. */
    public static final WorkspaceEntityStatus UPDATING = fromString("Updating");

    /**
     * Creates or finds a WorkspaceEntityStatus from its string representation.
     * @param name a name to look for
     * @return the corresponding WorkspaceEntityStatus
     */
    @JsonCreator
    public static WorkspaceEntityStatus fromString(String name) {
        return fromString(name, WorkspaceEntityStatus.class);
    }

    /**
     * @return known WorkspaceEntityStatus values
     */
    public static Collection<WorkspaceEntityStatus> values() {
        return values(WorkspaceEntityStatus.class);
    }
}
