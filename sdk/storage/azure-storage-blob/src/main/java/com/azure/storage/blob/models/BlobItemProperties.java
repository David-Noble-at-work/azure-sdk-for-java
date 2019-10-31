// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.storage.blob.models;

import com.azure.core.annotation.Fluent;
import com.azure.core.util.DateTimeRfc1123;
import com.azure.core.util.GeneralUtils;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.time.OffsetDateTime;

/**
 * Properties of a blob.
 */
@JacksonXmlRootElement(localName = "Properties")
@Fluent
public final class BlobItemProperties {
    /*
     * The creationTime property.
     */
    @JsonProperty(value = "Creation-Time")
    private DateTimeRfc1123 creationTime;

    /*
     * The lastModified property.
     */
    @JsonProperty(value = "Last-Modified", required = true)
    private DateTimeRfc1123 lastModified;

    /*
     * The eTag property.
     */
    @JsonProperty(value = "Etag", required = true)
    private String eTag;

    /*
     * Size in bytes
     */
    @JsonProperty(value = "Content-Length")
    private Long contentLength;

    /*
     * The contentType property.
     */
    @JsonProperty(value = "Content-Type")
    private String contentType;

    /*
     * The contentEncoding property.
     */
    @JsonProperty(value = "Content-Encoding")
    private String contentEncoding;

    /*
     * The contentLanguage property.
     */
    @JsonProperty(value = "Content-Language")
    private String contentLanguage;

    /*
     * The contentMd5 property.
     */
    @JsonProperty(value = "Content-MD5")
    private byte[] contentMd5;

    /*
     * The contentDisposition property.
     */
    @JsonProperty(value = "Content-Disposition")
    private String contentDisposition;

    /*
     * The cacheControl property.
     */
    @JsonProperty(value = "Cache-Control")
    private String cacheControl;

    /*
     * The blobSequenceNumber property.
     */
    @JsonProperty(value = "x-ms-blob-sequence-number")
    private Long blobSequenceNumber;

    /*
     * Possible values include: 'BlockBlob', 'PageBlob', 'AppendBlob'
     */
    @JsonProperty(value = "BlobType")
    private BlobType blobType;

    /*
     * Possible values include: 'locked', 'unlocked'
     */
    @JsonProperty(value = "LeaseStatus")
    private LeaseStatusType leaseStatus;

    /*
     * Possible values include: 'available', 'leased', 'expired', 'breaking',
     * 'broken'
     */
    @JsonProperty(value = "LeaseState")
    private LeaseStateType leaseState;

    /*
     * Possible values include: 'infinite', 'fixed'
     */
    @JsonProperty(value = "LeaseDuration")
    private LeaseDurationType leaseDuration;

    /*
     * The copyId property.
     */
    @JsonProperty(value = "CopyId")
    private String copyId;

    /*
     * Possible values include: 'pending', 'success', 'aborted', 'failed'
     */
    @JsonProperty(value = "CopyStatus")
    private CopyStatusType copyStatus;

    /*
     * The copySource property.
     */
    @JsonProperty(value = "CopySource")
    private String copySource;

    /*
     * The copyProgress property.
     */
    @JsonProperty(value = "CopyProgress")
    private String copyProgress;

    /*
     * The copyCompletionTime property.
     */
    @JsonProperty(value = "CopyCompletionTime")
    private DateTimeRfc1123 copyCompletionTime;

    /*
     * The copyStatusDescription property.
     */
    @JsonProperty(value = "CopyStatusDescription")
    private String copyStatusDescription;

    /*
     * The serverEncrypted property.
     */
    @JsonProperty(value = "ServerEncrypted")
    private Boolean serverEncrypted;

    /*
     * The incrementalCopy property.
     */
    @JsonProperty(value = "IncrementalCopy")
    private Boolean incrementalCopy;

    /*
     * The destinationSnapshot property.
     */
    @JsonProperty(value = "DestinationSnapshot")
    private String destinationSnapshot;

