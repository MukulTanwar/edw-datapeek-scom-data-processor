/*
 * Copyright 2013 Expedia, Inc. All rights reserved. EXPEDIA
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.expedia.edw.datapeek.dataProcessors.scomDataProcessor.results;

import com.expedia.edw.datapeek.common.models.Monitor;

/**
 * Creates a Monitor from a Scom field.
 * 
 * @author dbauman
 * 
 */
public interface ScomFieldParser {

    /**
     * Creates a Monitor from the Scom field name.
     * 
     * @param field
     *            The field (column name).
     * @return A Monitor instance.
     */
    Monitor parse(final String field);

}
