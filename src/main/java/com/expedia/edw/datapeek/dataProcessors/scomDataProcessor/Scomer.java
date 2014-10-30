/*
 * Copyright 2013 Expedia, Inc. All rights reserved. EXPEDIA
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.expedia.edw.datapeek.dataProcessors.scomDataProcessor;

import javax.inject.Inject;
import javax.inject.Named;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.expedia.edw.datapeek.common.logging.Stopwatcher;
import com.expedia.edw.datapeek.dataProcessor.exceptions.TimeoutException;
import com.expedia.edw.datapeek.dataProcessors.scomDataProcessor.service.ScomSearchException;
import com.expedia.edw.datapeek.dataProcessors.scomDataProcessor.service.ScomService;
import com.expedia.edw.datapeek.dataProcessors.scomDataProcessor.service.models.PostSearchResponse;
import com.expedia.edw.datapeek.dataProcessors.scomDataProcessor.service.models.ScomJobs;
import com.expedia.edw.datapeek.dataProcessors.scomDataProcessor.service.models.ScomMessageXml;
import com.expedia.edw.datapeek.dataProcessors.scomDataProcessor.service.models.ScomMessages;
import com.expedia.edw.datapeek.dataProcessors.scomDataProcessor.service.models.ScomResults;
import com.expedia.edw.datapeek.dataProcessors.scomDataProcessor.service.models.ScomSearch;
import com.google.common.base.Joiner;

/**
 * Does the Scoming.
 * 
 * @author dbauman
 * 
 */
public class Scomer {

    /**
     * Class logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(Scomer.class);

    public static final String PROPERTY_WARNING_WHITELIST = "scom.warningWhiteList";

    private final ScomService scomService;

    private final String[] warningWhiteList;

    @Inject
    public Scomer(final ScomService scomService,
            @Named(Scomer.PROPERTY_WARNING_WHITELIST) final String warningWhiteList) {
        this.scomService = scomService;
        this.warningWhiteList = warningWhiteList.split("|");
    }

    /***
     * Executes a Scom Query and processes the result.
     * 
     * @param searchQuery
     * @param earliest
     * @param latest
     * @param interval
     * @param ignoreErrors
     * @param parentStopwatch
     * @return
     * @throws TimeoutException
     * @throws ScomSearchException
     */
    public ScomResults executeScomQuery(final String searchQuery, final DateTime earliest, final DateTime latest,
            final int interval, final boolean ignoreErrors, final Stopwatcher parentStopwatch) throws TimeoutException,
            ScomSearchException {

        /* Start stopwatch */
        final Stopwatcher stopwatch = parentStopwatch.child();
        stopwatch.startMeasuring("scomer");

        ScomResults results = null;
        String searchId = null;
        boolean success = false;

        try {

            final String cleanedSearchquery = Scomer.cleanScomQuery(searchQuery, interval);

            /* Create the Search Job */
            final PostSearchResponse response = this.scomService.postSearch(cleanedSearchquery, earliest, latest);

            /* Get the Search Job Id */
            searchId = response.getSid();

            /* Abort if no search id was created */
            if (searchId == null) {
                for (final ScomMessageXml message : response.getMessages()) {
                    Scomer.LOGGER.error(message.getMessage());
                    stopwatch.stopMeasuring("scomer");
                    stopwatch.finish();

                    throw new UnknownError("No search id created for search.");
                }
            }

            ScomJobs jobs = null;
            ScomSearch search = null;
            int pollCounter = 0;

            /*
             * Deadline of when the Search Job needs to be returned (without results)
             */
            final DateTime noJobTimeoutTime = DateTime.now().plus(ScomCallableFactory.NO_JOB_TIMEOUT_MILLIS);

            while (true) {
                /*
                 * Get the status of the Search Job. If null is returned, the job must not exist yet
                 */
                jobs = this.scomService.getSearchStatus(searchId);
                pollCounter++;
                if (jobs != null) {

                    search = jobs.first();

                    if (search.isDone()) {
                        break;
                    }
                } else {
                    if (noJobTimeoutTime.isBeforeNow()) {
                        /* Abort the Work Item */
                        throw new TimeoutException("Could not retrieve the created Scom Job before timeout expired.");
                    }
                }
                try {
                    /* Sleep for a configured amount of time before retrying */
                    Thread.sleep(ScomCallableFactory.CHECK_JOB_MILLIS);

                } catch (final InterruptedException e) {
                    Scomer.LOGGER.warn("InterruptedException occured in Scomer", e);
                }
            }

            /* Log all search dimensions */
            stopwatch.addDimension("isFailed", search.isFailed());
            stopwatch.addMetric("diskUsage", search.getDiskUsage());
            stopwatch.addMetric("dropCount", search.getDropCount());
            stopwatch.addMetric("eventAvailableCount", search.getEventAvailableCount());
            stopwatch.addMetric("eventCount", search.getEventCount());
            stopwatch.addMetric("eventFieldCount", search.getEventFieldCount());
            stopwatch.addMetric("resultCount", search.getResultCount());
            stopwatch.addMetric("scanCount", search.getScanCount());
            stopwatch.addMetric("resultPollCount", pollCounter);

            /* final Check for errors */
            final ScomMessages messages = search.getMessages();
            if (messages.getFatal() != null) {
                stopwatch.addMetric("fatalMessagesCount", messages.getFatal().size());

                /* Always log fatal messages but don't fail the Work Items */
                Scomer.LOGGER.warn("Scom fatal message(s): " + Joiner.on(", ").join(messages.getFatal()));
            } else {
                stopwatch.addMetric("fatalMessagesCount", 0);
            }

            if (messages.getError() != null) {
                stopwatch.addMetric("errorMessagesCount", messages.getError().size());
                if (!ignoreErrors) {
                    Scomer.LOGGER.warn("Scom error message(s): " + Joiner.on(", ").join(messages.getError()));
                    throw new ScomSearchException();
                }
            } else {
                stopwatch.addMetric("errorMessagesCount", 0);
            }

            if (messages.getWarn() != null) {
                stopwatch.addMetric("warnMessagesCount", messages.getWarn().size());
                if (!ignoreErrors) {
                    Scomer.LOGGER.warn("Scom warn message: " + Joiner.on(", ").join(messages.getWarn()));

                    /*
                     * Test each warning against the whitelist. If it is not whitelisted, throw an exception
                     */
                    for (final String warning : messages.getWarn()) {
                        for (final String whitelisted : this.warningWhiteList) {
                            if (!warning.matches(whitelisted)) {
                                throw new ScomSearchException();
                            }
                        }
                    }
                }
            } else {
                stopwatch.addMetric("warnMessagesCount", 0);
            }

            if (messages.getInfo() != null) {
                stopwatch.addMetric("infoMessagesCount", messages.getInfo().size());
            } else {
                stopwatch.addMetric("infoMessagesCount", 0);
            }

            /* Get the results (if any) */
            if (search.getResultCount() > 0) {
                results = this.scomService.getSearchResults(searchId);
            }

            /* If we made it this far, it was a success */
            success = true;
        } finally {
            /* Delete the Scom search */
            if (searchId != null) {
                this.scomService.deleteSearch(searchId);
            }

            /* Stop the stopwatch */
            Scomer.LOGGER.debug("Finished Scomer");
            stopwatch.addDimension("success", success);
            stopwatch.stopMeasuring("scomer", "scomer");
            stopwatch.finish();
        }

        return results;
    }

