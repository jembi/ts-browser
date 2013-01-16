/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */
package org.jembi.rhea.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jembi.rhea.TerminologyService;

import com.apelon.apelonserver.client.ServerConnection;
import com.apelon.apelonserver.client.ServerConnectionSocket;
import com.apelon.dts.client.DTSException;
import com.apelon.dts.client.association.AssociationQuery;
import com.apelon.dts.client.association.AssociationType;
import com.apelon.dts.client.association.ConceptAssociation;
import com.apelon.dts.client.association.Synonym;
import com.apelon.dts.client.attribute.DTSProperty;
import com.apelon.dts.client.attribute.DTSPropertyType;
import com.apelon.dts.client.concept.ConceptAttributeSetDescriptor;
import com.apelon.dts.client.concept.ConceptChild;
import com.apelon.dts.client.concept.ConceptParent;
import com.apelon.dts.client.concept.DTSConcept;
import com.apelon.dts.client.concept.DTSConceptQuery;
import com.apelon.dts.client.concept.DTSSearchOptions;
import com.apelon.dts.client.concept.NavChildContext;
import com.apelon.dts.client.concept.NavParentContext;
import com.apelon.dts.client.concept.NavQuery;
import com.apelon.dts.client.concept.OntylogConcept;
import com.apelon.dts.client.concept.OntylogConceptQuery;
import com.apelon.dts.client.concept.SearchQuery;
import com.apelon.dts.client.concept.ThesaurusConceptQuery;
import com.apelon.dts.client.namespace.Namespace;
import com.apelon.dts.client.namespace.NamespaceQuery;
import com.apelon.dts.client.namespace.NamespaceType;

/**
 * An Apelon DTS implementation for the terminology service
 *
 * @author Jembi Health Systems
 */
public class ApelonServiceImpl implements TerminologyService {
	
	protected final Log log = LogFactory.getLog(getClass());
	
	
	public class ApelonNamespace implements TerminologyService.TSNamespace {
		protected Namespace ns;
		private int id;
		private String code;
		private String name;

		private ApelonNamespace(Namespace ns) {
			this.ns = ns;
			id = ns.getId();
			code = ns.getCode();
			name = ns.getName();
		}

		@Override
		public int getId() {
			return id;
		}

		@Override
		public void setId(int id) {
			this.id = id;
		}

		@Override
		public String getCode() {
			return code;
		}