    /*
     * The deletedTime property.
     */
    @JsonProperty(value = "DeletedTime")
    private DateTimeRfc1123 deletedTime;

    /*
     * The remainingRetentionDays property.
     */
    @JsonProperty(value = "RemainingRetentionDays")
    private Integer remainingRetentionDays;

    /*
     * Possible values include: 'P4', 'P6', 'P10', 'P15', 'P20', 'P30', 'P40',
     * 'P50', 'P60', 'P70', 'P80', 'Hot', 'Cool', 'Archive'
     */
    @JsonProperty(value = "AccessTier")
    private AccessTier accessTier;

    /*
     * The accessTierInferred property.
     */
    @JsonProperty(value = "AccessTierInferred")
    private Boolean accessTierInferred;

    /*
     * Possible values include: 'rehydrate-pending-to-hot',
     * 'rehydrate-pending-to-cool'
     */
    @JsonProperty(value = "ArchiveStatus")
    private ArchiveStatus archiveStatus;

    /*
     * The customerProvidedKeySha256 property.
     */
    @JsonProperty(value = "CustomerProvidedKeySha256")
    private String customerProvidedKeySha256;

    /*
     * The accessTierChangeTime property.
     */
    @JsonProperty(value = "AccessTierChangeTime")
    private DateTimeRfc1123 accessTierChangeTime;

    /**
     * Get the creationTime property: The creationTime property.
     *
     * @return the creationTime value.
     */
    public OffsetDateTime getCreationTime() {
        if (this.creationTime == null) {
            return null;
        }
        return this.creationTime.getDateTime();
    }

    /**
     * Set the creationTime property: The creationTime property.
     *
     * @param creationTime the creationTime value to set.
     * @return the BlobItemProperties object itself.
     */
    public BlobItemProperties setCreationTime(OffsetDateTime creationTime) {
        if (creationTime == null) {
            this.creationTime = null;
        } else {
            this.creationTime = new DateTimeRfc1123(creationTime);
        }
        return this;
    }

    /**
     * Get the lastModified property: The lastModified property.
     *
     * @return the lastModified value.
     */
    public OffsetDateTime getLastModified() {
        if (this.lastModified == null) {
            return null;
        }
        return this.lastModified.getDateTime();
    }

    /**
     * Set the lastModified property: The lastModified property.
     *
     * @param lastModified the lastModified value to set.
     * @return the BlobItemProperties object itself.
     */
    public BlobItemProperties setLastModified(OffsetDateTime lastModified) {
        if (lastModified == null) {
            this.lastModified = null;
        } else {
            this.lastModified = new DateTimeRfc1123(lastModified);
        }
        return this;
    }

    /**
     * Get the eTag property: The eTag property.
     *
     * @return the eTag value.
     */
    public String getETag() {
        return this.eTag;
    }

    /**
     * Set the eTag property: The eTag property.
     *
     * @param eTag the eTag value to set.
     * @return the BlobItemProperties object itself.
     */
    public BlobItemProperties setETag(String eTag) {
        this.eTag = eTag;
        return this;
    }

    /**
     * Get the contentLength property: Size in bytes.
     *
     * @return the contentLength value.
     */
    public Long getContentLength() {
        return this.contentLength;
    }

    /**
     * Set the contentLength property: Size in bytes.
     *
     * @param contentLength the contentLength value to set.
     * @return the BlobItemProperties object itself.
     */
    public BlobItemProperties setContentLength(Long contentLength) {
        this.contentLength = contentLength;
        return this;
    }

    /**
     * Get the contentType property: The contentType property.
     *
     * @return the contentType value.
     */
    public String getContentType() {
        return this.contentType;
    }

    /**
     * Set the contentType property: The contentType property.
     *
     * @param contentType the contentType value to set.
     * @return the BlobItemProperties object itself.
     */
    public BlobItemProperties setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    /**
     * Get the contentEncoding property: The contentEncoding property.
     *
     * @return the contentEncoding value.
     */
    public String getContentEncoding() {
        return this.contentEncoding;
    }

