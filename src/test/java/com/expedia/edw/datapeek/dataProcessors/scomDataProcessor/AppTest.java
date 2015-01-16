/*
 * Copyright 2013 Expedia, Inc. All rights reserved. EXPEDIA
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.expedia.edw.datapeek.dataProcessors.scomDataProcessor;

import org.junit.Test;

import com.expedia.edw.datapeek.dataProcessors.scomDataProcessor.App;
/**
 * @author mutanwar
 *
 */

	public class AppTest {

	    @Test
	    public void testApp() throws InterruptedException {
	        App.main(new String[2]);
	        Thread.sleep(1000);
	        App.shutdown();
	    }

	}