		@Override
		public void setCode(String code) {
			this.code = code;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public void setName(String name) {
			this.name = name;
		}

		@Override
		@SuppressWarnings({ "rawtypes", "unchecked" })
		public List<TSTerm> getRoots() {
			try {
				if (ns.getNamespaceType().equals(NamespaceType.ONTYLOG_EXTENSION)) {
					List res = new ArrayList(1);
					res.add(new PseudoRoot(ns));
					return res;
				}
					
				NavQuery navQry = NavQuery.createInstance(getConn());
				ConceptChild[] roots = navQry.getConceptChildRoots(asd, id);
				List<TSTerm> res = new ArrayList<TSTerm>(roots.length);
				for (ConceptChild c : roots)
					res.add(new ApelonTerm(c));
				return res;
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}
	}

	public class ApelonProperty implements TerminologyService.TSProperty {
		private String name, value;
		protected DTSPropertyType _propTypeRef;

		private ApelonProperty(String name, String value) {
			this.name = name;
			this.value = value;
		}

		private ApelonProperty(DTSPropertyType type) {
			this(type.getName(), null);
			_propTypeRef = type;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public void setName(String name) {
			this.name = name;
		}

		@Override
		public String getValue() {
			return value;
		}

		@Override
		public void setValue(String value) {
			this.value = value;
		}
	}

	public class ApelonTerm implements TerminologyService.TSTerm {
		private DTSConcept concept;
		private String code;
		private String name;
		private TSNamespace namespace;
		private String preferredTerm;
		private List<TSTerm> synonyms;
		private List<TSProperty> properties;
		private List<TSTerm> superConcepts = null;
		private List<TSTerm> subConcepts = null;
		private Boolean hasSubConcepts = null;

		private ApelonTerm() {}
		
		private ApelonTerm(DTSConcept concept) throws TerminologyService.TSException {
			this.concept = concept;
			code = concept.getCode();
			name = concept.getName();
			namespace = lookupNamespace(concept.getNamespaceId());

			Synonym pref = concept.getFetchedPreferredTerm();
			if (pref!=null)
				preferredTerm = pref.getConcept().getName();
			else
				preferredTerm = name;

			synonyms = new ArrayList<TSTerm>();
			for (Synonym s : concept.getFetchedSynonyms()) {
				synonyms.add(new ApelonTerm(s.getConcept()));
			}

			properties = new ArrayList<TSProperty>();
			for (DTSProperty prop : concept.getFetchedProperties()) {
				properties.add(new ApelonProperty(prop.getName(), prop.getValue()));
			}
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public TSNamespace getNamespace() {
			return namespace;
		}

		public void setNamespace(TSNamespace namespace) {
			this.namespace = namespace;
		}

		public String getPreferredTerm() {
			return preferredTerm;
		}

		public void setPreferredTerm(String preferredTerm) {
			this.preferredTerm = preferredTerm;
		}

		public List<TSTerm> getSynonyms() {
			return synonyms;
		}

		public void setSynonyms(List<TSTerm> synonyms) {
			this.synonyms = synonyms;
		}

		public List<TSProperty> getProperties() {
			return properties;
		}

		public void setProperties(List<TSProperty> properties) {
			this.properties = properties;
		}

		public void setSuperConcepts(List<TSTerm> superConcepts) {
			this.superConcepts = superConcepts;
		}

		public void setSubConcepts(List<TSTerm> subConcepts) {
			this.subConcepts = subConcepts;
			hasSubConcepts = (subConcepts!=null) ? subConcepts.size()>0 : false;
		}

		public List<TSTerm> getSuperConcepts() {
			try {
				if (superConcepts==null) {
					NavQuery navQry = NavQuery.createInstance(getConn());
					NavParentContext ctx = navQry.getNavParentContext((OntylogConcept)concept, asd);
					superConcepts = new ArrayList<TSTerm>();
					
					if (ctx.getParents()==null || ctx.getParents().length==0) {
						loadSuperconceptsThroughAssociations();
					} else {
						for (ConceptParent cp : ctx.getParents()) {
							superConcepts.add(new ApelonTerm(cp));
						}
					}
					
					Collections.sort(superConcepts, new Comparator<TSTerm>() {
						public int compare(TSTerm o1, TSTerm o2) {
							if (o1==o2) return 0;
							return o1.getName().compareToIgnoreCase(o2.getName());
						}
					});
				}
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
			return superConcepts;
		}

		public List<TSTerm> getSubConcepts() {
			try {
				if (subConcepts==null) {
					NavQuery navQry = NavQuery.createInstance(getConn());
					NavChildContext ctx = navQry.getNavChildContext((OntylogConcept)concept, asd);
					subConcepts = new ArrayList<TSTerm>();

					if (ctx.getChildren()==null || ctx.getChildren().length==0) {
						loadSubconceptsThroughAssociations();
					} else {
						for (ConceptChild cp : ctx.getChildren()) {
							ApelonTerm term = new ApelonTerm(cp);
							term.hasSubConcepts = cp.getHasChildren();
							subConcepts.add(term);
						}
					}
					
					Collections.sort(subConcepts, new Comparator<TSTerm>() {
						public int compare(TSTerm o1, TSTerm o2) {
							if (o1==o2) return 0;
							return o1.getName().compareToIgnoreCase(o2.getName());
						}
					});
				}
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
			return subConcepts;
		}

		public boolean getHasSubConcepts() {
			try {
				if (hasSubConcepts==null) {
					hasSubConcepts = getSubConcepts().size() > 0;
				}
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
			return hasSubConcepts.booleanValue();
		}

		public String getHasSubConceptsAsString() {
			return getHasSubConcepts() ? "true" : "false";
		}
		
		private void loadSuperconceptsThroughAssociations() throws TerminologyService.TSException, DTSException {
			for (ConceptAssociation cca : fetchInverseConceptAssociations(concept.getNamespaceId(), code, "Parent Of")) {
				ApelonTerm term = new ApelonTerm(cca.getFromConcept());
				term.hasSubConcepts = true;
				superConcepts.add(term);
			}
		}

		private void loadSubconceptsThroughAssociations() throws TerminologyService.TSException, DTSException {
			for (ConceptAssociation cca : fetchConceptAssociations(concept.getNamespaceId(), code, "Parent Of")) {
				ApelonTerm term = new ApelonTerm(cca.getToConcept());
				term.hasSubConcepts = fetchConceptAssociations(concept.getNamespaceId(), cca.getToConcept().getCode(), "Parent Of").length > 0;
				subConcepts.add(term);
			}
		}
	}
	
	/**
	 * If the namespace is an ontylog extension, we return a non-existent "pseudo-root".
	 * This can then be used by the getRoots and getTerm methods to add handling for ontylog extensions.
	 * 
	 * TODO I'm sure there's a better way than using fake roots
	 */
	private class PseudoRoot extends ApelonTerm {
		
		public static final String CODE = "Ontylog Extension Pseudo Root";
		
		private PseudoRoot(Namespace ns) {
			this(new ApelonNamespace(ns));
		}
		
		private PseudoRoot(TerminologyService.TSNamespace ns) {
			setCode(CODE);
			setNamespace(ns);
		}

		@Override
		public List<TSTerm> getSubConcepts() {
			try {
				List<TSTerm> res = new ArrayList<TSTerm>();
				ApelonNamespace lns = (ApelonNamespace)lookupNamespace( ((ApelonNamespace)getNamespace()).ns.getLinkedNamespaceId() );
				res.addAll(lns.getRoots());
				//TODO this currently works for our Rwanda Extension, but isn't generic
				res.add( getTerm("C1", getNamespace().getId()) );
				return res;
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}

		@Override
		public boolean getHasSubConcepts() {
			return true;
		}
	}

	private ServerConnection _conn;
	private ConceptAttributeSetDescriptor asd;


	public ApelonServiceImpl() {
		asd = new ConceptAttributeSetDescriptor("allProps");
		asd.setAllPropertyTypes(true);
		log.info("Instantiated new Apelon terminology service instance");
	}


	private synchronized ServerConnection getConn() throws TerminologyService.TSException {
		try {
			if (_conn==null)
				_conn = new ServerConnectionSocket("localhost", 6666);
			return _conn;
		} catch (Exception ex) {
			throw new TerminologyService.TSException(ex);
		}
	}

	/* Browsing */

	public TSTerm getTerm(String code, int namespaceId) throws TerminologyService.TSException {
		try {
			if (code.equals(PseudoRoot.CODE))
				return new PseudoRoot(lookupNamespace(namespaceId));
			
			DTSConcept c = ThesaurusConceptQuery.createInstance(getConn()).findConceptByCode(code, namespaceId, asd);
			
			if (c==null) {
				ApelonNamespace ns = (ApelonNamespace)lookupNamespace(namespaceId);
				if (ns.ns.getNamespaceType().equals(NamespaceType.ONTYLOG_EXTENSION))
				c = ThesaurusConceptQuery.createInstance(getConn()).findConceptByCode(code, ns.ns.getLinkedNamespaceId(), asd);
			}
			
			return c!=null ? new ApelonTerm(c) : null;
		} catch (DTSException ex) {
			throw new TerminologyService.TSException(ex);
		}
	}

	public List<TSTerm> getRootTerms(int namespaceId) throws TerminologyService.TSException {
		try {
			OntylogConcept[] cs = NavQuery.createInstance(getConn()).
				getConceptChildRoots(asd, namespaceId);
			List<TSTerm> res = new ArrayList<TSTerm>();
			for (OntylogConcept c : cs)
				res.add(new ApelonTerm(c));
			return res;
		} catch (DTSException ex) {
			throw new TerminologyService.TSException(ex);
		}
	}

	private ConceptAssociation[] fetchConceptAssociations(int namespaceId, String code, String name) throws TerminologyService.TSException, DTSException {
		ConceptAttributeSetDescriptor ca = new ConceptAttributeSetDescriptor(name + " asd");
		ca.addConceptAssociationType( AssociationQuery.createInstance(getConn()).findAssociationTypeByName(name, namespaceId) );
		OntylogConcept c = (OntylogConcept)OntylogConceptQuery.createInstance(getConn()).findConceptByCode(code, namespaceId, ca);
		ConceptAssociation[] res = c.getFetchedConceptAssociations();
		return res!=null ? res : new ConceptAssociation[0];
	}

	private ConceptAssociation[] fetchInverseConceptAssociations(int namespaceId, String code, String name) throws TerminologyService.TSException, DTSException {
		ConceptAttributeSetDescriptor ca = new ConceptAttributeSetDescriptor(name + " asd");
		ca.addInverseConceptAssociationType( AssociationQuery.createInstance(getConn()).findAssociationTypeByName(name, namespaceId) );
		OntylogConcept c = (OntylogConcept)OntylogConceptQuery.createInstance(getConn()).findConceptByCode(code, namespaceId, ca);
		ConceptAssociation[] res = c.getFetchedInverseConceptAssociations();
		return res!=null ? res : new ConceptAssociation[0];
	}
	
	public boolean validateTerm(String conceptCode, String namespaceCode) throws TerminologyService.TSException {
		try {
			ServerConnection conn = getConn();
			NamespaceQuery qry = NamespaceQuery.createInstance(conn);
			Namespace ns = qry.findNamespaceByCode(namespaceCode);
			if (ns==null)
				return false;
			
			DTSConcept c = ThesaurusConceptQuery.createInstance(conn).
				findConceptByCode(conceptCode, ns.getId(), ConceptAttributeSetDescriptor.NO_ATTRIBUTES);
			
			if (c==null && ns.getNamespaceType().equals(NamespaceType.ONTYLOG_EXTENSION))
				c = ThesaurusConceptQuery.createInstance(conn).
					findConceptByCode(conceptCode, ns.getLinkedNamespaceId(), ConceptAttributeSetDescriptor.NO_ATTRIBUTES);
			
			return c!=null;
		} catch (DTSException ex) {
			throw new TerminologyService.TSException(ex);
		}
	}
	
	public String validateTerm_ReturnAs1or0String(String code, String namespaceCode) throws TerminologyService.TSException {
		return validateTerm(code, namespaceCode) ? "1" : "0";
	}

	/* Searching */

	public List<TSTerm> search(String query) throws TerminologyService.TSException {
		return search(query, false);
	}
	
	public List<TSTerm> search(String query, boolean exact) throws TerminologyService.TSException {
		return search(null, query, exact);
	}

	public List<TSTerm> search(List<Integer> namespaceIds, String query, boolean exact) throws TerminologyService.TSException {
		return search(namespaceIds, query, exact, -1);
	}
	
	public List<TSTerm> search(List<Integer> namespaceIds, String query, boolean exact, int maxResultsPerNamespace) throws TerminologyService.TSException {
		if (namespaceIds==null || namespaceIds.isEmpty())
			return search((Integer)null, query, exact, maxResultsPerNamespace);
		
		List<TSTerm> res = new LinkedList<TerminologyService.TSTerm>();
		for (Integer id : namespaceIds) {
			res.addAll( search(id, query, exact, maxResultsPerNamespace) );
		}
		return res;
	}
	
	private List<TSTerm> search(Integer namespaceId, String query, boolean exact, int maxResultsPerNamespace) throws TerminologyService.TSException {
		try {
			DTSSearchOptions searchOpts = new DTSSearchOptions();
			
			if (namespaceId!=null)
				searchOpts.setNamespaceId(namespaceId);
			if (maxResultsPerNamespace>0)
				searchOpts.setLimit(maxResultsPerNamespace);
			searchOpts.setAttributeSetDescriptor(asd);
			
			SearchQuery searchQry = SearchQuery.createInstance(getConn());
			
			String theQuery = buildQueryString(query, exact);
			if (theQuery.length()<3 || (theQuery.charAt(0)=='*' && theQuery.length()<5))
				return Collections.emptyList();
			
			DTSConcept[] cons = searchQry.findConceptsWithNameMatching(theQuery, searchOpts, true);

			//TODO Lookup by code
			//if (namespaceId!=null && (cons==null || cons.length==0)) {
			//	//look for code
			//	DTSPropertyType type = getCodeInSourcePropertyType(namespaceId);
			//	if (type!=null) {
			//		cons = searchQry.findConceptsWithPropertyMatching(type, theQuery, searchOpts);
			//	}
			//}
	
			List<TSTerm> res = new LinkedList<TSTerm>();
			for (DTSConcept c : cons)
				res.add(new ApelonTerm(c));
			return res;
		} catch (Exception ex) {
			throw new TerminologyService.TSException(ex);
		}
	}

	private DTSPropertyType getCodeInSourcePropertyType(int namespaceId) throws TerminologyService.TSException {
		for (TSProperty prop : getAllPropertyTypes(namespaceId)) {
			if ("Code in Source".equalsIgnoreCase(prop.getName()))
				return ((ApelonProperty)prop)._propTypeRef;
		}
		return null;
	}
	
	private static String buildQueryString(String query, boolean exact) {
		StringBuilder scrubbed = new StringBuilder();
		for (int i=0; i<query.length(); i++) {
			if (Character.isLetterOrDigit(query.charAt(i)) || Character.isWhitespace(query.charAt(i)))
				scrubbed.append(query.charAt(i));
		}
		
		if (exact)
			return scrubbed.toString();
		
		StringBuilder res = new StringBuilder();
		res.append("*");
		for (String s : scrubbed.toString().split("\\s+"))
			res.append(s + "*");
		return res.toString();
	}

	/* Namespaces */

	public TSNamespace lookupNamespace(int id) throws TerminologyService.TSException {
		try {
			NamespaceQuery qry = NamespaceQuery.createInstance(getConn());
			Namespace ns = qry.findNamespaceById(id);
			return ns!=null ? new ApelonNamespace(ns) : null;
		} catch (Exception ex) {
			throw new TerminologyService.TSException(ex);
		}
	}

	public TSNamespace lookupNamespace(String code) throws TerminologyService.TSException {
		try {
			NamespaceQuery qry = NamespaceQuery.createInstance(getConn());
			Namespace ns = qry.findNamespaceByCode(code);
			return ns!=null ? new ApelonNamespace(ns) : null;
		} catch (Exception ex) {
			throw new TerminologyService.TSException(ex);
		}
	}

	public List<TSNamespace> getAllNamespaces() throws TerminologyService.TSException {
		try {
			NamespaceQuery qry = NamespaceQuery.createInstance(getConn());
			List<TSNamespace> res = new ArrayList<TSNamespace>();
	
			for (Namespace ns : qry.getNamespaces())
				res.add(new ApelonNamespace(ns));
	
			return res;
		} catch (Exception ex) {
			throw new TerminologyService.TSException(ex);
		}
	}
	
	public List<TSProperty> getAllPropertyTypes(int namespaceId) throws TSException {
		try {
			DTSPropertyType types[] = DTSConceptQuery.createInstance(getConn()).getConceptPropertyTypes(namespaceId);
			if (types==null || types.length==0)
				return Collections.emptyList();
			
			List<TSProperty> res = new ArrayList<TerminologyService.TSProperty>(types.length);
			for (DTSPropertyType type : types) {
				res.add(new ApelonProperty(type));
			}
			return res;
		} catch (Exception ex) {
			throw new TerminologyService.TSException(ex);
		}
	}

	/* Exporting */

	private static class _TSTreeNode {
		List<TSTerm> breadth;
		int i;

		public _TSTreeNode(List<TSTerm> breadth, int i) {
			this.breadth = breadth;
			this.i = i;
		}
	}

	/**
	 * Export a specified namespace as a CSV string. Traversal is done depth-first.
	 */
	public String exportNamespace(int namespaceId) throws TerminologyService.TSException {
		StringBuilder res = new StringBuilder();
		
		try {
			LinkedList<_TSTreeNode> cStack = new LinkedList<_TSTreeNode>();
			List<TSTerm> breadth = getRootTerms(namespaceId);
			List<TSProperty> props = getAllPropertyTypes(namespaceId);
			int i = 0;
			ThesaurusConceptQuery cQuery = ThesaurusConceptQuery.createInstance(getConn());
			
			res.append("\"Code\",\"Name\"");
			for (TSProperty prop : props)
				res.append(",\"" + prop.getName() + "\"");
			res.append("\n");
	
			while (i<breadth.size()) {
				ApelonTerm term = (ApelonTerm)breadth.get(i);
				//properties aren't being fetched for sub-concepts, so look up the term to fetch it's properties
				DTSProperty[] termProps = cQuery.findConceptById(term.concept.getId(), namespaceId, asd).getFetchedProperties();
				
				res.append("\"" + term.getCode() + "\",\"" + term.getName() + "\"");
				for (TSProperty prop : props) {
					boolean addedProp = false;
					
					for (DTSProperty termProp : termProps) {
						if (termProp.getName().equals(prop.getName())) {
							res.append(",\"" + termProp.getValue() + "\"");
							addedProp = true;
						}
					}
					
					if (!addedProp)
						res.append(",\"\"");
				}
				res.append("\n");
				
				if (term.getHasSubConcepts()) {
					cStack.push(new _TSTreeNode(breadth, i));
					breadth = term.getSubConcepts();
					i = 0;
					continue;
				}
	
				while (i+1==breadth.size() && !cStack.isEmpty()) {
					_TSTreeNode node = cStack.pop();
					breadth = node.breadth;
					i = node.i;
				}
	
				i++;
			}
		} catch (Exception ex) {
			throw new TerminologyService.TSException(ex);
		}

		return res.toString();
	}

	
	public String exportTerm(String code, int namespaceId) throws TerminologyService.TSException {
		StringBuilder res = new StringBuilder();
		ApelonTerm term = (ApelonTerm)getTerm(code, namespaceId);
		
		res.append("\"Code\",\"Name\"");
		for (TSProperty prop : term.properties)
			res.append(",\"" + prop.getName() + "\"");
		res.append("\n");
		
		res.append("\"" + term.getCode() + "\",\"" + term.getName() + "\"");
		for (TSProperty prop : term.getProperties())
			res.append(",\"" + prop.getValue() + "\"");
		res.append("\n");
		
		return res.toString();
	}
}
