/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See License.txt in the project root for
 * license information.
 *
 * Code generated by Microsoft (R) AutoRest Code Generator.
 */

package com.microsoft.azure.cognitiveservices.vision.contentmoderator.models;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Schema items of the body.
 */
public class CreateReviewBodyItem {
    /**
     * Type of the content. Possible values include: 'Image', 'Text'.
     */
    @JsonProperty(value = "Type", required = true)
    private String type;

    /**
     * Content to review.
     */
    @JsonProperty(value = "Content", required = true)
    private String content;

    /**
     * Content Identifier.
     */
    @JsonProperty(value = "ContentId", required = true)
    private String contentId;

    /**
     * Optional CallbackEndpoint.
     */
    @JsonProperty(value = "CallbackEndpoint")
    private String callbackEndpoint;

    /**
     * Optional metadata details.
     */
    @JsonProperty(value = "Metadata")
    private List<CreateReviewBodyItemMetadataItem> metadata;

    /**
     * Get type of the content. Possible values include: 'Image', 'Text'.
     *
     * @return the type value
     */
    public String type() {
        return this.type;
    }

    /**
     * Set type of the content. Possible values include: 'Image', 'Text'.
     *
     * @param type the type value to set
     * @return the CreateReviewBodyItem object itself.
     */
    public CreateReviewBodyItem withType(String type) {
        this.type = type;
        return this;
    }

    /**
     * Get content to review.
     *
     * @return the content value
     */
    public String content() {
        return this.content;
    }

    /**
     * Set content to review.
     *
     * @param content the content value to set
     * @return the CreateReviewBodyItem object itself.
     */
    public CreateReviewBodyItem withContent(String content) {
        this.content = content;
        return this;
    }

    /**
     * Get content Identifier.
     *
     * @return the contentId value
     */
    public String contentId() {
        return this.contentId;
    }

    /**
     * Set content Identifier.
     *
     * @param contentId the contentId value to set
     * @return the CreateReviewBodyItem object itself.
     */
    public CreateReviewBodyItem withContentId(String contentId) {
        this.contentId = contentId;
        return this;
    }

    /**
     * Get optional CallbackEndpoint.
     *
     * @return the callbackEndpoint value
     */
    public String callbackEndpoint() {
        return this.callbackEndpoint;
    }

    /**
     * Set optional CallbackEndpoint.
     *
     * @param callbackEndpoint the callbackEndpoint value to set
     * @return the CreateReviewBodyItem object itself.
     */
    public CreateReviewBodyItem withCallbackEndpoint(String callbackEndpoint) {
        this.callbackEndpoint = callbackEndpoint;
        return this;
    }

    /**
     * Get optional metadata details.
     *
     * @return the metadata value
     */
    public List<CreateReviewBodyItemMetadataItem> metadata() {
        return this.metadata;
    }

    /**
     * Set optional metadata details.
     *
     * @param metadata the metadata value to set
     * @return the CreateReviewBodyItem object itself.
     */
    public CreateReviewBodyItem withMetadata(List<CreateReviewBodyItemMetadataItem> metadata) {
        this.metadata = metadata;
        return this;
    }

}
