package edu.hawaii.kra.lookup;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.lookup.CollectionIncomplete;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.coreservice.framework.CoreFrameworkServiceLocator;

//KC-530 Lookup screens are too difficult for users, make searches easier by implementing search engine

public class UhSolrExternalSearch {
	
	protected static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(UhSolrExternalSearch.class);
	
	public List<? extends BusinessObject> getExternalSearchResults(@SuppressWarnings("rawtypes") Class c, Map<String, String> conf, String query) {
		ModifiableSolrParams p = new ModifiableSolrParams();
		p.set("q", query);
		return getExternalSearchResults(c, conf, p);
	}
	
	protected List<? extends BusinessObject> getExternalSearchResults(@SuppressWarnings("rawtypes") Class classObj, Map<String, String> conf, ModifiableSolrParams paramsFromCaller) {
		
        ConfigurationService configService = KRADServiceLocator.getKualiConfigurationService();
        String solrUrl = configService.getPropertyValueAsString("solr.url"); 
		
    	HttpSolrServer solr = new HttpSolrServer(solrUrl);
    	
    	ModifiableSolrParams params = new ModifiableSolrParams();
    	params.set("qt", "/browse");
    	params.set("sort","score desc, name_untouched asc");
    	params.set("mm", "25%");
    	params.set("fq","kind:" + conf.get("kind"));
    	params.set("rows", 100);

    	for (String p : paramsFromCaller.getParameterNames()) {
    		params.set(p, paramsFromCaller.get(p));
    	}

        List<BusinessObject> searchResultsReturn = new ArrayList<BusinessObject>();
    	Map<String, String> dbParams = new java.util.HashMap<String, String>();
    	String pk = conf.get("pk");
    	
    	String query_raw = params.get("q");
    	String[] terms = query_raw.split("\\s+");
    	String query = new String();

    	for (String t : terms) {
    		if (t.isEmpty())
    			continue;

    		query = query + " " + t;
    		
	    	if (t.length() < 1 || t.length() > 3)
	    		continue;

	    	try {
	    		String wildCardRegex = CoreFrameworkServiceLocator.getParameterService().getParameterValueAsString("KC-GEN","A","uh_solr_wildcard_regex");
		    	if (! t.matches(wildCardRegex))
		    		query = query + '*';
	    	} catch (Exception e) {
	    		LOG.warn("UHSEARCH: couldnt find wildcard regex parameter; using original query: " + query_raw);
	    		query = query_raw;
				e.printStackTrace();
	    	}

    	}

    	params.set("q", query);

        try {
			QueryResponse resp = solr.query(params);
			Iterator<SolrDocument> docs = resp.getResults().iterator();
	
			while(docs.hasNext()) {
				SolrDocument d = docs.next();
				String id = (String) d.getFieldValue("id");
		    	dbParams.put(pk, id);

		    	BusinessObject o = KRADServiceLocator.getBusinessObjectService().findByPrimaryKey(classObj, dbParams);

		    	if (o == null) {
		    		LOG.warn("UHSEARCH: couldnt find a BusinessObject (" + classObj.getName() + "," + pk + "," + id + ")");
		    	} else {
		    		searchResultsReturn.add(o);
		    	}
			}

		} catch (SolrServerException e) {
			e.printStackTrace();
		}
        return new CollectionIncomplete(searchResultsReturn, new Long(searchResultsReturn.size()));
    }

}

//KC-530 END

