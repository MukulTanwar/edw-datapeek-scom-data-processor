/*
 * Copyright 2013 Expedia, Inc. All rights reserved. EXPEDIA
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.expedia.edw.datapeek.dataProcessors.scomDataProcessor.configuration;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

/**
 * Custom Jackson ObjectMapper provider. Adds JodaTime deserialization support.
 * 
 * @author dbauman
 * 
 */
@Provider
public class ObjectMapperProvider implements ContextResolver<ObjectMapper> {
    private final ObjectMapper mapper;

    /**
     * Default Constructor.
     */
    public ObjectMapperProvider() {
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JodaModule());
    }

    /**
     * Gets the Object Mapper.
     */
    @Override
    public ObjectMapper getContext(final Class<?> type) {
        return this.mapper;
    }
}
