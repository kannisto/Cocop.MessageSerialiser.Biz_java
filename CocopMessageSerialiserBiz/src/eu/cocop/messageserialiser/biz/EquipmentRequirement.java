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

import org.mesa.xml.b2mml_v0600.EquipmentRequirementType;
import org.mesa.xml.b2mml_v0600.QuantityValueType;

import eu.cocop.messageserialiser.biz.QuantityValue;
import eu.cocop.messageserialiser.biz.InvalidMessageException;

/**
 * Represents an equipment-related requirement.
 * @author Petri Kannisto
 */
public final class EquipmentRequirement
{
	private final ArrayList<QuantityValue> m_quantities;
	
	/**
	 * Constructor.
	 */
	public EquipmentRequirement()
	{
		m_quantities = new ArrayList<>();
	}
	
	/**
	 * Constructor.
	 * @param proxy XML proxy.
	 * @throws InvalidMessageException Thrown if an error is encountered in the message.
	 */
	EquipmentRequirement(EquipmentRequirementType proxy) throws InvalidMessageException
	{
		m_quantities = new ArrayList<>();
		
		if (proxy.getQuantity() != null)
		{
			// Reading quantities
			for (QuantityValueType q : proxy.getQuantity())
			{
				QuantityValue quantityValue = new QuantityValue(q);
				m_quantities.add(quantityValue);
			}
		}
	}
	
	/**
	 * Quantities.
	 * @return Quantities.
	 */
	public ArrayList<QuantityValue> getQuantities()
	{
		return m_quantities;
	}
	
	/**
	 * Creates an XML proxy from the object.
	 * @return Proxy.
	 */
	EquipmentRequirementType toXmlProxy()
	{
		EquipmentRequirementType retval = new EquipmentRequirementType();
		
		// Add quantities
		for (QuantityValue q : m_quantities)
		{
			retval.getQuantity().add(q.toXmlProxy());
		}
		
		return retval;
	}
}