    /**
     * Set the contentEncoding property: The contentEncoding property.
     *
     * @param contentEncoding the contentEncoding value to set.
     * @return the BlobItemProperties object itself.
     */
    public BlobItemProperties setContentEncoding(String contentEncoding) {
        this.contentEncoding = contentEncoding;
        return this;
    }

    /**
     * Get the contentLanguage property: The contentLanguage property.
     *
     * @return the contentLanguage value.
     */
    public String getContentLanguage() {
        return this.contentLanguage;
    }

    /**
     * Set the contentLanguage property: The contentLanguage property.
     *
     * @param contentLanguage the contentLanguage value to set.
     * @return the BlobItemProperties object itself.
     */
    public BlobItemProperties setContentLanguage(String contentLanguage) {
        this.contentLanguage = contentLanguage;
        return this;
    }

    /**
     * Get the contentMd5 property: The contentMd5 property.
     *
     * @return the contentMd5 value.
     */
    public byte[] getContentMd5() {
        return GeneralUtils.clone(this.contentMd5);
    }

    /**
     * Set the contentMd5 property: The contentMd5 property.
     *
     * @param contentMd5 the contentMd5 value to set.
     * @return the BlobItemProperties object itself.
     */
    public BlobItemProperties setContentMd5(byte[] contentMd5) {
        this.contentMd5 = GeneralUtils.clone(contentMd5);
        return this;
    }

    /**
     * Get the contentDisposition property: The contentDisposition property.
     *
     * @return the contentDisposition value.
     */
    public String getContentDisposition() {
        return this.contentDisposition;
    }

    /**
     * Set the contentDisposition property: The contentDisposition property.
     *
     * @param contentDisposition the contentDisposition value to set.
     * @return the BlobItemProperties object itself.
     */
    public BlobItemProperties setContentDisposition(String contentDisposition) {
        this.contentDisposition = contentDisposition;
        return this;
    }

    /**
     * Get the cacheControl property: The cacheControl property.
     *
     * @return the cacheControl value.
     */
    public String getCacheControl() {
        return this.cacheControl;
    }

    /**
     * Set the cacheControl property: The cacheControl property.
     *
     * @param cacheControl the cacheControl value to set.
     * @return the BlobItemProperties object itself.
     */
    public BlobItemProperties setCacheControl(String cacheControl) {
        this.cacheControl = cacheControl;
        return this;
    }

    /**
     * Get the blobSequenceNumber property: The blobSequenceNumber property.
     *
     * @return the blobSequenceNumber value.
     */
    public Long getBlobSequenceNumber() {
        return this.blobSequenceNumber;
    }

    /**
     * Set the blobSequenceNumber property: The blobSequenceNumber property.
     *
     * @param blobSequenceNumber the blobSequenceNumber value to set.
     * @return the BlobItemProperties object itself.
     */
    public BlobItemProperties setBlobSequenceNumber(Long blobSequenceNumber) {
        this.blobSequenceNumber = blobSequenceNumber;
        return this;
    }

    /**
     * Get the blobType property: Possible values include: 'BlockBlob',
     * 'PageBlob', 'AppendBlob'.
     *
     * @return the blobType value.
     */
    public BlobType getBlobType() {
        return this.blobType;
    }

    /**
     * Set the blobType property: Possible values include: 'BlockBlob',
     * 'PageBlob', 'AppendBlob'.
     *
     * @param blobType the blobType value to set.
     * @return the BlobItemProperties object itself.
     */
    public BlobItemProperties setBlobType(BlobType blobType) {
        this.blobType = blobType;
        return this;
    }

    /**
     * Get the leaseStatus property: Possible values include: 'locked',
     * 'unlocked'.
     *
     * @return the leaseStatus value.
     */
    public LeaseStatusType getLeaseStatus() {
        return this.leaseStatus;
    }

