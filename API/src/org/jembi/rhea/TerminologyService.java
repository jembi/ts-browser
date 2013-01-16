/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.jembi.rhea;

import java.util.List;

/**
 * The terminology service interface
 *
 * @author Jembi Health Systems
 */
public interface TerminologyService {

	public interface TSNamespace {
		public int getId();
		public void setId(int id);
		public String getCode();
		public void setCode(String code);
		public String getName();
		public void setName(String name);
		public List<TSTerm> getRoots();
	}

	public interface TSProperty {
		public String getName();
		public void setName(String name);
		public String getValue();
		public void setValue(String value);
	}

	public interface TSTerm {
		public String getCode();
		public void setCode(String code);
		public String getName();
		public void setName(String name);
		public TSNamespace getNamespace();
		public void setNamespace(TSNamespace namespace);
		public String getPreferredTerm();
		public void setPreferredTerm(String preferredTerm);
		public List<TSTerm> getSynonyms();
		public void setSynonyms(List<TSTerm> synonyms);
		public List<TSProperty> getProperties();
		public void setProperties(List<TSProperty> properties);
		public void setSuperConcepts(List<TSTerm> superConcepts);
		public void setSubConcepts(List<TSTerm> subConcepts);
		public List<TSTerm> getSuperConcepts();
		public List<TSTerm> getSubConcepts();
		public boolean getHasSubConcepts();
		public String getHasSubConceptsAsString();
	}


	/* Browsing */

	public TSTerm getTerm(String code, int namespaceId) throws TSException;

	public List<TSTerm> getRootTerms(int namespaceId) throws TSException;

	public boolean validateTerm(String conceptCode, String namespaceCode) throws TSException;
	
	//helper for php
	public String validateTerm_ReturnAs1or0String(String code, String namespaceCode) throws TSException;

	/* Searching */

	public List<TSTerm> search(String query) throws TSException;
	
	public List<TSTerm> search(String query, boolean exact) throws TSException;

	public List<TSTerm> search(List<Integer> namespaceIds, String query, boolean exact) throws TSException;
	
	public List<TSTerm> search(List<Integer> namespaceIds, String query, boolean exact, int maxResultsPerNamespace) throws TSException;

	/* Namespaces */

	public TSNamespace lookupNamespace(int id) throws TSException;

	public TSNamespace lookupNamespace(String code) throws TSException;

	public List<TSNamespace> getAllNamespaces() throws TSException;
	
	public List<TSProperty> getAllPropertyTypes(int namespaceId) throws TSException;

	/* Exporting */

	/**
	 * Export a specified namespace as a CSV string. Traversal is done depth-first.
	 */
	public String exportNamespace(int namespaceId) throws TSException;

	/**
	 * Export a single term as a CSV string
	 */
	public String exportTerm(String code, int namespaceId) throws TSException;
	
	
	public static class TSException extends Exception {
		private static final long serialVersionUID = 1L;

		public TSException(String msg) {
			super(msg);
		}
		
		public TSException(Throwable ex) {
			super(ex);
		}
	}
}
