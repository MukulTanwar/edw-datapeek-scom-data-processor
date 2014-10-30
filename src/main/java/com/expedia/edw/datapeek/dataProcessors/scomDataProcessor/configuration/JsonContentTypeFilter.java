/*
 * Copyright 2013 Expedia, Inc. All rights reserved. EXPEDIA
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.expedia.edw.datapeek.dataProcessors.scomDataProcessor.configuration;

import javax.ws.rs.core.HttpHeaders;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.ClientFilter;
import com.sun.jersey.core.header.InBoundHeaders;

/**
 * Fixes text/plain content types to application/json.
 * 
 * 
 * @author dbauman
 * 
 */
public class JsonContentTypeFilter extends ClientFilter {

    private final Client client;

    public JsonContentTypeFilter(final Client client) {
        this.client = client;
    }

    @Override
    public ClientResponse handle(final ClientRequest clientRequest) {

        // Call the next filter
        final ClientResponse response = this.getNext().handle(clientRequest);

        final String contentType = response.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE);

        if (contentType.startsWith("text/plain")) {
            final String newContentType = "application/json" + contentType.substring(10);

            /* Create a new modified clone of the headers */
            final InBoundHeaders headers = new InBoundHeaders();

            for (final String header : response.getHeaders().keySet()) {
                if (header.equals(HttpHeaders.CONTENT_TYPE)) {
                    headers.putSingle(HttpHeaders.CONTENT_TYPE, newContentType);
                } else {
                    headers.put(header, headers.get(header));
                }
            }

            return new ClientResponse(response.getStatus(),
                    headers,
                    response.getEntityInputStream(),
                    this.client.getMessageBodyWorkers());
        }

        return response;
    }

}
