/*
 * Copyright 2013 Expedia, Inc. All rights reserved. EXPEDIA
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.expedia.edw.datapeek.dataProcessors.scomDataProcessor;

import java.util.Properties;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.expedia.edw.datapeek.common.WorkItem;
import com.expedia.edw.datapeek.dataProcessor.CallableFactory;
import com.expedia.edw.datapeek.dataProcessor.WorkItemResult;
import com.expedia.edw.datapeek.dataProcessors.scomDataProcessor.configuration.ConfigurationModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Creates ScomCallable instances.
 * 
 * @author dbauman
 * 
 */
public class ScomCallableFactory implements CallableFactory {

    /**
     * Provides access to the scom.checkJobMillis property in the config file. Indicates how long to wait between checking the status of
     * each running Scom job.
     * 
     * @return
     */
    public static final long CHECK_JOB_MILLIS;

    /**
     * Provides access to the scom.noJobTimeoutMillis property in the config file. This value indicates how long to retry getting the
     * created Search job from Scom before giving up. It is NOT an indicator for how long to wait for results, but rather, how long to
     * wait for the job to have been created.
     */
    public static final long NO_JOB_TIMEOUT_MILLIS;

    /**
     * Guice injector for creating the ScomCallables with dependencies.
     */
    private static final Injector INJECTOR = Guice.createInjector(new ConfigurationModule());

    /**
     * Class logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ScomCallableFactory.class);

    /**
     * Initialize static properties from the config file.
     */
    static {
        final Properties properties = ScomCallableFactory.INJECTOR.getInstance(Properties.class);

        /* Defaults are handled in the Configuration class */
        CHECK_JOB_MILLIS = Long.parseLong(
                properties.getProperty(ConfigurationModule.PROPERTY_SCOM_CHECK_JOB_MILLIS));
        NO_JOB_TIMEOUT_MILLIS = Long.parseLong(
                properties.getProperty(ConfigurationModule.PROPERTY_SCOM_NO_JOB_TIMEOUT_MILLIS));

    }

    /**
     * Create a new instance of the Callable.
     * 
     * @param workItem
     *            The Work Item to be processed in this Callable.
     * @return The new Callable instance.
     */
    @Override
    public Callable<WorkItemResult> create(final WorkItem workItem) {
        throw new UnsupportedOperationException("Scom Data Processor must use the multiple WorkItem interface.");
    }

    /**
     * Create a ScomCallable instance.
     * 
     * @param workItem
     *            The Work Item(s) to be processed in this Callable.
     * 
     * @return The new Callable instance for this Work Item.
     * @see com.expedia.edw.datapeek.dataProcessor.CallableFactory#create()
     */
    @Override
    public Callable<WorkItemResult[]> create(final WorkItem[] workItems) {

        ScomCallableFactory.LOGGER.debug("Creating ScomCallable");

        /* Instantiate callable with dependencies */
        final ScomCallable callable = ScomCallableFactory.INJECTOR.getInstance(ScomCallable.class);

        /* Set the work item into the Callable */
        return callable.setWorkItems(workItems);
    }

    /**
     * Return the name of this Data Processor.
     * 
     * @return
     */
    @Override
    public String getName() {
        return "Scom";
    }

}
