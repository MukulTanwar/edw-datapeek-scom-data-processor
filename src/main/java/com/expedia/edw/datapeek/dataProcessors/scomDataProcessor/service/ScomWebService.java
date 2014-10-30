/*
 * Copyright 2013 Expedia, Inc. All rights reserved. EXPEDIA
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.expedia.edw.datapeek.dataProcessors.scomDataProcessor.service;

import javax.inject.Inject;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.joda.time.DateTime;

import com.expedia.edw.datapeek.common.logging.Stopwatcher;
import com.expedia.edw.datapeek.dataProcessors.scomDataProcessor.configuration.WebResourceBuilder;
import com.expedia.edw.datapeek.dataProcessors.scomDataProcessor.service.models.PostSearchResponse;
import com.expedia.edw.datapeek.dataProcessors.scomDataProcessor.service.models.ScomJobs;
import com.expedia.edw.datapeek.dataProcessors.scomDataProcessor.service.models.ScomResults;
import com.google.common.collect.ImmutableMap;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

/**
 * Implementation of the Scom Service.
 * 
 * @author dbauman
 * 
 */
public class ScomWebService implements ScomService {

    private final WebResource defaultWebResource;

    private final Stopwatcher stopwatch;

    private final WebResourceBuilder webResourceBuilder;

    /**
     * Default constructor.
     * 
     * @param client
     * @param webResourceBuilder
     * @param stopwatch
     */
    @Inject
    public ScomWebService(final WebResourceBuilder webResourceBuilder, final Stopwatcher stopwatch) {
        this.webResourceBuilder = webResourceBuilder;
        this.defaultWebResource = this.webResourceBuilder.createDefaultWebResource();
        this.stopwatch = stopwatch;
    }

    /**
     * Deletes an existing Search.
     * 
     * @param searchId
     *            The Search Id to delete.
     * 
     */
    @Override
    public void deleteSearch(final String searchId) {
        final Stopwatcher localStopwatch = this.stopwatch.child();
        localStopwatch.startMeasuring(Stopwatcher.METRIC_HTTP);

        final WebResource request = this.getWebResource(searchId).path("search/jobs/" + searchId);
        final ClientResponse response = request.delete(ClientResponse.class);

        localStopwatch.stopMeasuring(Stopwatcher.METRIC_HTTP, "httpDeleteSearch");
        localStopwatch.incrementParentMetric("scomHttpOps", true);
        localStopwatch.finish(ImmutableMap.of(
                "verb", "DELETE",
                "requestHost", request.getURI().getHost(),
                "operation", "/search/jobs",
                "search_id", searchId,
                "statusCode", response.getStatus()));
    }

    /**
     * Gets the results for a Search Id.
     * 
     * @param searchId
     *            The Search Id to retrieve.
     * @return
     */
    @Override
    public ScomResults getSearchResults(final String searchId) {
        final Stopwatcher localStopwatch = this.stopwatch.child();
        localStopwatch.startMeasuring(Stopwatcher.METRIC_HTTP);

        final WebResource request = this.getWebResource(searchId)
                .path("search/jobs/" + searchId + "/results")
                .queryParam("output_mode", "json_cols")
                .queryParam("maxresultrows", "0");

        final ClientResponse response = request
                .accept(MediaType.APPLICATION_JSON)
                .get(ClientResponse.class);

        localStopwatch.stopMeasuring(Stopwatcher.METRIC_HTTP, "httpGetResults");
        localStopwatch.incrementParentMetric("scomHttpOps", true);
        localStopwatch.finish(ImmutableMap.of(
                "verb", "GET",
                "requestHost", request.getURI().getHost(),
                "search_id", searchId,
                "operation", "/search/jobs/results",
                "statusCode", response.getStatus()));

        /* Only parse the result if a 200 was returned */
        if (response.getStatus() != 200) {
            return null;
        }

        /* Check for empty content e.g. no results */
        if ("0".equals(response.getHeaders().get("Content-Length").get(0))) {
            return new ScomResults();
        }

        return response.getEntity(ScomResults.class);
    }

    /**
     * Gets the current status for a Search Id.
     * 
     * @param searchId
     *            The Search Id to retrieve.
     * @return
     */
    @Override
    public ScomJobs getSearchStatus(final String searchId) {
        final Stopwatcher localStopwatch = this.stopwatch.child();
        localStopwatch.startMeasuring(Stopwatcher.METRIC_HTTP);

        final WebResource request = this.getWebResource(searchId)
                .path("search/jobs/" + searchId)
                .queryParam("output_mode", "json");

        final ClientResponse response = request
                .accept(MediaType.APPLICATION_JSON)
                .get(ClientResponse.class);

        localStopwatch.stopMeasuring(Stopwatcher.METRIC_HTTP, "httpPostSearch");
        localStopwatch.incrementParentMetric("scomHttpOps", true);
        localStopwatch.finish(ImmutableMap.of(
                "verb", "GET",
                "requestHost", request.getURI().getHost(),
                "search_id", searchId,
                "operation", "/search/jobs",
                "statusCode", response.getStatus()));

        /*
         * Only parse the result if a 200 was returned
         * 
         * 204 is a likely culprit, seems it is returned before Scom has the job in it's system
         */
        if (response.getStatus() != 200) {
            return null;
        }

        return response.getEntity(ScomJobs.class);
    }

    /**
     * Parses a search Id to get a hostname, if found. Otherwise returns the default WebResource.
     * 
     * @param searchId
     *            The searchId.
     * @return A WebResource.
     */
    private WebResource getWebResource(final String searchId) {
        final String searchId2 = searchId.toLowerCase();

        final int endIndex = searchId2.indexOf(".idx.expedmz.com");
        if (endIndex > 0) {
            final int startIndex = searchId2.lastIndexOf(".", endIndex - 1);

            final String host = searchId2.substring(startIndex + 1);
            return this.webResourceBuilder.createWebResource(host);
        }

        /* Else default */
        return this.defaultWebResource;
    }

    /**
     * Creates a new Search.
     * 
     * @param searchQuery
     *            The search query string.
     * @param earliest
     *            The start time for the search.
     * @param latest
     *            The end time for the search.
     * @return The response, containing the new Search Id (if it exists) and any messages.
     */
    @Override
    public PostSearchResponse postSearch(final String searchQuery, final DateTime earliest, final DateTime latest) {
        final Stopwatcher localStopwatch = this.stopwatch.child();
        localStopwatch.startMeasuring(Stopwatcher.METRIC_HTTP);

        final WebResource request = this.defaultWebResource.path("search/jobs");

        final MultivaluedMap<String, String> formData = new MultivaluedMapImpl();
        formData.add("search", "search " + searchQuery);
        formData.add("earliest_time", earliest.toString());
        formData.add("latest_time", latest.toString());

        final ClientResponse response = request
                .type(MediaType.APPLICATION_FORM_URLENCODED)
                .post(ClientResponse.class, formData);

        localStopwatch.stopMeasuring(Stopwatcher.METRIC_HTTP, "httpPostSearch");
        localStopwatch.incrementParentMetric("scomHttpOps", true);
        localStopwatch.finish(ImmutableMap.of(
                "verb", "POST",
                "requestHost", request.getURI().getHost(),
                "operation", "/search/jobs",
                "statusCode", response.getStatus()));

        return response.getEntity(PostSearchResponse.class);
    }
}