    /**
     * Set the leaseStatus property: Possible values include: 'locked',
     * 'unlocked'.
     *
     * @param leaseStatus the leaseStatus value to set.
     * @return the BlobItemProperties object itself.
     */
    public BlobItemProperties setLeaseStatus(LeaseStatusType leaseStatus) {
        this.leaseStatus = leaseStatus;
        return this;
    }

    /**
     * Get the leaseState property: Possible values include: 'available',
     * 'leased', 'expired', 'breaking', 'broken'.
     *
     * @return the leaseState value.
     */
    public LeaseStateType getLeaseState() {
        return this.leaseState;
    }

    /**
     * Set the leaseState property: Possible values include: 'available',
     * 'leased', 'expired', 'breaking', 'broken'.
     *
     * @param leaseState the leaseState value to set.
     * @return the BlobItemProperties object itself.
     */
    public BlobItemProperties setLeaseState(LeaseStateType leaseState) {
        this.leaseState = leaseState;
        return this;
    }

    /**
     * Get the leaseDuration property: Possible values include: 'infinite',
     * 'fixed'.
     *
     * @return the leaseDuration value.
     */
    public LeaseDurationType getLeaseDuration() {
        return this.leaseDuration;
    }

    /**
     * Set the leaseDuration property: Possible values include: 'infinite',
     * 'fixed'.
     *
     * @param leaseDuration the leaseDuration value to set.
     * @return the BlobItemProperties object itself.
     */
    public BlobItemProperties setLeaseDuration(LeaseDurationType leaseDuration) {
        this.leaseDuration = leaseDuration;
        return this;
    }

    /**
     * Get the copyId property: The copyId property.
     *
     * @return the copyId value.
     */
    public String getCopyId() {
        return this.copyId;
    }

    /**
     * Set the copyId property: The copyId property.
     *
     * @param copyId the copyId value to set.
     * @return the BlobItemProperties object itself.
     */
    public BlobItemProperties setCopyId(String copyId) {
        this.copyId = copyId;
        return this;
    }

    /**
     * Get the copyStatus property: Possible values include: 'pending',
     * 'success', 'aborted', 'failed'.
     *
     * @return the copyStatus value.
     */
    public CopyStatusType getCopyStatus() {
        return this.copyStatus;
    }

    /**
     * Set the copyStatus property: Possible values include: 'pending',
     * 'success', 'aborted', 'failed'.
     *
     * @param copyStatus the copyStatus value to set.
     * @return the BlobItemProperties object itself.
     */
    public BlobItemProperties setCopyStatus(CopyStatusType copyStatus) {
        this.copyStatus = copyStatus;
        return this;
    }

    /**
     * Get the copySource property: The copySource property.
     *
     * @return the copySource value.
     */
    public String getCopySource() {
        return this.copySource;
    }

    /**
     * Set the copySource property: The copySource property.
     *
     * @param copySource the copySource value to set.
     * @return the BlobItemProperties object itself.
     */
    public BlobItemProperties setCopySource(String copySource) {
        this.copySource = copySource;
        return this;
    }

    /**
     * Get the copyProgress property: The copyProgress property.
     *
     * @return the copyProgress value.
     */
    public String getCopyProgress() {
        return this.copyProgress;
    }

    /**
     * Set the copyProgress property: The copyProgress property.
     *
     * @param copyProgress the copyProgress value to set.
     * @return the BlobItemProperties object itself.
     */
    public BlobItemProperties setCopyProgress(String copyProgress) {
        this.copyProgress = copyProgress;
        return this;
    }

    /**
     * Get the copyCompletionTime property: The copyCompletionTime property.
     *
     * @return the copyCompletionTime value.
     */
    public OffsetDateTime getCopyCompletionTime() {
        if (this.copyCompletionTime == null) {
            return null;
        }
        return this.copyCompletionTime.getDateTime();
    }

