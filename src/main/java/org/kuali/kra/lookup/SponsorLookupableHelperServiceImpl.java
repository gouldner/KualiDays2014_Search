/*
 * Copyright 2005-2010 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kra.lookup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kra.bo.Sponsor;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import edu.hawaii.kra.lookup.UhSolrExternalSearch;


public class SponsorLookupableHelperServiceImpl  extends KualiLookupableHelperServiceImpl {
    private static final String HIERARCHY_NAME = "hierarchyName";
    private static final String SELECTED_HIERARCHY_NAME = "selectedHierarchyName";

    /**
     * 
     * @see org.kuali.core.lookup.KualiLookupableHelperServiceImpl#getSearchResults(java.util.Map)
     * This is primarily for multiple value lookup.  also need to take care of single value lookup
     */
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
		// KC-530 Lookup screens are too difficult for users, make searches easier by implementing search engine
		UhSolrExternalSearch search = new UhSolrExternalSearch();
		Map<String, String> conf = new HashMap<String, String>();
		conf.put("kind", "SPONSOR");
		conf.put("pk", "sponsorCode");

		String q = fieldValues.get("sponsorSearchInput");
		return search.getExternalSearchResults(Sponsor.class, conf, q);
		// KC-530 END
    }
}

