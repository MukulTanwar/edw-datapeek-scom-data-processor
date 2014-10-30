/*
 * Copyright 2013 Expedia, Inc. All rights reserved. EXPEDIA
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.expedia.edw.datapeek.dataProcessors.scomDataProcessor.service.models;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Response for posting a new search job.
 * 
 * @author dbauman
 * 
 */
@XmlRootElement(name = "response")
public class PostSearchResponse {

    private List<ScomMessageXml> messages = new ArrayList<>();

    private String sid;

    /**
     * @return the messages
     */
    @XmlElementWrapper(name = "messages")
    @XmlElement(name = "msg")
    public List<ScomMessageXml> getMessages() {
        return this.messages;
    }

    /**
     * @return the sid
     */
    public String getSid() {
        return this.sid;
    }

    /**
     * @param messages
     *            the messages to set
     */
    public void setMessages(final List<ScomMessageXml> messages) {
        this.messages = messages;
    }

    /**
     * @param sid
     *            the sid to set
     */
    public void setSid(final String sid) {
        this.sid = sid;
    }
}
