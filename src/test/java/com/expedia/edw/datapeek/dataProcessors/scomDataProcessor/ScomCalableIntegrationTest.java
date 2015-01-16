package com.expedia.edw.datapeek.dataProcessors.scomDataProcessor;
/*
 * Copyright 2013 Expedia, Inc. All rights reserved. EXPEDIA
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

import java.util.Set;
import java.util.concurrent.Callable;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

import com.expedia.edw.datapeek.common.WorkItem;
import com.expedia.edw.datapeek.common.models.Monitor;
import com.expedia.edw.datapeek.common.models.SimpleMonitorData;
import com.expedia.edw.datapeek.dataProcessor.WorkItemResult;

/**
 * Call a ScomCallable to process a query.
 * 
 * @author dbauman <dbauman@expedia.com>
 * 
 */

public class ScomCalableIntegrationTest {

	
	    /**
	     * Test Creating a search job and getting results.
	     * 
	     * @throws Exception
	     */
	    @Test
	    public final void testScomCallable() throws Exception {
	        final ScomCallableFactory factory = new ScomCallableFactory();
	        final Monitor monitor = new Monitor();
	        monitor.setId(173);
	        monitor.setType(33);
	        monitor.setName("TestMonitor");
	        monitor.putProperty("interval", "120");
	        monitor.putProperty("scomQuery",
	                "SOURCE=jdbc  driver=[jdbc:sqlserver://CHEXLISREP01.idx.expedmz.com:1433;databaseName=Datapeek;username=reader;password=reader;]   query=[SELECT PD.DateTime, hostinfo.hostname, CASE WHEN (PRI.InstanceName='' or PRI.InstanceName= null) THEN REPLACE(PR.ObjectName,'|','') ELSE REPLACE(PR.ObjectName + ', ' + PRI.InstanceName ,'|','') END as  PerfName  , PR.CounterName, SampleValue FROM CHSXSQLSCMLD02.OperationsManagerDW_CHLODPROD.Perf.vPerfRaw PD INNER JOIN CHSXSQLSCMLD02.OperationsManagerDW_CHLODPROD.dbo.vPerformanceRuleInstance AS PRI ON PD.PerformanceRuleInstanceRowId = PRI.PerformanceRuleInstanceRowId INNER JOIN CHSXSQLSCMLD02.OperationsManagerDW_CHLODPROD.dbo.vPerformanceRule AS PR ON PR.RuleRowId = PRI.RuleRowId INNER JOIN (SELECT hostname , hostid from (SELECT DISTINCT LEFT(ME.Name,CHARINDEX('.',ME.Name)-1) as hostname, ManagedEntityRowID as hostid FROM CHSXSQLSCMLD02.OperationsManagerDW_CHLODPROD.dbo.vManagedEntity ME WHERE Name LIKE '%.com%' OR Name LIKE '%karmalab.net%' union SELECT DISTINCT LEFT(ME.Path,CHARINDEX('.',ME.Path)-1) as hostname, ManagedEntityRowID as hostid FROM CHSXSQLSCMLD02.OperationsManagerDW_CHLODPROD.dbo.vManagedEntity ME WHERE  Path LIKE '%.com' OR Path LIKE '%karmalab.net') A ) hostinfo ON hostinfo.hostid = PD.ManagedEntityRowID WHERE PD.DateTime >= '2015-01-14 03:24:00.000' and PD.DateTime < '2015-01-14 03:25:00.000']  mode=[JSON]  | CACHE    | output=Data");

	        final WorkItem workItem = new WorkItem("key", monitor,
	                DateTime.now()
	                        .minusMinutes(10)
	                        .withSecondOfMinute(0)
	                        .withMillisOfSecond(0));
	        final WorkItem[] workItems = new WorkItem[] { workItem };
	        final Callable<WorkItemResult[]> callable = factory.create(workItems);

	        /* Execute */
	        final WorkItemResult result = callable.call()[0];

	        /* Log the results */
	        final Set<SimpleMonitorData> data = result.getMonitorData();
	        
	        for (final SimpleMonitorData datum : data) {
	            datum.debugLog();
	        }
	        Assert.assertTrue(result.isSuccess());
	    }

//	    /**
//	     * Test Creating a search job with invalid functions
//	     * 
//	     * @throws Exception
//	     */
//	    @Test
//	    public final void testScomCallableBadQueryPriority1Fatal() throws Exception {
//	        final ScomCallableFactory factory = new ScomCallableFactory();
//	        final Monitor monitor = new Monitor();
//	        monitor.setId(1);
//	        monitor.setType(33);
//	        monitor.setName("TestMonitor");
//	        monitor.putProperty("interval", "600");
//	        monitor.putProperty("priority", "1");
//	        monitor.putProperty("splunkQuery",
//	                "index=\"oms\" sourcetype=\"TripServicePerformanceLogs\" Message=\"TRIP_*\" " +
//	                        "| eval Description=\"a=b;c=d\" " +
//	                        "| timechart span=1m" +
//	                        "    blahblah(DurationMilliseconds)");
//
//	        final WorkItem workItem = new WorkItem("key", monitor, DateTime.now().minusHours(1));
//	        final WorkItem[] workItems = new WorkItem[] { workItem };
//	        final Callable<WorkItemResult[]> callable = factory.create(workItems);
//
//	        /* Execute */
//	        final WorkItemResult result = callable.call()[0];
//
//	        Assert.assertTrue(result.isSuccess());
//	        Assert.assertEquals(0, result.getMonitorData().size());
//	    }
//
//	    /**
//	     * Test Creating a search job with invalid functions
//	     * 
//	     * @throws Exception
//	     */
//	    @Test
//	    public final void testScomCallableBadQueryPriority2Fatal() throws Exception {
//	        final ScomCallableFactory factory = new ScomCallableFactory();
//	        final Monitor monitor = new Monitor();
//	        monitor.setId(1);
//	        monitor.setType(33);
//	        monitor.setName("TestMonitor");
//	        monitor.putProperty("interval", "600");
//	        monitor.putProperty("priority", "2");
//	        monitor.putProperty("splunkQuery",
//	                "index=\"oms\" sourcetype=\"TripServicePerformanceLogs\" Message=\"TRIP_*\" " +
//	                        "| eval Description=\"a=b;c=d\" " +
//	                        "| timechart span=1m" +
//	                        "    blahblah(DurationMilliseconds)");
//
//	        final WorkItem workItem = new WorkItem("key", monitor, DateTime.now().minusHours(1));
//	        final WorkItem[] workItems = new WorkItem[] { workItem };
//	        final Callable<WorkItemResult[]> callable = factory.create(workItems);
//
//	        /* Execute */
//	        final WorkItemResult result = callable.call()[0];
//
//	        Assert.assertTrue(result.isSuccess());
//	        Assert.assertEquals(0, result.getMonitorData().size());
//	    }
//
//	    /**
//	     * Test Creating a search job and getting results.
//	     * 
//	     * @throws Exception
//	     */
//	    @Test
//	    public final void testScomCallableWithSpan() throws Exception {
//	        final ScomCallableFactory factory = new ScomCallableFactory();
//	        final Monitor monitor = new Monitor();
//	        monitor.setId(1);
//	        monitor.setType(33);
//	        monitor.setName("TestMonitor");
//	        monitor.putProperty("interval", "60");
//	        monitor.putProperty("splunkQuery",
//	                "index=\"oms\" sourcetype=\"TripServicePerformanceLogs\" Message=\"TRIP_*\" " +
//	                        "| eval Description=\"a=b;c=d\" " +
//	                        "| timechart" +
//	                        "    exactperc50(DurationMilliseconds) as \"TP50 (ms)\"," +
//	                        "    exactperc99(DurationMilliseconds) as \"TP99 (ms)\"," +
//	                        "    count as \"Request Count\"," +
//	                        "    eval(count(eval(success2=\"false\"))) as \"Failed Count\"," +
//	                        "    eval(1-(count(eval(success2=\"false\"))/count)) as \"Success Rate\" " +
//	                        "by Description");
//
//	        final WorkItem workItem = new WorkItem("key", monitor, DateTime.now().minusHours(1));
//	        final WorkItem[] workItems = new WorkItem[] { workItem };
//	        final Callable<WorkItemResult[]> callable = factory.create(workItems);
//
//	        /* Execute */
//	        final WorkItemResult result = callable.call()[0];
//
//	        Assert.assertTrue(result.isSuccess());
//	        Assert.assertTrue(result.getMonitorData().size() > 0);
//	    }
//
//	    /**
//	     * Test a Monitor without a Scom query
//	     * 
//	     * @throws Exception
//	     */
//	    @Test
//	    public final void testScomCallableNoQuery() throws Exception {
//	        final ScomCallableFactory factory = new ScomCallableFactory();
//	        final Monitor monitor = new Monitor();
//	        monitor.setId(1);
//	        monitor.setType(33);
//	        monitor.setName("TestMonitor");
//	        monitor.putProperty("interval", "600");
//	        monitor.putProperty("sputnikQuery",
//	                "index=\"oms\" sourcetype=\"TripServicePerformanceLogs\" Message=\"TRIP_*\" " +
//	                        "| eval Description=\"a=b;c=d\" " +
//	                        "| timechart span=1m" +
//	                        "    blahblah(DurationMilliseconds)");
//
//	        final WorkItem workItem = new WorkItem("key", monitor, DateTime.now().minusHours(1));
//	        final WorkItem[] workItems = new WorkItem[] { workItem };
//	        final Callable<WorkItemResult[]> callable = factory.create(workItems);
//
//	        /* Execute */
//	        final WorkItemResult result = callable.call()[0];
//
//	        Assert.assertFalse(result.isSuccess());
//	        Assert.assertEquals(0, result.getMonitorData().size());
//	    }
//
//	    /**
//	     * Test Creating a search job and getting results. The query has no dimensions for the monitors, though, only "metric". So no monitors
//	     * should be created.
//	     * 
//	     * @throws Exception
//	     */
//	    @Test
//	    public final void testScomCallableBadMonitors() throws Exception {
//	        final ScomCallableFactory factory = new ScomCallableFactory();
//	        final Monitor monitor = new Monitor();
//	        monitor.setId(1);
//	        monitor.setType(33);
//	        monitor.setName("TestMonitor");
//	        monitor.putProperty("interval", "600");
//	        monitor.putProperty("splunkQuery",
//	                "index=\"oms\" sourcetype=\"TripServicePerformanceLogs\" Message=\"TRIP_*\" " +
//	                        "| timechart " +
//	                        "    exactperc50(DurationMilliseconds) as \"TP50 (ms)\"," +
//	                        "    exactperc99(DurationMilliseconds) as \"TP99 (ms)\"," +
//	                        "    count as \"Request Count\"," +
//	                        "    eval(count(eval(success2=\"false\"))) as \"Failed Count\"," +
//	                        "    eval(1-(count(eval(success2=\"false\"))/count)) as \"Success Rate\"");
//
//	        final WorkItem workItem = new WorkItem("key", monitor, DateTime.now().minusHours(1));
//	        final WorkItem[] workItems = new WorkItem[] { workItem };
//	        final Callable<WorkItemResult[]> callable = factory.create(workItems);
//
//	        /* Execute */
//	        final WorkItemResult result = callable.call()[0];
//
//	        /* Log the results */
//	        final Set<SimpleMonitorData> data = result.getMonitorData();
//	        for (final SimpleMonitorData datum : data) {
//	            datum.debugLog();
//	        }
//
//	        Assert.assertTrue(result.isSuccess());
//	        Assert.assertEquals(0, data.size());
//	    }
//	
//
//	
}
