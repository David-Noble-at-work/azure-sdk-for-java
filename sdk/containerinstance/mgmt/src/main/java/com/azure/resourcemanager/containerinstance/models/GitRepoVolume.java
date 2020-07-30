// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.
// Code generated by Microsoft (R) AutoRest Code Generator.

package com.azure.resourcemanager.containerinstance.models;

import com.azure.core.annotation.Fluent;
import com.azure.core.util.logging.ClientLogger;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/** The GitRepoVolume model. */
@Fluent
public final class GitRepoVolume {
    @JsonIgnore private final ClientLogger logger = new ClientLogger(GitRepoVolume.class);

    /*
     * Target directory name. Must not contain or start with '..'.  If '.' is
     * supplied, the volume directory will be the git repository.  Otherwise,
     * if specified, the volume will contain the git repository in the
     * subdirectory with the given name.
     */
    @JsonProperty(value = "directory")
    private String directory;

    /*
     * Repository URL
     */
    @JsonProperty(value = "repository", required = true)
    private String repository;

    /*
     * Commit hash for the specified revision.
     */
    @JsonProperty(value = "revision")
    private String revision;

    /**
     * Get the directory property: Target directory name. Must not contain or start with '..'. If '.' is supplied, the
     * volume directory will be the git repository. Otherwise, if specified, the volume will contain the git repository
     * in the subdirectory with the given name.
     *
     * @return the directory value.
     */
    public String directory() {
        return this.directory;
    }

    /**
     * Set the directory property: Target directory name. Must not contain or start with '..'. If '.' is supplied, the
     * volume directory will be the git repository. Otherwise, if specified, the volume will contain the git repository
     * in the subdirectory with the given name.
     *
     * @param directory the directory value to set.
     * @return the GitRepoVolume object itself.
     */
    public GitRepoVolume withDirectory(String directory) {
        this.directory = directory;
        return this;
    }

    /**
     * Get the repository property: Repository URL.
     *
     * @return the repository value.
     */
    public String repository() {
        return this.repository;
    }

    /**
     * Set the repository property: Repository URL.
     *
     * @param repository the repository value to set.
     * @return the GitRepoVolume object itself.
     */
    public GitRepoVolume withRepository(String repository) {
        this.repository = repository;
        return this;
    }

    /**
     * Get the revision property: Commit hash for the specified revision.
     *
     * @return the revision value.
     */
    public String revision() {
        return this.revision;
    }

    /**
     * Set the revision property: Commit hash for the specified revision.
     *
     * @param revision the revision value to set.
     * @return the GitRepoVolume object itself.
     */
    public GitRepoVolume withRevision(String revision) {
        this.revision = revision;
        return this;
    }

    /**
     * Validates the instance.
     *
     * @throws IllegalArgumentException thrown if the instance is not valid.
     */
    public void validate() {
        if (repository() == null) {
            throw logger
                .logExceptionAsError(
                    new IllegalArgumentException("Missing required property repository in model GitRepoVolume"));
        }
    }
}
