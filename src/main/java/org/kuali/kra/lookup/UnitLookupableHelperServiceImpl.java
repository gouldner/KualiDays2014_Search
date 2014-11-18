/*
 * Copyright 2005-2013 The Kuali Foundation
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kra.bo.Unit;
import org.kuali.kra.infrastructure.Constants;
import org.kuali.kra.infrastructure.PermissionConstants;
import org.kuali.kra.service.KcPersonService;
import org.kuali.kra.service.UnitAuthorizationService;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import edu.hawaii.kra.lookup.UhSolrExternalSearch;


/**
 * Unit lookup that accounts for the extra parameter {@code campusCode} and filters the search results if it is defined.
 */
public class UnitLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    private static final long serialVersionUID = -3661085880649722426L;
    
    private static final String CAMPUS_CODE_FIELD = "code";
    private static final String CAMPUS_LOOKUPABLE_CLASS_NAME = "org.kuali.rice.location.impl.campus.CampusBo";

    private KcPersonService kcPersonService;
    private UnitAuthorizationService unitAuthorizationService;
    
    @Override
    public List<HtmlData> getCustomActionUrls(BusinessObject businessObject, List pkNames) {
        List<HtmlData> htmlDataList = new ArrayList<HtmlData>();
        String personId = getKcPersonService().getKcPersonByPersonId(GlobalVariables.getUserSession().getPerson().getPrincipalId()).getPersonId();
        boolean hasModifyPermission = getUnitAuthorizationService().hasPermission(personId, "KC-UNT", PermissionConstants.MODIFY_UNIT);
        if (hasModifyPermission) {
            AnchorHtmlData editHtmlData = getUrlData(businessObject, KRADConstants.MAINTENANCE_EDIT_METHOD_TO_CALL, pkNames);
            htmlDataList.add(editHtmlData);

            AnchorHtmlData copyHtmlData = getUrlData(businessObject, KRADConstants.MAINTENANCE_COPY_METHOD_TO_CALL, pkNames);
            htmlDataList.add(copyHtmlData);

            AnchorHtmlData deleteHtmlData = getUrlData(businessObject, KRADConstants.MAINTENANCE_DELETE_METHOD_TO_CALL, pkNames);
            htmlDataList.add(deleteHtmlData);
        }
        return htmlDataList;
    }
    
  
	protected List<? extends BusinessObject> getSearchResultsHelper(Map<String, String> fieldValues, boolean unbounded) {
		// KC-530 Lookup screens are too difficult for users, make searches easier by implementing search engine
		UhSolrExternalSearch search = new UhSolrExternalSearch();
		Map<String, String> conf = new HashMap<String, String>();
		conf.put("kind", "UNIT");
		conf.put("pk", "unitNumber");

		String q = fieldValues.get("unitSearchInput");
		return search.getExternalSearchResults(Unit.class, conf, q);
		// KC-530 END
    }

    public KcPersonService getKcPersonService() {
        return kcPersonService;
    }

    public void setKcPersonService(KcPersonService kcPersonService) {
        this.kcPersonService = kcPersonService;
    }

    public UnitAuthorizationService getUnitAuthorizationService() {
        return unitAuthorizationService;
    }

    public void setUnitAuthorizationService(UnitAuthorizationService unitAuthorizationService) {
        this.unitAuthorizationService = unitAuthorizationService;
    }

}