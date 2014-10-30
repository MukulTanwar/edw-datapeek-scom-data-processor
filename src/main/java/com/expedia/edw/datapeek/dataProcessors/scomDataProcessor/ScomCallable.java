/*
 * Copyright 2013 Expedia, Inc. All rights reserved. EXPEDIA
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.expedia.edw.datapeek.dataProcessors.scomDataProcessor;

import java.util.Set;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.expedia.edw.datapeek.common.WorkItem;
import com.expedia.edw.datapeek.common.logging.Stopwatcher;
import com.expedia.edw.datapeek.common.models.Monitor;
import com.expedia.edw.datapeek.common.models.MonitorBase;
import com.expedia.edw.datapeek.common.models.Property;
import com.expedia.edw.datapeek.common.models.SimpleMonitorData;
import com.expedia.edw.datapeek.dataProcessor.WorkItemResult;
import com.expedia.edw.datapeek.dataProcessor.exceptions.TimeoutException;
import com.expedia.edw.datapeek.dataProcessors.scomDataProcessor.results.ResultProcessor;
import com.expedia.edw.datapeek.dataProcessors.scomDataProcessor.service.ScomSearchException;
import com.expedia.edw.datapeek.dataProcessors.scomDataProcessor.service.models.ScomResults;

/**
 * Scom Query Task.
 * 
 * <pre>
 * How it works: 
 *   POST https://chslappspls007.karmalab.net:8089/servicesNS/slapoc/Datapeek/
 *   Host: chslappspls007.karmalab.net:8089
 *   Content-Length: 202
 *   Authorization: Basic c2xhcG9jOnMmWTVKWmtDUFg4
 *   Content-type: application/x-www-form-urlencoded
 * 
 *   search="search+index%3D%22app%22+sourcetype%3D%22SLADashboard%22+%7C+stats+avg(elapsed)+by+baseurl"
 *   &earliest_time="2013-03-11T15:00:00.000-08:00"
 *   &latest_time="2013-03-11T15:10:00.000-08:00"
 * 
 * The Authorization header is a base64 encoding of "username:password". Fiddler
 * has a textwizard tool that does this for you.  Search has to be URL-encoded.
 * 
 * Result:
 *   201 Created
 *   &lt;response&gt;&lt;sid&gt;1363040523.63366.chs-appspls17.idx.expedmz.com&lt;/sid&gt;&lt;/response&gt;
 * 
 * Then:
 *   GET https://chslappspls007.karmalab.net:8089/servicesNS/slapoc/Datapeek/search/jobs/1363040523.63366.chs-appspls17.idx.expedmz.com
 *   Host: scomoi:8089
 *   Authorization: Basic c2xhcG9jOnMmWTVKWmtDUFg4
 * 
 * </pre>
 * 
 * @author dbauman <dbauman@expedia.com>
 * 
 */
public class ScomCallable implements Callable<WorkItemResult[]> {

    /**
     * Class logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ScomCallable.class);

    private static final String PROPERTY_QUERY = "scomQuery";

    private static final String PROPERTY_SERVICENAME = "serviceName";

    private final ResultProcessor resultProcessor;

    private final Scomer scomer;

    private final Stopwatcher stopwatch;

    private WorkItem[] workItems;

    /**
     * Injecting Constructor.
     * 
     * @param resultProcessor
     * @param scomService
     * @param stopwatch
     */
    @Inject
    public ScomCallable(final ResultProcessor resultProcessor,
            final Scomer scomer,
            final Stopwatcher stopwatch) {
        this.resultProcessor = resultProcessor;
        this.scomer = scomer;
        this.stopwatch = stopwatch;
    }

    /**
     * Execute the task.
     * 
     * @see java.util.concurrent.Callable#call()
     */
    @Override
    public WorkItemResult[] call() {
        final WorkItemResult result = new WorkItemResult();

        /* All Work Items are for the same monitor, so use the first one */
        final Monitor workItemMonitor = this.workItems[0].getMonitor();
        final int monitorId = workItemMonitor.getId();

        ScomCallable.LOGGER.debug("Starting ScomCallable - " + monitorId);

        this.stopwatch.startMeasuring("scomCallable");
        this.stopwatch.addDimension("monitorId", monitorId);
        this.stopwatch.addMetric("workItems", this.workItems.length);

        /* Get the query parameters */
        final String searchQuery = workItemMonitor.getPropertyValue(ScomCallable.PROPERTY_QUERY);
        if (searchQuery == null) {
            /* Abort if there is no query */
            this.stopwatch.stopMeasuring("scomCallable");
            this.stopwatch.finish();
            result.setSuccess(false);
            return new WorkItemResult[] { result };
        }

        final WorkItem firstWorkItem = this.workItems[0];
        final WorkItem lastWorkItem = this.workItems[this.workItems.length - 1];

        /* Span all the work items */
        final DateTime earliest = firstWorkItem.getStartTime();
        final DateTime latest = lastWorkItem.getEndTime();
        final int interval = this.workItems[0].getIntervalLength();
        final long priority = workItemMonitor.getPropertyLong("priority");

        boolean success = false;

        try {
            /* Execute the Scom Query */
            final ScomResults results = this.scomer.executeScomQuery(searchQuery, earliest, latest, interval,
                    priority == 1 ? true : false, this.stopwatch);

            if (results != null) {
                final Set<SimpleMonitorData> simpleMonitorData = this.resultProcessor.process(results, this.stopwatch);

                ScomCallable.LOGGER.debug("Monitor Count: " + simpleMonitorData.size());
                for (final SimpleMonitorData datum : simpleMonitorData) {
                    final Monitor monitor = datum.getMonitor();

                    /*
                     * Copy service/interval from the scom query monitor to the derived monitor
                     */
                    monitor.copyProperty(MonitorBase.PROPERTY_INTERVAL, workItemMonitor);
                    monitor.copyProperty(ScomCallable.PROPERTY_SERVICENAME, workItemMonitor);

                    /* Generate name */
                    final StringBuilder name = new StringBuilder();
                    name.append(monitor.getPropertyValue(ScomCallable.PROPERTY_SERVICENAME));
                    name.append(" ");
                    name.append(Scomer.getTimespan(interval));
                    name.append(" - ");

                    boolean firstProperty = true;
                    for (final Property p : monitor.getProperties()) {
                        if (p.getName().equals(ScomCallable.PROPERTY_SERVICENAME) ||
                                p.getName().equals(MonitorBase.PROPERTY_INTERVAL)) {
                            continue;
                        }

                        if (!firstProperty) {
                            name.append(",");
                        }

                        name.append(p.getValue());
                        firstProperty = false;
                    }

                    monitor.setName(name.toString());

                    datum.debugLog();
                }

                result.addMonitorData(simpleMonitorData);
            }

            success = true;
        } catch (final ScomSearchException | TimeoutException e) {
            this.stopwatch.addDimension("exception", e.getClass().getSimpleName());

        } finally {

            /* Stop the stopwatch */
            ScomCallable.LOGGER.debug("Finished ScomCallable - " + monitorId);
            this.stopwatch.addDimension("priority", priority);
            this.stopwatch.addDimension("success", success);
            this.stopwatch.stopMeasuring("scomCallable");
            this.stopwatch.finish();
        }

        result.setSuccess(success);
        return new WorkItemResult[] { result };
    }

    /**
     * Sets the Work Item for this Callable instance.
     * 
     * @param workItem
     *            The Work Item to set.
     * @return This instance.
     */
    public ScomCallable setWorkItems(final WorkItem[] workItems) {
        this.workItems = workItems.clone();

        return this;
    }
}
