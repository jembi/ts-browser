/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.jembi.rhea;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.jembi.rhea.impl.ApelonServiceImpl;

/**
 * The main factory for retrieving an instance of the terminology service
 *
 * @author Jembi Health Systems
 */
public class TerminologyServiceFactory {
	
	private static TerminologyService INSTANCE;
	private static ScheduledExecutorService EXEC_SERVICE;
	private static Object lock = new Object();
	
	public TerminologyService getDefaultInstance() {
		synchronized (lock) {
			if (INSTANCE==null)
				INSTANCE = new ApelonServiceImpl();
			
			//launch the namespace csv exporter thread if isn't already running
			if (EXEC_SERVICE==null) {
				EXEC_SERVICE = Executors.newSingleThreadScheduledExecutor();
				EXEC_SERVICE.scheduleWithFixedDelay(new NamespaceExporter(INSTANCE), 0, 6, TimeUnit.HOURS);
			}
		
			return INSTANCE;
		}
	}
}