    /**
     * Cleans up a Scom query and preps it for execution. Ensures timespan commands have a correct span based on the interval.
     * 
     * @param originalQuery
     * @param interval
     * @return The updated query
     */
    public static String cleanScomQuery(final String originalQuery, final int interval) {
        if (originalQuery == null) {
            return null;
        }

        /* Replace/Add the span=* argument to the last timechart */
        /*
         * Other timechart commands should have their own span which is not modified
         */
        final String timechart = "timechart";
        final int lastIndex = originalQuery.lastIndexOf(timechart);

        if (lastIndex < 0) {
            return originalQuery;
        }

        final String preTimechart = originalQuery.substring(0, lastIndex);
        String postTimechart = originalQuery.substring(lastIndex + timechart.length()).trim();
        Scomer.LOGGER.debug(preTimechart);
        Scomer.LOGGER.debug(postTimechart);

        if (postTimechart.startsWith("span=")) {
            postTimechart = postTimechart.substring(postTimechart.indexOf(' ')).trim();
        }

        /* Add the time span */
        final String timespanString = Scomer.getTimespan(interval);
        return preTimechart + timechart + " span=" + timespanString + " " + postTimechart;
    }

    /**
     * Creates a timespan string for Scom, given a number of seconds. Examples: 60: 1m 300: 5m 3600: 1h 86400: 1d 604800: 1w
     * 
     * @param interval
     * @return
     */
    public static String getTimespan(final int interval) {

        if (interval % 604800 == 0) {
            return (interval / 604800) + "w";
        } else if (interval % 86400 == 0) {
            return (interval / 86400) + "d";
        } else if (interval % 3600 == 0) {
            return (interval / 3600) + "h";
        } else if (interval % 60 == 0) {
            return (interval / 60) + "m";
        } else {
            return interval + "s";
        }
    }
}
