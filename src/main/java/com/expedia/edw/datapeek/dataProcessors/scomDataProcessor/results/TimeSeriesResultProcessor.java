/*
 * Copyright 2013 Expedia, Inc. All rights reserved. EXPEDIA
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.expedia.edw.datapeek.dataProcessors.scomDataProcessor.results;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.expedia.edw.datapeek.common.logging.Stopwatcher;
import com.expedia.edw.datapeek.common.models.Monitor;
import com.expedia.edw.datapeek.common.models.SimpleMonitorData;
import com.expedia.edw.datapeek.dataProcessors.scomDataProcessor.service.models.ScomResults;

/**
 * Processes a ScomResult and creates a set of MonitorData. Specific to time-series data with a _time column.
 * 
 * @author dbauman <dbauman@expedia.com>
 * 
 */
public class TimeSeriesResultProcessor implements ResultProcessor {

    /**
     * Class logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(TimeSeriesResultProcessor.class);

    private static final String TIME_SERIES_COLUMN = "_time";

    private static final String SCOM_INTERNAL_FIELD_PREFIX = "_";

    private final ScomFieldParser fieldParser;

    /**
     * Default consructor.
     */
    @Inject
    public TimeSeriesResultProcessor(final ScomFieldParser fieldParser) {
        this.fieldParser = fieldParser;
    }

    /**
     * Converts a ScomResult object into a set of MonitorData elements, using the _time column to do time-series grouping.
     * 
     * @param results
     *            The results to process
     * @param stopwatch
     *            The stopwatcher to use
     * @return The corresponding MonitorData
     */
    @Override
    public Set<SimpleMonitorData> process(final ScomResults results, final Stopwatcher stopwatch) {
        TimeSeriesResultProcessor.LOGGER.debug("Processing Time Series results");
        stopwatch.startMeasuring("resultsProcess");

        /* Map of field indexes to SimpleMonitorData objects */
        final Map<Integer, SimpleMonitorData> monitors = new HashMap<>();
        int countOfBadMonitors = 0;

        List<String> timeColumn = null;

        final List<String> fields = results.getFields();
        final List<List<String>> columns = results.getColumns();

        if (columns.size() == 0) {
            stopwatch.stopMeasuring("resultsProcess");
            stopwatch.addMetric("noData", 1);

            return new HashSet<SimpleMonitorData>();
        }

        /* Loop through fields */
        for (int i = 0; i < fields.size(); i++) {
            final String field = fields.get(i);

            if (field.equalsIgnoreCase(TimeSeriesResultProcessor.TIME_SERIES_COLUMN)) {
                /* Store the time column */
                timeColumn = columns.get(i);
            } else if (field.startsWith(TimeSeriesResultProcessor.SCOM_INTERNAL_FIELD_PREFIX)) {
                continue;
            } else {
                final Monitor m = this.fieldParser.parse(field);

                /*
                 * Skip any columns/monitors that only have one property (metric)
                 */
                if (m.getProperties().size() == 1) {
                    countOfBadMonitors++;
                    continue;
                }

                /*
                 * Store the Monitor for later. The field index will be used as a lookup on the monitors hashmap when processing the columns
                 */
                monitors.put(i, new SimpleMonitorData(m));
            }
        }

        if (timeColumn == null) {
            stopwatch.stopMeasuring("resultsProcess");
            stopwatch.addMetric("noTime", 1);
            TimeSeriesResultProcessor.LOGGER
                    .warn("Scom query did not have a _time column; it cannot be processed.");

            return new HashSet<SimpleMonitorData>();
        }

        /* Loop through columns */
        final int numberOfRows = columns.get(0).size();
        for (int column = 0; column < columns.size(); column++) {
            final List<String> currentColumn = columns.get(column);
            final SimpleMonitorData currentMonitorData = monitors.get(column);

            if (currentMonitorData == null) {
                continue;
            }

            /* Loop through Rows */
            for (int row = 0; row < numberOfRows; row++) {
                final DateTime time = new DateTime(timeColumn.get(row));
                final String valueString = currentColumn.get(row);

                if (valueString == null) {
                    continue;
                }

                currentMonitorData.pushData(time, Double.parseDouble(valueString));
            }
        }

        final Set<SimpleMonitorData> dataSet = new HashSet<>(monitors.values());

        stopwatch.stopMeasuring("resultsProcess");
        stopwatch.addMetric("badMonitors", countOfBadMonitors);

        if (countOfBadMonitors > 0) {
            TimeSeriesResultProcessor.LOGGER
                    .warn("Scom query had " + countOfBadMonitors + " bad monitor definitions");
        }

        return dataSet;
    }
}
