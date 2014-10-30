/*
 * Copyright 2013 Expedia, Inc. All rights reserved. EXPEDIA
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.expedia.edw.datapeek.dataProcessors.scomDataProcessor.service.models;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

/**
 * Xml serialization model for Messages. Used for the Search POST result.
 * 
 * @author dbauman
 * 
 */
public class ScomMessageXml {

    private String message;
    private String type;

    /**
     * @return the message
     */
    @XmlValue
    public String getMessage() {
        return this.message;
    }

    /**
     * @return the type
     */
    @XmlAttribute
    public String getType() {
        return this.type;
    }

    /**
     * @param message
     *            the message to set
     */
    public void setMessage(final String message) {
        this.message = message;
    }

    /**
     * @param type
     *            the type to set
     */
    public void setType(final String type) {
        this.type = type;
    }
}
