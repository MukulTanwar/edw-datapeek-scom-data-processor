/*
 * Copyright 2013 Expedia, Inc. All rights reserved. EXPEDIA
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.expedia.edw.datapeek.dataProcessors.scomDataProcessor.results;

import java.util.Set;

import com.expedia.edw.datapeek.common.logging.Stopwatcher;
import com.expedia.edw.datapeek.common.models.SimpleMonitorData;
import com.expedia.edw.datapeek.dataProcessors.scomDataProcessor.service.models.ScomResults;

/**
 * Processes a ScomResult and creates a set of MonitorDatas.
 * 
 * @author dbauman <dbauman@expedia.com>
 * 
 */
public interface ResultProcessor {

    /**
     * Converts a ScomResult object into a set of SimpleMonitorData elements.
     * 
     * @param results
     *            The results to process
     * @param stopwatch
     *            The stopwatcher to use
     * 
     * @return The corresponding SimpleMonitorData
     */
    abstract Set<SimpleMonitorData> process(ScomResults results, Stopwatcher stopwatch);

}
