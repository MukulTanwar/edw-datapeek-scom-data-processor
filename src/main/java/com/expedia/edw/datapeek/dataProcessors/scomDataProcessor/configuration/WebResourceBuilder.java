/*
 * Copyright 2013 Expedia, Inc. All rights reserved. EXPEDIA
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.expedia.edw.datapeek.dataProcessors.scomDataProcessor.configuration;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.ws.rs.core.MediaType;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

/**
 * Creates new WebResources.
 * 
 * @author dbauman
 * 
 */
@Singleton
public class WebResourceBuilder {

    private final Client client;

    private final String baseUrl;

    @Inject
    public WebResourceBuilder(final Client client,
            @Named(ConfigurationModule.PROPERTY_SCOM_BASEURL) final String baseUrl) {
        this.client = client;
        this.baseUrl = baseUrl;
    }

    /**
     * Create a WebResource for the default host.
     * 
     * @return A new WebResource
     */
    public WebResource createDefaultWebResource() {
        return this.create(this.baseUrl);
    }

    private WebResource create(final String uri) {
        final WebResource resource = this.client.resource(uri);
        resource.accept(MediaType.APPLICATION_XML_TYPE);
        resource.header("X-Requested-With", "ScomDataProcessor");

        return resource;
    }

    /**
     * Create a WebResource for a specific host.
     * 
     * @param host
     *            The host name.
     * @return A new WebResource
     */
    public WebResource createWebResource(final String host) {
        final String modifiedUri = this.baseUrl.replaceAll("\\:\\/\\/.*:", "://" + host + ":");
        return this.create(modifiedUri);
    }
}
