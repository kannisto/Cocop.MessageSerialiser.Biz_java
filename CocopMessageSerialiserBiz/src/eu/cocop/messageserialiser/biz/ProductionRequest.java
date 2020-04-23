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
import org.mesa.xml.b2mml_v0600.SegmentRequirementType;

import eu.cocop.messageserialiser.biz.HierarchyScope;
import eu.cocop.messageserialiser.biz.IdentifierType;
import eu.cocop.messageserialiser.biz.SegmentRequirement;
import eu.cocop.messageserialiser.biz.InvalidMessageException;

/**
 * Represents a request for a certain production entity.
 * @author Petri Kannisto
 */
public final class ProductionRequest
{
	private final ArrayList<SegmentRequirement> m_segmentRequirements;
	
	private IdentifierType m_identifier = null;
	private HierarchyScope m_hierarchyScope = null;
	private Object m_schedulingParams = null;
	
	
	/**
	 * Constructor
	 */
	public ProductionRequest()
	{
		m_segmentRequirements = new ArrayList<>();
	}
	
	/**
	 * Constructor.
	 * @param xmlBytes XML proxy.
	 * @throws InvalidMessageException Thrown if an error is encountered.
	 */
	ProductionRequest(ProductionRequestType proxy) throws InvalidMessageException
	{
		m_segmentRequirements = new ArrayList<>();
		
		// Read identifier
		if (proxy.getID() != null)
		{
			m_identifier = new IdentifierType(proxy.getID());
		}
		
		// Read hierarchy scope
		if (proxy.getHierarchyScope() != null)
        {
            m_hierarchyScope = new HierarchyScope(proxy.getHierarchyScope()); // throws InvalidMessageException
        }

		// Read segment requirements
        if (proxy.getSegmentRequirement() != null)
        {
            for (SegmentRequirementType segReq : proxy.getSegmentRequirement())
            {
                SegmentRequirement req = new SegmentRequirement(segReq); // throws InvalidMessageException
            	m_segmentRequirements.add(req);
            }
        }
        
        // Read scheduling parameters
        if (proxy.getSchedulingParameters() != null)
        {
        	m_schedulingParams = proxy.getSchedulingParameters();
        }
	}
	
	/**
	 * Segment requirements.
	 * @return Segment requirements.
	 */
	public ArrayList<SegmentRequirement> getSegmentRequirements()
	{
		return m_segmentRequirements;
	}

	/**
	 * Identifier.
	 * @return Identifier.
	 */
	public IdentifierType getIdentifier()
	{
		return m_identifier;
	}
	
	/**
	 * Identifier.
	 * @return Identifier.
	 */
	public void setIdentifier(IdentifierType id)
	{
		m_identifier = id;
	}
	
	/**
	 * Hierarchy scope.
	 * @return Hierarchy scope.
	 */
	public HierarchyScope getHierarchyScope()
	{
		return m_hierarchyScope;
	}
	
	/**
	 * Hierarchy scope.
	 * @param h Hierarchy scope.
	 */
	public void setHierarchyScope(HierarchyScope h)
	{
		m_hierarchyScope = h;
	}
	
	/**
	 * Scheduling parameters.
	 * @return Scheduling parameters.
	 */
	public Object getSchedulingParameters()
	{
		return m_schedulingParams;
	}
	
	/**
	 * Scheduling parameters.
	 * @param par Scheduling parameters.
	 */
	public void setSchedulingParameters(Object par)
	{
		m_schedulingParams = par;
	}
	
	/**
	 * Creates an XML proxy from the object.
	 * @param idPrefix ID prefix to enable the generation of unique IDs within the document.
	 * @return Proxy.
	 */
	ProductionRequestType toXmlProxy(String idPrefix)
	{
		ProductionRequestType retval = new ProductionRequestType();
		
		// Set identifier
		if (m_identifier != null)
		{
			org.mesa.xml.b2mml_v0600.IdentifierType idProxy = new org.mesa.xml.b2mml_v0600.IdentifierType();
			m_identifier.populateXmlProxy(idProxy);
			retval.setID(idProxy);
		}
		
		// Set hierarchy scope
		if (m_hierarchyScope != null)
		{
			retval.setHierarchyScope(m_hierarchyScope.toXmlProxy());
		}
		
		// Add segment requirements
		for (SegmentRequirement req : m_segmentRequirements)
		{
			retval.getSegmentRequirement().add(req.toXmlProxy());
		}
		
		// Add scheduling parameters (if any)
		if (m_schedulingParams != null)
		{
			retval.setSchedulingParameters(m_schedulingParams);
		}
		
		return retval;
	}
	
	/**
	 * Provides any extra types needed in serialisation.
	 * @return Extra types or an empty collection if none exist.
	 */
	TreeMap<String, Class<?>> getExtraTypes()
	{
		TreeMap<String, Class<?>> retval = new TreeMap<>(); 
		
		if (m_schedulingParams != null)
		{
			Class<?> classInfo = m_schedulingParams.getClass();
			retval.put(classInfo.getCanonicalName(), classInfo);
		}
		
		return retval;
	}
}
