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

import org.mesa.xml.b2mml_v0600.EquipmentIDType;
import org.mesa.xml.b2mml_v0600.HierarchyScopeType;

import eu.cocop.messageserialiser.biz.EquipmentElementLevelType;
import eu.cocop.messageserialiser.biz.IdentifierType;
import eu.cocop.messageserialiser.biz.InvalidMessageException;

/**
 * Indicates the scope of equipment within the plant hierarchy.
 * @author Petri Kannisto
 */
public final class HierarchyScope
{
	private IdentifierType m_equipmentId = null;
	private EquipmentElementLevelType m_equipmentElementLevel = EquipmentElementLevelType.Other;
	
	/**
	 * Constructor.
	 * @param eqId Equipment ID.
	 * @param lev Equipment element level.
	 * @throws IllegalArgumentException Thrown if equipment ID is null.
	 */
	public HierarchyScope(IdentifierType eqId, EquipmentElementLevelType lev) throws IllegalArgumentException
	{
		if (eqId == null || eqId.getValue() == null)
		{
			throw new IllegalArgumentException("Equipment ID must not be null in hierarchy scope");
		}
		
		m_equipmentId = eqId;
		m_equipmentElementLevel = lev;
	}
	
	/**
	 * Constructor.
	 * @param xmlBytes XML proxy.
	 * @throws InvalidMessageException Thrown if an error is encountered.
	 */
	HierarchyScope(HierarchyScopeType proxy) throws InvalidMessageException
	{
		try
		{
			m_equipmentId = new IdentifierType(proxy.getEquipmentID());
			String eqLevRaw = proxy.getEquipmentElementLevel().getValue();
			m_equipmentElementLevel = EquipmentElementLevelType.valueOf(eqLevRaw);
		}
		catch (IllegalArgumentException e)
		{
			throw new InvalidMessageException("Invalid equipment element level", e);
		}
		catch (NullPointerException e)
		{
			throw new InvalidMessageException("Failed to read HierarchyScope - something expected is missing", e);
		}
	}

	/**
	 * Equipment ID.
	 * @return Equipment ID.
	 */
	public IdentifierType getEquipmentIdentifier()
	{
		return m_equipmentId;
	}
	
	/**
	 * Equipment element level.
	 * @return Equipment element level.
	 */
	public EquipmentElementLevelType getEquipmentElementLevel()
	{
		return m_equipmentElementLevel;
	}
	
	/**
	 * Creates an XML proxy from the object.
	 * @return Proxy.
	 */
	HierarchyScopeType toXmlProxy()
	{
		HierarchyScopeType proxy = new HierarchyScopeType();
		
		// Equipment ID
		EquipmentIDType equipmentIdProxy = new EquipmentIDType();
		m_equipmentId.populateXmlProxy(equipmentIdProxy);
		proxy.setEquipmentID(equipmentIdProxy);
		
		// Write equipment element level
		org.mesa.xml.b2mml_v0600.EquipmentElementLevelType levProxy = new org.mesa.xml.b2mml_v0600.EquipmentElementLevelType();
		levProxy.setValue(m_equipmentElementLevel.name());
		proxy.setEquipmentElementLevel(levProxy);
		
		return proxy;
	}
}
