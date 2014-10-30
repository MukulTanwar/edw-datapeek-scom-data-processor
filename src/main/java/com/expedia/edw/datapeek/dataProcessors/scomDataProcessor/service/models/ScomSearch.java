/*
 * Copyright 2013 Expedia, Inc. All rights reserved. EXPEDIA
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.expedia.edw.datapeek.dataProcessors.scomDataProcessor.service.models;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Contains the information for a Scom Search. This model is designed for the JSON output_mode.
 * 
 * @author dbauman
 * 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ScomSearch {

    private int diskUsage;
    private String dispatchState;
    private double doneProgress;
    private int dropCount;
    private DateTime earliestTime;
    private int eventAvailableCount;
    private int eventCount;
    private int eventFieldCount;
    private boolean isDone;
    private boolean isFailed;
    private boolean isFinalized;
    private boolean isPaused;
    private boolean isZombie;
    private String keywords;
    private DateTime latestTime;
    private ScomMessages messages;
    private int priority;
    private int resultCount;
    private double runDuration;
    private int scanCount;
    private String sid;

    /**
     * @return the diskUsage
     */
    public int getDiskUsage() {
        return this.diskUsage;
    }

    /**
     * @return the dispatchState
     */
    public String getDispatchState() {
        return this.dispatchState;
    }

    /**
     * @return the doneProgress
     */
    public double getDoneProgress() {
        return this.doneProgress;
    }

    /**
     * @return the dropCount
     */
    public int getDropCount() {
        return this.dropCount;
    }

    /**
     * @return the earliestTime
     */
    public DateTime getEarliestTime() {
        return this.earliestTime;
    }

    /**
     * @return the eventAvailableCount
     */
    public int getEventAvailableCount() {
        return this.eventAvailableCount;
    }

    /**
     * @return the eventCount
     */
    public int getEventCount() {
        return this.eventCount;
    }

    /**
     * @return the eventFieldCount
     */
    public int getEventFieldCount() {
        return this.eventFieldCount;
    }

    /**
     * @return the keywords
     */
    public String getKeywords() {
        return this.keywords;
    }

    /**
     * @return the latestTime
     */
    public DateTime getLatestTime() {
        return this.latestTime;
    }

    /**
     * @return the messages
     */
    public ScomMessages getMessages() {
        return this.messages;
    }

    /**
     * @return the priority
     */
    public int getPriority() {
        return this.priority;
    }

    /**
     * @return the resultCount
     */
    public int getResultCount() {
        return this.resultCount;
    }

    /**
     * @return the runDuration
     */
    public double getRunDuration() {
        return this.runDuration;
    }

    /**
     * @return the scanCount
     */
    public int getScanCount() {
        return this.scanCount;
    }

    /**
     * @return the sid
     */
    public String getSid() {
        return this.sid;
    }

    /**
     * @return the isDone
     */
    public boolean isDone() {
        return this.isDone;
    }

    /**
     * @return the isFailed
     */
    public boolean isFailed() {
        return this.isFailed;
    }

    /**
     * @return the isFinalized
     */
    public boolean isFinalized() {
        return this.isFinalized;
    }

    /**
     * @return the isPaused
     */
    public boolean isPaused() {
        return this.isPaused;
    }

    /**
     * @return the isZombie
     */
    public boolean isZombie() {
        return this.isZombie;
    }

    /**
     * @param diskUsage
     *            the diskUsage to set
     */
    public void setDiskUsage(final int diskUsage) {
        this.diskUsage = diskUsage;
    }

    /**
     * @param dispatchState
     *            the dispatchState to set
     */
    public void setDispatchState(final String dispatchState) {
        this.dispatchState = dispatchState;
    }

    /**
     * @param isDone
     *            the isDone to set
     */
    @JsonProperty("isDone")
    public void setDone(final boolean isDone) {
        this.isDone = isDone;
    }

    /**
     * @param doneProgress
     *            the doneProgress to set
     */
    public void setDoneProgress(final double doneProgress) {
        this.doneProgress = doneProgress;
    }

    /**
     * @param dropCount
     *            the dropCount to set
     */
    public void setDropCount(final int dropCount) {
        this.dropCount = dropCount;
    }

    /**
     * @param earliestTime
     *            the earliestTime to set
     */
    public void setEarliestTime(final DateTime earliestTime) {
        this.earliestTime = earliestTime;
    }

    /**
     * @param eventAvailableCount
     *            the eventAvailableCount to set
     */
    public void setEventAvailableCount(final int eventAvailableCount) {
        this.eventAvailableCount = eventAvailableCount;
    }

    /**
     * @param eventCount
     *            the eventCount to set
     */
    public void setEventCount(final int eventCount) {
        this.eventCount = eventCount;
    }

    /**
     * @param eventFieldCount
     *            the eventFieldCount to set
     */
    public void setEventFieldCount(final int eventFieldCount) {
        this.eventFieldCount = eventFieldCount;
    }

    /**
     * @param isFailed
     *            the isFailed to set
     */
    @JsonProperty("isFailed")
    public void setFailed(final boolean isFailed) {
        this.isFailed = isFailed;
    }

    /**
     * @param isFinalized
     *            the isFinalized to set
     */
    @JsonProperty("isFinalized")
    public void setFinalized(final boolean isFinalized) {
        this.isFinalized = isFinalized;
    }

    /**
     * @param keywords
     *            the keywords to set
     */
    public void setKeywords(final String keywords) {
        this.keywords = keywords;
    }

    /**
     * @param latestTime
     *            the latestTime to set
     */
    public void setLatestTime(final DateTime latestTime) {
        this.latestTime = latestTime;
    }

    /**
     * @param messages
     *            the messages to set
     */
    public void setMessages(final ScomMessages messages) {
        this.messages = messages;
    }

    /**
     * @param isPaused
     *            the isPaused to set
     */
    @JsonProperty("isPaused")
    public void setPaused(final boolean isPaused) {
        this.isPaused = isPaused;
    }

    /**
     * @param priority
     *            the priority to set
     */
    public void setPriority(final int priority) {
        this.priority = priority;
    }

    /**
     * @param resultCount
     *            the resultCount to set
     */
    public void setResultCount(final int resultCount) {
        this.resultCount = resultCount;
    }

    /**
     * @param runDuration
     *            the runDuration to set
     */
    public void setRunDuration(final double runDuration) {
        this.runDuration = runDuration;
    }

    /**
     * @param scanCount
     *            the scanCount to set
     */
    public void setScanCount(final int scanCount) {
        this.scanCount = scanCount;
    }

    /**
     * @param sid
     *            the sid to set
     */
    public void setSid(final String sid) {
        this.sid = sid;
    }

    /**
     * @param isZombie
     *            the isZombie to set
     */
    @JsonProperty("isZombie")
    public void setZombie(final boolean isZombie) {
        this.isZombie = isZombie;
    }
}
