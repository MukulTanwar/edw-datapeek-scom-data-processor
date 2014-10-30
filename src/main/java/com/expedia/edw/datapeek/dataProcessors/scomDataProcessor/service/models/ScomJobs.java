/*
 * Copyright 2013 Expedia, Inc. All rights reserved. EXPEDIA
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.expedia.edw.datapeek.dataProcessors.scomDataProcessor.service.models;

import java.util.List;

/**
 * Contains the information for multiple Search Jobs. This model is designed for the JSON output_mode.
 * 
 * This model supports both of these URLs: /search/jobs?output_mode=json or /search/jobs/searchId?output_mode=json
 * 
 * The 2nd one returns a data array of one.
 * 
 * @author dbauman
 * 
 */
public class ScomJobs {
    private int count;
    private List<ScomSearch> data;
    private int offset;
    private int total;

    /**
     * @return the count
     */
    public int getCount() {
        return this.count;
    }

    /**
     * @return the data
     */
    public List<ScomSearch> getData() {
        return this.data;
    }

    /**
     * @return the offset
     */
    public int getOffset() {
        return this.offset;
    }

    /**
     * @return the total
     */
    public int getTotal() {
        return this.total;
    }

    /**
     * @param count
     *            the count to set
     */
    public void setCount(final int count) {
        this.count = count;
    }

    /**
     * @param data
     *            the data to set
     */
    public void setData(final List<ScomSearch> data) {
        this.data = data;
    }

    /**
     * @param offset
     *            the offset to set
     */
    public void setOffset(final int offset) {
        this.offset = offset;
    }

    /**
     * @param total
     *            the total to set
     */
    public void setTotal(final int total) {
        this.total = total;
    }

    /**
     * If the data List has at least one element, returns the first element.
     * 
     * @return The first ScomSearch in the Jobs list.
     */
    public ScomSearch first() {
        if (this.data == null || this.data.size() == 0) {
            return null;
        }
        return this.data.get(0);
    }
}
