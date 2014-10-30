/*
 * Copyright 2013 Expedia, Inc. All rights reserved. EXPEDIA
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.expedia.edw.datapeek.dataProcessors.scomDataProcessor.service.models;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Results for a Scom search job. Designed for JSON output using output_mode=json_cols
 * 
 * Sample
 * 
 * <pre>
 * {
 *    "fields": ["_time", "Col1", "Col2"],
 *    "columns": [
 *      ["2013-03-12T10:00:00.000-07:00", "2013-03-12T10:00:10.000-07:00"],
 *      ["0", null],
 *      ["1", "2"]
 *    ] 
 * }
 * </pre>
 * 
 * @author dbauman
 * 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ScomResults {
    private final List<String> fields = new ArrayList<>();

    private final List<List<String>> columns = new ArrayList<>();

    /**
     * @return the fields
     */
    public List<String> getFields() {
        return this.fields;
    }

    /**
     * @return the columns
     */
    public List<List<String>> getColumns() {
        return this.columns;
    }

}