    /**
     * Set the copyCompletionTime property: The copyCompletionTime property.
     *
     * @param copyCompletionTime the copyCompletionTime value to set.
     * @return the BlobItemProperties object itself.
     */
    public BlobItemProperties setCopyCompletionTime(OffsetDateTime copyCompletionTime) {
        if (copyCompletionTime == null) {
            this.copyCompletionTime = null;
        } else {
            this.copyCompletionTime = new DateTimeRfc1123(copyCompletionTime);
        }
        return this;
    }

    /**
     * Get the copyStatusDescription property: The copyStatusDescription
     * property.
     *
     * @return the copyStatusDescription value.
     */
    public String getCopyStatusDescription() {
        return this.copyStatusDescription;
    }

    /**
     * Set the copyStatusDescription property: The copyStatusDescription
     * property.
     *
     * @param copyStatusDescription the copyStatusDescription value to set.
     * @return the BlobItemProperties object itself.
     */
    public BlobItemProperties setCopyStatusDescription(String copyStatusDescription) {
        this.copyStatusDescription = copyStatusDescription;
        return this;
    }

    /**
     * Get the serverEncrypted property: The serverEncrypted property.
     *
     * @return the serverEncrypted value.
     */
    public Boolean isServerEncrypted() {
        return this.serverEncrypted;
    }

    /**
     * Set the serverEncrypted property: The serverEncrypted property.
     *
     * @param serverEncrypted the serverEncrypted value to set.
     * @return the BlobItemProperties object itself.
     */
    public BlobItemProperties setServerEncrypted(Boolean serverEncrypted) {
        this.serverEncrypted = serverEncrypted;
        return this;
    }

    /**
     * Get the incrementalCopy property: The incrementalCopy property.
     *
     * @return the incrementalCopy value.
     */
    public Boolean isIncrementalCopy() {
        return this.incrementalCopy;
    }

    /**
     * Set the incrementalCopy property: The incrementalCopy property.
     *
     * @param incrementalCopy the incrementalCopy value to set.
     * @return the BlobItemProperties object itself.
     */
    public BlobItemProperties setIncrementalCopy(Boolean incrementalCopy) {
        this.incrementalCopy = incrementalCopy;
        return this;
    }

    /**
     * Get the destinationSnapshot property: The destinationSnapshot property.
     *
     * @return the destinationSnapshot value.
     */
    public String getDestinationSnapshot() {
        return this.destinationSnapshot;
    }

    /**
     * Set the destinationSnapshot property: The destinationSnapshot property.
     *
     * @param destinationSnapshot the destinationSnapshot value to set.
     * @return the BlobItemProperties object itself.
     */
    public BlobItemProperties setDestinationSnapshot(String destinationSnapshot) {
        this.destinationSnapshot = destinationSnapshot;
        return this;
    }

    /**
     * Get the deletedTime property: The deletedTime property.
     *
     * @return the deletedTime value.
     */
    public OffsetDateTime getDeletedTime() {
        if (this.deletedTime == null) {
            return null;
        }
        return this.deletedTime.getDateTime();
    }

    /**
     * Set the deletedTime property: The deletedTime property.
     *
     * @param deletedTime the deletedTime value to set.
     * @return the BlobItemProperties object itself.
     */
    public BlobItemProperties setDeletedTime(OffsetDateTime deletedTime) {
        if (deletedTime == null) {
            this.deletedTime = null;
        } else {
            this.deletedTime = new DateTimeRfc1123(deletedTime);
        }
        return this;
    }

    /**
     * Get the remainingRetentionDays property: The remainingRetentionDays
     * property.
     *
     * @return the remainingRetentionDays value.
     */
    public Integer getRemainingRetentionDays() {
        return this.remainingRetentionDays;
    }

    /**
     * Set the remainingRetentionDays property: The remainingRetentionDays
     * property.
     *
     * @param remainingRetentionDays the remainingRetentionDays value to set.
     * @return the BlobItemProperties object itself.
     */
    public BlobItemProperties setRemainingRetentionDays(Integer remainingRetentionDays) {
        this.remainingRetentionDays = remainingRetentionDays;
        return this;
    }

