/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 *
 * Code generated by Microsoft (R) AutoRest Code Generator.
 */

package com.microsoft.azure.management.recoveryservices.v2016_06_01;

import com.microsoft.azure.arm.model.HasInner;
import com.microsoft.azure.arm.resources.models.Resource;
import com.microsoft.azure.arm.resources.models.GroupableResourceCore;
import com.microsoft.azure.arm.resources.models.HasResourceGroup;
import com.microsoft.azure.arm.model.Refreshable;
import com.microsoft.azure.arm.model.Updatable;
import com.microsoft.azure.arm.model.Appliable;
import com.microsoft.azure.arm.model.Creatable;
import com.microsoft.azure.arm.resources.models.HasManager;
import com.microsoft.azure.management.recoveryservices.v2016_06_01.implementation.RecoveryServicesManager;
import com.microsoft.azure.management.recoveryservices.v2016_06_01.implementation.VaultInner;

/**
 * Type representing Vault.
 */
public interface Vault extends HasInner<VaultInner>, Resource, GroupableResourceCore<RecoveryServicesManager, VaultInner>, HasResourceGroup, Refreshable<Vault>, Updatable<Vault.Update>, HasManager<RecoveryServicesManager> {
    /**
     * @return the eTag value.
     */
    String eTag();

    /**
     * @return the properties value.
     */
    VaultProperties properties();

    /**
     * @return the sku value.
     */
    Sku sku();

    /**
     * The entirety of the Vault definition.
     */
    interface Definition extends DefinitionStages.Blank, DefinitionStages.WithGroup, DefinitionStages.WithCreate {
    }

    /**
     * Grouping of Vault definition stages.
     */
    interface DefinitionStages {
        /**
         * The first stage of a Vault definition.
         */
        interface Blank extends GroupableResourceCore.DefinitionWithRegion<WithGroup> {
        }

        /**
         * The stage of the Vault definition allowing to specify the resource group.
         */
        interface WithGroup extends GroupableResourceCore.DefinitionStages.WithGroup<WithCreate> {
        }

        /**
         * The stage of the vault definition allowing to specify ETag.
         */
        interface WithETag {
            /**
             * Specifies eTag.
             * @param eTag Optional ETag
             * @return the next definition stage
             */
            WithCreate withETag(String eTag);
        }

        /**
         * The stage of the vault definition allowing to specify Properties.
         */
        interface WithProperties {
            /**
             * Specifies properties.
             * @param properties the properties parameter value
             * @return the next definition stage
             */
            WithCreate withProperties(VaultProperties properties);
        }

        /**
         * The stage of the vault definition allowing to specify Sku.
         */
        interface WithSku {
            /**
             * Specifies sku.
             * @param sku the sku parameter value
             * @return the next definition stage
             */
            WithCreate withSku(Sku sku);
        }

        /**
         * The stage of the definition which contains all the minimum required inputs for
         * the resource to be created (via {@link WithCreate#create()}), but also allows
         * for any other optional settings to be specified.
         */
        interface WithCreate extends Creatable<Vault>, Resource.DefinitionWithTags<WithCreate>, DefinitionStages.WithETag, DefinitionStages.WithProperties, DefinitionStages.WithSku {
        }
    }
    /**
     * The template for a Vault update operation, containing all the settings that can be modified.
     */
    interface Update extends Appliable<Vault>, Resource.UpdateWithTags<Update>, UpdateStages.WithETag, UpdateStages.WithProperties, UpdateStages.WithSku {
    }

    /**
     * Grouping of Vault update stages.
     */
    interface UpdateStages {
        /**
         * The stage of the vault update allowing to specify ETag.
         */
        interface WithETag {
            /**
             * Specifies eTag.
             * @param eTag Optional ETag
             * @return the next update stage
             */
            Update withETag(String eTag);
        }

        /**
         * The stage of the vault update allowing to specify Properties.
         */
        interface WithProperties {
            /**
             * Specifies properties.
             * @param properties the properties parameter value
             * @return the next update stage
             */
            Update withProperties(VaultProperties properties);
        }

        /**
         * The stage of the vault update allowing to specify Sku.
         */
        interface WithSku {
            /**
             * Specifies sku.
             * @param sku the sku parameter value
             * @return the next update stage
             */
            Update withSku(Sku sku);
        }

    }
}
