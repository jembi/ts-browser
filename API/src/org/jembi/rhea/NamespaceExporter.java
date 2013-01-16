/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.jembi.rhea;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jembi.rhea.TerminologyService.TSException;
import org.jembi.rhea.TerminologyService.TSNamespace;


/**
 * A service thread that periodically exports each namespace as a csv file.
 * This export is used by the website.
 *
 * @author Jembi Health Systems
 */
public class NamespaceExporter extends Thread {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	private static final String OUTPUT_DIR = "/var/www/export/";
	private TerminologyService service;
	
	
	public NamespaceExporter(TerminologyService service) {
		this.service = service;
	}

	@Override
	public void run() {
		List<TSNamespace> namespaces = null;
		try {
			namespaces = service.getAllNamespaces();
		} catch (TSException ex) {
			log.error(ex);
			return;
		}
			
		for (TSNamespace ns : namespaces) {
			log.info("Exporting namespace " + ns.getName() + " (" + ns.getCode() + ")");

			if ("LOINC-3".equals(ns.getCode())) {
				log.info("Skipping LOINC-3");
				continue;
			}
			
			try {
				String export = service.exportNamespace(ns.getId());
				File f = new File(OUTPUT_DIR + ns.getId() + ".csv");
				
				if (f.exists())
					f.delete();
				f.createNewFile();
				
				OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(f));
				try {
					out.write(export);
				} finally {
					out.flush();
					out.close();
				}
				
				log.info("Exported to " + f.getAbsolutePath());
			} catch (Exception ex) {
				log.error(ex);
			}
		}
	}
}
