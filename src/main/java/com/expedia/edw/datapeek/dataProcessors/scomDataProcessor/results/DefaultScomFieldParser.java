/*
 * Copyright 2013 Expedia, Inc. All rights reserved. EXPEDIA
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.expedia.edw.datapeek.dataProcessors.scomDataProcessor.results;

import javax.inject.Inject;
import javax.inject.Named;

import com.expedia.edw.datapeek.common.models.Monitor;
import com.expedia.edw.datapeek.dataProcessors.scomDataProcessor.configuration.ConfigurationModule;

/**
 * Creates a Monitor from a Scom field. Parses the string into the properties that compose the monitor.
 * 
 * @author dbauman
 * 
 */
public class DefaultScomFieldParser implements ScomFieldParser {

    @Inject
    public DefaultScomFieldParser(
            @Named(ConfigurationModule.PROPERTY_SCOM_TARGETMONITORTYPE) final int targetMonitorType) {
        this.targetMonitorType = targetMonitorType;
    }

    private final int targetMonitorType;
    private static final String MONITOR_PROPERTY_DELIMITER = ";";
    private static final String MONITOR_PROPERTY_KEY_DELIMITER = "=";

    /**
     * Creates a Monitor from the Scom field name. Supports either of these formats:
     * 
     * "metric: key1=value1;key2=value2;key3=value3" "metric"
     * 
     * @param field
     *            The field (column name).
     * @return A Monitor instance.
     */
    @Override
    public Monitor parse(final String field) {
        if (field == null) {
            throw new IllegalArgumentException("Field is null");
        }

        final Monitor monitor = new Monitor();

        /* Metric property comes after a : sometimes */
        final int colonIndex = field.indexOf(':');

        String metric = null;
        if (colonIndex < 0) {
            /* Just metric in field */
            metric = field;
        } else {
            /* Split metric and additional fields */
            metric = field.substring(0, colonIndex);

            final String itemString = field.substring(colonIndex + 2);
            final String[] items = itemString.split(DefaultScomFieldParser.MONITOR_PROPERTY_DELIMITER);

            /* Add each additional property */
            if (items.length > 0) {

                /* Parse each delimited property */
                for (final String item : items) {
                    final int equalIndex = item.indexOf(DefaultScomFieldParser.MONITOR_PROPERTY_KEY_DELIMITER);

                    if (equalIndex > 0) {
                        final String name = item.substring(0, equalIndex);
                        final String value = item.substring(equalIndex + 1);

                        monitor.putProperty(name.toLowerCase(), value);
                    }
                }
            }
        }

        /* Add metric property */
        monitor.putProperty("metric", metric);

        /* Add monitor type from config */
        monitor.setType(this.targetMonitorType);

        monitor.setDescription("Created by Scom Data Processor");

        return monitor;
    }
}