    /**
     * Get the accessTier property: Possible values include: 'P4', 'P6', 'P10',
     * 'P15', 'P20', 'P30', 'P40', 'P50', 'P60', 'P70', 'P80', 'Hot', 'Cool',
     * 'Archive'.
     *
     * @return the accessTier value.
     */
    public AccessTier getAccessTier() {
        return this.accessTier;
    }

    /**
     * Set the accessTier property: Possible values include: 'P4', 'P6', 'P10',
     * 'P15', 'P20', 'P30', 'P40', 'P50', 'P60', 'P70', 'P80', 'Hot', 'Cool',
     * 'Archive'.
     *
     * @param accessTier the accessTier value to set.
     * @return the BlobItemProperties object itself.
     */
    public BlobItemProperties setAccessTier(AccessTier accessTier) {
        this.accessTier = accessTier;
        return this;
    }

    /**
     * Get the accessTierInferred property: The accessTierInferred property.
     *
     * @return the accessTierInferred value.
     */
    public Boolean isAccessTierInferred() {
        return this.accessTierInferred;
    }

    /**
     * Set the accessTierInferred property: The accessTierInferred property.
     *
     * @param accessTierInferred the accessTierInferred value to set.
     * @return the BlobItemProperties object itself.
     */
    public BlobItemProperties setAccessTierInferred(Boolean accessTierInferred) {
        this.accessTierInferred = accessTierInferred;
        return this;
    }

    /**
     * Get the archiveStatus property: Possible values include:
     * 'rehydrate-pending-to-hot', 'rehydrate-pending-to-cool'.
     *
     * @return the archiveStatus value.
     */
    public ArchiveStatus getArchiveStatus() {
        return this.archiveStatus;
    }

    /**
     * Set the archiveStatus property: Possible values include:
     * 'rehydrate-pending-to-hot', 'rehydrate-pending-to-cool'.
     *
     * @param archiveStatus the archiveStatus value to set.
     * @return the BlobItemProperties object itself.
     */
    public BlobItemProperties setArchiveStatus(ArchiveStatus archiveStatus) {
        this.archiveStatus = archiveStatus;
        return this;
    }

    /**
     * Get the customerProvidedKeySha256 property: The
     * customerProvidedKeySha256 property.
     *
     * @return the customerProvidedKeySha256 value.
     */
    public String getCustomerProvidedKeySha256() {
        return this.customerProvidedKeySha256;
    }

    /**
     * Set the customerProvidedKeySha256 property: The
     * customerProvidedKeySha256 property.
     *
     * @param customerProvidedKeySha256 the customerProvidedKeySha256 value to
     * set.
     * @return the BlobItemProperties object itself.
     */
    public BlobItemProperties setCustomerProvidedKeySha256(String customerProvidedKeySha256) {
        this.customerProvidedKeySha256 = customerProvidedKeySha256;
        return this;
    }

    /**
     * Get the accessTierChangeTime property: The accessTierChangeTime
     * property.
     *
     * @return the accessTierChangeTime value.
     */
    public OffsetDateTime getAccessTierChangeTime() {
        if (this.accessTierChangeTime == null) {
            return null;
        }
        return this.accessTierChangeTime.getDateTime();
    }

    /**
     * Set the accessTierChangeTime property: The accessTierChangeTime
     * property.
     *
     * @param accessTierChangeTime the accessTierChangeTime value to set.
     * @return the BlobItemProperties object itself.
     */
    public BlobItemProperties setAccessTierChangeTime(OffsetDateTime accessTierChangeTime) {
        if (accessTierChangeTime == null) {
            this.accessTierChangeTime = null;
        } else {
            this.accessTierChangeTime = new DateTimeRfc1123(accessTierChangeTime);
        }
        return this;
    }
}
