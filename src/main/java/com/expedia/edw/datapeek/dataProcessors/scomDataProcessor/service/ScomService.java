/*
 * Copyright 2013 Expedia, Inc. All rights reserved. EXPEDIA
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.expedia.edw.datapeek.dataProcessors.scomDataProcessor.service;

import org.joda.time.DateTime;

import com.expedia.edw.datapeek.dataProcessors.scomDataProcessor.service.models.PostSearchResponse;
import com.expedia.edw.datapeek.dataProcessors.scomDataProcessor.service.models.ScomJobs;
import com.expedia.edw.datapeek.dataProcessors.scomDataProcessor.service.models.ScomResults;

/**
 * Interface for interacting with the Scom REST API service.
 * 
 * @author dbauman
 * 
 */
public interface ScomService {

    /**
     * Creates a new Search.
     * 
     * @param search
     *            The search query string.
     * @param earliest
     *            The start time for the search.
     * @param latest
     *            The end time for the search.
     * @return The response, containing the new Search Id (if it exists) and any messages.
     */
    PostSearchResponse postSearch(final String search, final DateTime earliest, final DateTime latest);

    /**
     * Gets the current status for a Search Id.
     * 
     * @param searchId
     *            The Search Id to retrieve.
     * @return
     */
    ScomJobs getSearchStatus(String searchId);

    /**
     * Gets the results for a Search Id.
     * 
     * @param searchId
     *            The Search Id to retrieve.
     * @return
     */
    ScomResults getSearchResults(String searchId);

    /**
     * Deletes an existing Search.
     * 
     * @param searchId
     *            The Search Id to delete.
     * 
     */
    void deleteSearch(String searchId);
}
