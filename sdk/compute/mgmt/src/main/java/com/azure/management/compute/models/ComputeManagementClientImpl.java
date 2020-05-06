// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.management.compute.models;

import com.azure.core.http.HttpPipeline;
import com.azure.core.http.HttpPipelineBuilder;
import com.azure.core.http.policy.CookiePolicy;
import com.azure.core.http.policy.RetryPolicy;
import com.azure.core.http.policy.UserAgentPolicy;
import com.azure.core.management.AzureEnvironment;
import com.azure.management.AzureServiceClient;

/** Initializes a new instance of the ComputeManagementClientImpl type. */
public final class ComputeManagementClientImpl extends AzureServiceClient {
    /**
     * Subscription credentials which uniquely identify Microsoft Azure subscription. The subscription ID forms part of
     * the URI for every service call.
     */
    private String subscriptionId;

    /**
     * Gets Subscription credentials which uniquely identify Microsoft Azure subscription. The subscription ID forms
     * part of the URI for every service call.
     *
     * @return the subscriptionId value.
     */
    public String getSubscriptionId() {
        return this.subscriptionId;
    }

    /**
     * Sets Subscription credentials which uniquely identify Microsoft Azure subscription. The subscription ID forms
     * part of the URI for every service call.
     *
     * @param subscriptionId the subscriptionId value.
     * @return the service client itself.
     */
    public ComputeManagementClientImpl setSubscriptionId(String subscriptionId) {
        this.subscriptionId = subscriptionId;
        return this;
    }

    /** server parameter. */
    private String host;

    /**
     * Gets server parameter.
     *
     * @return the host value.
     */
    public String getHost() {
        return this.host;
    }

    /**
     * Sets server parameter.
     *
     * @param host the host value.
     * @return the service client itself.
     */
    public ComputeManagementClientImpl setHost(String host) {
        this.host = host;
        return this;
    }

    /** The HTTP pipeline to send requests through. */
    private final HttpPipeline httpPipeline;

    /**
     * Gets The HTTP pipeline to send requests through.
     *
     * @return the httpPipeline value.
     */
    public HttpPipeline getHttpPipeline() {
        return this.httpPipeline;
    }

    /** The OperationsInner object to access its operations. */
    private final OperationsInner operations;

    /**
     * Gets the OperationsInner object to access its operations.
     *
     * @return the OperationsInner object.
     */
    public OperationsInner operations() {
        return this.operations;
    }

    /** The AvailabilitySetsInner object to access its operations. */
    private final AvailabilitySetsInner availabilitySets;

    /**
     * Gets the AvailabilitySetsInner object to access its operations.
     *
     * @return the AvailabilitySetsInner object.
     */
    public AvailabilitySetsInner availabilitySets() {
        return this.availabilitySets;
    }

    /** The ProximityPlacementGroupsInner object to access its operations. */
    private final ProximityPlacementGroupsInner proximityPlacementGroups;

    /**
     * Gets the ProximityPlacementGroupsInner object to access its operations.
     *
     * @return the ProximityPlacementGroupsInner object.
     */
    public ProximityPlacementGroupsInner proximityPlacementGroups() {
        return this.proximityPlacementGroups;
    }

    /** The DedicatedHostGroupsInner object to access its operations. */
    private final DedicatedHostGroupsInner dedicatedHostGroups;

    /**
     * Gets the DedicatedHostGroupsInner object to access its operations.
     *
     * @return the DedicatedHostGroupsInner object.
     */
    public DedicatedHostGroupsInner dedicatedHostGroups() {
        return this.dedicatedHostGroups;
    }

    /** The DedicatedHostsInner object to access its operations. */
    private final DedicatedHostsInner dedicatedHosts;

    /**
     * Gets the DedicatedHostsInner object to access its operations.
     *
     * @return the DedicatedHostsInner object.
     */
    public DedicatedHostsInner dedicatedHosts() {
        return this.dedicatedHosts;
    }

    /** The VirtualMachineExtensionImagesInner object to access its operations. */
    private final VirtualMachineExtensionImagesInner virtualMachineExtensionImages;

    /**
     * Gets the VirtualMachineExtensionImagesInner object to access its operations.
     *
     * @return the VirtualMachineExtensionImagesInner object.
     */
    public VirtualMachineExtensionImagesInner virtualMachineExtensionImages() {
        return this.virtualMachineExtensionImages;
    }

    /** The VirtualMachineExtensionsInner object to access its operations. */
    private final VirtualMachineExtensionsInner virtualMachineExtensions;

    /**
     * Gets the VirtualMachineExtensionsInner object to access its operations.
     *
     * @return the VirtualMachineExtensionsInner object.
     */
    public VirtualMachineExtensionsInner virtualMachineExtensions() {
        return this.virtualMachineExtensions;
    }

    /** The VirtualMachineImagesInner object to access its operations. */
    private final VirtualMachineImagesInner virtualMachineImages;

