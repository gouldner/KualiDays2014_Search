package edu.hawaii.organization.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kra.bo.Organization;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import edu.hawaii.kra.lookup.UhSolrExternalSearch;


public class UhOrganizationLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl{
	
	private static final long serialVersionUID = -1006355566366882023L;

	protected List<? extends BusinessObject> getSearchResultsHelper(Map<String, String> fieldValues, boolean unbounded) {
		// KC-530 Lookup screens are too difficult for users, make searches easier by implementing search engine
		UhSolrExternalSearch search = new UhSolrExternalSearch();
		Map<String, String> conf = new HashMap<String, String>();
		conf.put("kind", "ORGANIZATION");
		conf.put("pk", "organizationId");

		String q = fieldValues.get("organizationSearchInput");
		return search.getExternalSearchResults(Organization.class, conf, q);
		// KC-530 END
    }
}
