/*
 * Copyright 2013 Expedia, Inc. All rights reserved. EXPEDIA
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.expedia.edw.datapeek.dataProcessors.scomDataProcessor.configuration;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.expedia.edw.datapeek.common.configuration.PropertiesModule;
import com.expedia.edw.datapeek.dataProcessors.scomDataProcessor.results.DefaultScomFieldParser;
import com.expedia.edw.datapeek.dataProcessors.scomDataProcessor.results.ResultProcessor;
import com.expedia.edw.datapeek.dataProcessors.scomDataProcessor.results.ScomFieldParser;
import com.expedia.edw.datapeek.dataProcessors.scomDataProcessor.results.TimeSeriesResultProcessor;
import com.expedia.edw.datapeek.dataProcessors.scomDataProcessor.service.ScomService;
import com.expedia.edw.datapeek.dataProcessors.scomDataProcessor.service.ScomWebService;
import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;

/**
 * Guice configuration module for the Chieftain application.
 * 
 * @author dbauman
 * 
 */
public final class ConfigurationModule extends AbstractModule {

    public static final String PROPERTY_SCOM_CONNECT_TIMEOUT = "scom.connectTimeoutMillis";
    public static final String PROPERTY_SCOM_READ_TIMEOUT = "scom.readTimeoutMillis";
    public static final String PROPERTY_SCOM_USERNAME = "scom.username";
    public static final String PROPERTY_SCOM_PASSWORD = "scom.password";
    public static final String PROPERTY_SCOM_BASEURL = "scom.baseurl";
    public static final String PROPERTY_SCOM_CHECK_JOB_MILLIS = "scom.checkJobMillis";
    public static final String PROPERTY_SCOM_NO_JOB_TIMEOUT_MILLIS = "scom.noJobTimeoutMillis";
    public static final String PROPERTY_SCOM_TARGETMONITORTYPE = "scom.targetMonitorType";

    /**
     * Class logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationModule.class);

    @Override
    protected void configure() {

        ConfigurationModule.LOGGER.debug("Configuring Guice");

        /* Set default property values */
        final Map<String, String> defaultProperties = ImmutableMap.of(
                ConfigurationModule.PROPERTY_SCOM_CONNECT_TIMEOUT, "10000",
                ConfigurationModule.PROPERTY_SCOM_READ_TIMEOUT, "30000",
                ConfigurationModule.PROPERTY_SCOM_CHECK_JOB_MILLIS, "500",
                ConfigurationModule.PROPERTY_SCOM_NO_JOB_TIMEOUT_MILLIS, "6000");

        /* Include other modules */
        this.install(new PropertiesModule("dataprocessor.properties", defaultProperties));
        this.install(new JerseyModule());

        /* Add bindings here */
        this.bind(ScomService.class).to(ScomWebService.class);
        this.bind(ScomFieldParser.class).to(DefaultScomFieldParser.class);
        this.bind(ResultProcessor.class).to(TimeSeriesResultProcessor.class);
    }

}
