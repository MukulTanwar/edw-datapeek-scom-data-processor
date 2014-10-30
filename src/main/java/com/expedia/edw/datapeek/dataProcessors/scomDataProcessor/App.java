/*
 * Copyright 2013 Expedia, Inc. All rights reserved. EXPEDIA
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.expedia.edw.datapeek.dataProcessors.scomDataProcessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.expedia.edw.datapeek.dataProcessor.CatchupThreadPoolDataProcessor;
import com.expedia.edw.datapeek.dataProcessor.DataProcessor;

/**
 * Scom Data Processor Main Entry Point
 * 
 */
public final class App {

    /**
     * Class logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    /**
     * Application root.
     * 
     * @param args
     *            System args.
     */
    public static void main(final String[] args) {

        App.LOGGER.info("Starting Scom Data Processor!");

        final DataProcessor dataProcessor = new CatchupThreadPoolDataProcessor();
        dataProcessor.run(new ScomCallableFactory());

        App.LOGGER.info("Scom Data Processor open for business!");

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable()
        {
            @Override
            public void run() {
                App.shutdown();
            }
        }));
    }

    /**
     * Shutdown the Data Processor and dependencies.
     */
    public static void shutdown() {
        /* Shutdown */
        // dataProcessor.shutdown();
        App.LOGGER.info("Scom Data Processor done!");
    }

    /**
     * Private constructor to avoid initialization.
     */
    private App() {
    }
}
