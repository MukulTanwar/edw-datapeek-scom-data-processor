/*
 * Copyright 2013 Expedia, Inc. All rights reserved. EXPEDIA
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.expedia.edw.datapeek.dataProcessors.scomDataProcessor.service.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Sub-model for ScomSearch. Contains messages for each job.
 * 
 * @author dbauman
 * 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ScomMessages {
    private List<String> error;
    private List<String> fatal;
    private List<String> info;
    private List<String> warn;

    /**
     * @return the error messages.
     */
    public List<String> getError() {
        return this.error;
    }

    /**
     * @return the fatal messages.
     */
    public List<String> getFatal() {
        return this.fatal;
    }

    /**
     * @return the info messages.
     */
    public List<String> getInfo() {
        return this.info;
    }

    /**
     * @return the warning messages.
     */
    public List<String> getWarn() {
        return this.warn;
    }

    /**
     * @param error
     *            the error messages to set
     */
    public void setError(final List<String> error) {
        this.error = error;
    }

    /**
     * @param fatal
     *            the fatal messages to set
     */
    public void setFatal(final List<String> fatal) {
        this.fatal = fatal;
    }

    /**
     * @param info
     *            the info messages to set
     */
    public void setInfo(final List<String> info) {
        this.info = info;
    }

    /**
     * @param warn
     *            the warning messages to set
     */
    public void setWarn(final List<String> warn) {
        this.warn = warn;
    }
}
