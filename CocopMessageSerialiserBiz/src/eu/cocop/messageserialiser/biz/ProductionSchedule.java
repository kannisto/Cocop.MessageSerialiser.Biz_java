// 
// Please make sure to read and understand README.md and LICENSE.txt.
// 
// This file was prepared in the research project COCOP (Coordinating
// Optimisation of Complex Industrial Processes).
// https://cocop-spire.eu/
// Author: Petri Kannisto, Tampere University, Finland
// Last modified: 4/2020
// 
// This file has been derived the XML schemata of Business to Manufacturing 
// Markup Language (B2MML). B2MML has the following license agreement:
// 
// 'This Manufacturing Enterprise Solutions Association (MESA International)
// Work (including specifications, documents, software, and related items)
// referred to as the Business To Manufacturing Markup Language (B2MML) is
// provided by the copyright holders under the following license. Permission
// to use, copy, modify, or redistribute this Work and its documentation, with
// or without modification, for any purpose and without fee or royalty is
// hereby granted provided MESA is acknowledged as the originator of this Work
// using the following statement: "The Business To Manufacturing Markup
// Language (B2MML) is used courtesy of the Manufacturing Enterprise Solutions
// Association (MESA International)." In no event shall MESA, its members, or
// any third party be liable for any costs, expenses, losses, damages or
// injuries incurred by use of the Work or as a result of this agreement.'
// 
// The B2MML XML schemata are available at http://www.mesa.org/en/B2MML.asp
// 
// Please note: This software has *not* received any official compliance check
// with B2MML. This software was *not* created by MESA International.

package eu.cocop.messageserialiser.biz;

import java.util.ArrayList;
import java.util.TreeMap;

import org.mesa.xml.b2mml_v0600.ProductionRequestType;
import org.mesa.xml.b2mml_v0600.ProductionScheduleType;

import eu.cocop.messageserialiser.biz.ProductionRequest;
import eu.cocop.messageserialiser.biz.InvalidMessageException;

/**
 * Represents a production schedule that can request to realise multiple
 * production entities.
 * @author Petri Kannisto
 */
public final class ProductionSchedule
{
	private final ArrayList<ProductionRequest> m_productionRequests  = new ArrayList<>();
	
	
	/**
	 * Constructor.
	 */
	public ProductionSchedule()
	{
		// Empty ctor body
	}
	
	/**
	 * Constructor.
	 * @param xmlBytes XML proxy.
	 * @throws InvalidMessageException Thrown if an error is encountered.
	 */
	ProductionSchedule(ProductionScheduleType proxy) throws InvalidMessageException
	{
		if (proxy.getProductionRequest() != null)
        {
            // Read production requests
			for (ProductionRequestType requestProxy : proxy.getProductionRequest())
            {
            	ProductionRequest request = new ProductionRequest(requestProxy); // throws InvalidMessageException
            	m_productionRequests.add(request);
            }
        }
	}
	
	/**
	 * Production requests.
	 * @return Production requests.
	 */
	public ArrayList<ProductionRequest> getProductionRequests()
	{
		return m_productionRequests;
	}
	
	/**
	 * Generates an XML proxy.
	 * @param idPrefix ID prefix to enable the generation of unique IDs within the document.
	 * @return Proxy.
	 */
	ProductionScheduleType toXmlProxy(String idPrefix)
	{
		ProductionScheduleType proxy = new ProductionScheduleType();
		
		// Add production requests
		for (int a = 0; a < m_productionRequests.size(); ++a)
		{
			ProductionRequest req = m_productionRequests.get(a);
			String idPrefixLocal = String.format("%sSched_i%d-", idPrefix, (a+1)); // Would give, e.g., "(prefix)Sched_i2-"
			ProductionRequestType requestProxy = req.toXmlProxy(idPrefixLocal);
			
			proxy.getProductionRequest().add(requestProxy);
		}
		
		return proxy;
	}
	
	/**
	 * Provides any extra types needed in serialisation.
	 * @return Extra types or an empty collection if none exist.
	 */
	TreeMap<String, Class<?>> getExtraTypes()
	{
		TreeMap<String, Class<?>> extraTypes = new TreeMap<>();
		
		for (int a = 0; a < m_productionRequests.size(); ++a)
		{
			ProductionRequest req = m_productionRequests.get(a);
			extraTypes.putAll(req.getExtraTypes());
		}
		
		return extraTypes;
	}
}