    /**
     * Gets the VirtualMachineImagesInner object to access its operations.
     *
     * @return the VirtualMachineImagesInner object.
     */
    public VirtualMachineImagesInner virtualMachineImages() {
        return this.virtualMachineImages;
    }

    /** The UsagesInner object to access its operations. */
    private final UsagesInner usages;

    /**
     * Gets the UsagesInner object to access its operations.
     *
     * @return the UsagesInner object.
     */
    public UsagesInner usages() {
        return this.usages;
    }

    /** The VirtualMachinesInner object to access its operations. */
    private final VirtualMachinesInner virtualMachines;

    /**
     * Gets the VirtualMachinesInner object to access its operations.
     *
     * @return the VirtualMachinesInner object.
     */
    public VirtualMachinesInner virtualMachines() {
        return this.virtualMachines;
    }

    /** The VirtualMachineSizesInner object to access its operations. */
    private final VirtualMachineSizesInner virtualMachineSizes;

    /**
     * Gets the VirtualMachineSizesInner object to access its operations.
     *
     * @return the VirtualMachineSizesInner object.
     */
    public VirtualMachineSizesInner virtualMachineSizes() {
        return this.virtualMachineSizes;
    }

    /** The ImagesInner object to access its operations. */
    private final ImagesInner images;

    /**
     * Gets the ImagesInner object to access its operations.
     *
     * @return the ImagesInner object.
     */
    public ImagesInner images() {
        return this.images;
    }

    /** The VirtualMachineScaleSetsInner object to access its operations. */
    private final VirtualMachineScaleSetsInner virtualMachineScaleSets;

    /**
     * Gets the VirtualMachineScaleSetsInner object to access its operations.
     *
     * @return the VirtualMachineScaleSetsInner object.
     */
    public VirtualMachineScaleSetsInner virtualMachineScaleSets() {
        return this.virtualMachineScaleSets;
    }

    /** The VirtualMachineScaleSetExtensionsInner object to access its operations. */
    private final VirtualMachineScaleSetExtensionsInner virtualMachineScaleSetExtensions;

    /**
     * Gets the VirtualMachineScaleSetExtensionsInner object to access its operations.
     *
     * @return the VirtualMachineScaleSetExtensionsInner object.
     */
    public VirtualMachineScaleSetExtensionsInner virtualMachineScaleSetExtensions() {
        return this.virtualMachineScaleSetExtensions;
    }

    /** The VirtualMachineScaleSetRollingUpgradesInner object to access its operations. */
    private final VirtualMachineScaleSetRollingUpgradesInner virtualMachineScaleSetRollingUpgrades;

    /**
     * Gets the VirtualMachineScaleSetRollingUpgradesInner object to access its operations.
     *
     * @return the VirtualMachineScaleSetRollingUpgradesInner object.
     */
    public VirtualMachineScaleSetRollingUpgradesInner virtualMachineScaleSetRollingUpgrades() {
        return this.virtualMachineScaleSetRollingUpgrades;
    }

    /** The VirtualMachineScaleSetVMsInner object to access its operations. */
    private final VirtualMachineScaleSetVMsInner virtualMachineScaleSetVMs;

    /**
     * Gets the VirtualMachineScaleSetVMsInner object to access its operations.
     *
     * @return the VirtualMachineScaleSetVMsInner object.
     */
    public VirtualMachineScaleSetVMsInner virtualMachineScaleSetVMs() {
        return this.virtualMachineScaleSetVMs;
    }

    /** The LogAnalyticsInner object to access its operations. */
    private final LogAnalyticsInner logAnalytics;

    /**
     * Gets the LogAnalyticsInner object to access its operations.
     *
     * @return the LogAnalyticsInner object.
     */
    public LogAnalyticsInner logAnalytics() {
        return this.logAnalytics;
    }

    /** The VirtualMachineRunCommandsInner object to access its operations. */
    private final VirtualMachineRunCommandsInner virtualMachineRunCommands;

    /**
     * Gets the VirtualMachineRunCommandsInner object to access its operations.
     *
     * @return the VirtualMachineRunCommandsInner object.
     */
    public VirtualMachineRunCommandsInner virtualMachineRunCommands() {
        return this.virtualMachineRunCommands;
    }

    /** The ResourceSkusInner object to access its operations. */
    private final ResourceSkusInner resourceSkus;

    /**
     * Gets the ResourceSkusInner object to access its operations.
     *
     * @return the ResourceSkusInner object.
     */
    public ResourceSkusInner resourceSkus() {
        return this.resourceSkus;
    }

    /** The DisksInner object to access its operations. */
    private final DisksInner disks;

    /**
     * Gets the DisksInner object to access its operations.
     *
     * @return the DisksInner object.
     */
    public DisksInner disks() {
        return this.disks;
    }

    /** The SnapshotsInner object to access its operations. */
    private final SnapshotsInner snapshots;

    /**
     * Gets the SnapshotsInner object to access its operations.
     *
     * @return the SnapshotsInner object.
     */
    public SnapshotsInner snapshots() {
        return this.snapshots;
    }

    /** The GalleriesInner object to access its operations. */
    private final GalleriesInner galleries;

    /**
     * Gets the GalleriesInner object to access its operations.
     *
     * @return the GalleriesInner object.
     */
    public GalleriesInner galleries() {
        return this.galleries;
    }

    /** The GalleryImagesInner object to access its operations. */
    private final GalleryImagesInner galleryImages;

    /**
     * Gets the GalleryImagesInner object to access its operations.
     *
     * @return the GalleryImagesInner object.
     */
    public GalleryImagesInner galleryImages() {
        return this.galleryImages;
    }

    /** The GalleryImageVersionsInner object to access its operations. */
    private final GalleryImageVersionsInner galleryImageVersions;

    /**
     * Gets the GalleryImageVersionsInner object to access its operations.
     *
     * @return the GalleryImageVersionsInner object.
     */
    public GalleryImageVersionsInner galleryImageVersions() {
        return this.galleryImageVersions;
    }

    /** The GalleryApplicationsInner object to access its operations. */
    private final GalleryApplicationsInner galleryApplications;

    /**
     * Gets the GalleryApplicationsInner object to access its operations.
     *
     * @return the GalleryApplicationsInner object.
     */
    public GalleryApplicationsInner galleryApplications() {
        return this.galleryApplications;
    }

    /** The GalleryApplicationVersionsInner object to access its operations. */
    private final GalleryApplicationVersionsInner galleryApplicationVersions;

    /**
     * Gets the GalleryApplicationVersionsInner object to access its operations.
     *
     * @return the GalleryApplicationVersionsInner object.
     */
    public GalleryApplicationVersionsInner galleryApplicationVersions() {
        return this.galleryApplicationVersions;
    }

    /** The ContainerServicesInner object to access its operations. */
    private final ContainerServicesInner containerServices;

    /**
     * Gets the ContainerServicesInner object to access its operations.
     *
     * @return the ContainerServicesInner object.
     */
    public ContainerServicesInner containerServices() {
        return this.containerServices;
    }

    /** Initializes an instance of ComputeManagementClient client. */
    public ComputeManagementClientImpl() {
        this(
            new HttpPipelineBuilder().policies(new UserAgentPolicy(), new RetryPolicy(), new CookiePolicy()).build(),
            AzureEnvironment.AZURE);
    }

    /**
     * Initializes an instance of ComputeManagementClient client.
     *
     * @param httpPipeline The HTTP pipeline to send requests through.
     */
    public ComputeManagementClientImpl(HttpPipeline httpPipeline) {
        this(httpPipeline, AzureEnvironment.AZURE);
    }

    /**
     * Initializes an instance of ComputeManagementClient client.
     *
     * @param httpPipeline The HTTP pipeline to send requests through.
     * @param environment The Azure environment.
     */
    public ComputeManagementClientImpl(HttpPipeline httpPipeline, AzureEnvironment environment) {
        super(httpPipeline, environment);
        this.httpPipeline = httpPipeline;
        this.operations = new OperationsInner(this);
        this.availabilitySets = new AvailabilitySetsInner(this);
        this.proximityPlacementGroups = new ProximityPlacementGroupsInner(this);
        this.dedicatedHostGroups = new DedicatedHostGroupsInner(this);
        this.dedicatedHosts = new DedicatedHostsInner(this);
        this.virtualMachineExtensionImages = new VirtualMachineExtensionImagesInner(this);
        this.virtualMachineExtensions = new VirtualMachineExtensionsInner(this);
        this.virtualMachineImages = new VirtualMachineImagesInner(this);
        this.usages = new UsagesInner(this);
        this.virtualMachines = new VirtualMachinesInner(this);
        this.virtualMachineSizes = new VirtualMachineSizesInner(this);
        this.images = new ImagesInner(this);
        this.virtualMachineScaleSets = new VirtualMachineScaleSetsInner(this);
        this.virtualMachineScaleSetExtensions = new VirtualMachineScaleSetExtensionsInner(this);
        this.virtualMachineScaleSetRollingUpgrades = new VirtualMachineScaleSetRollingUpgradesInner(this);
        this.virtualMachineScaleSetVMs = new VirtualMachineScaleSetVMsInner(this);
        this.logAnalytics = new LogAnalyticsInner(this);
        this.virtualMachineRunCommands = new VirtualMachineRunCommandsInner(this);
        this.resourceSkus = new ResourceSkusInner(this);
        this.disks = new DisksInner(this);
        this.snapshots = new SnapshotsInner(this);
        this.galleries = new GalleriesInner(this);
        this.galleryImages = new GalleryImagesInner(this);
        this.galleryImageVersions = new GalleryImageVersionsInner(this);
        this.galleryApplications = new GalleryApplicationsInner(this);
        this.galleryApplicationVersions = new GalleryApplicationVersionsInner(this);
        this.containerServices = new ContainerServicesInner(this);
    }
}
