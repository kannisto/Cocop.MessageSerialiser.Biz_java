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

import org.mesa.xml.b2mml_v0600.MaterialDefinitionIDType;
import org.mesa.xml.b2mml_v0600.MaterialLotIDType;
import org.mesa.xml.b2mml_v0600.MaterialRequirementType;
import org.mesa.xml.b2mml_v0600.QuantityValueType;

import eu.cocop.messageserialiser.biz.IdentifierType;
import eu.cocop.messageserialiser.biz.MaterialRequirement;
import eu.cocop.messageserialiser.biz.MaterialUse;
import eu.cocop.messageserialiser.biz.QuantityValue;
import eu.cocop.messageserialiser.biz.InvalidMessageException;

/**
 * Represents a material-related requirement.
 * @author Petri Kannisto
 */
public final class MaterialRequirement
{
	private final ArrayList<IdentifierType> m_materialDefinitionIdentifiers;
	private final ArrayList<IdentifierType> m_materialLotIdentifiers;
	private MaterialUse m_materialUse = null;
	private final ArrayList<QuantityValue> m_quantities;
	private final ArrayList<MaterialRequirement> m_assemblyRequirements;
	
	
	/**
	 * Constructor.
	 */
	public MaterialRequirement()
	{
		m_materialDefinitionIdentifiers = new ArrayList<>();
		m_materialLotIdentifiers = new ArrayList<>();
		m_quantities = new ArrayList<>();
		m_assemblyRequirements = new ArrayList<>();
	}
	
	/**
	 * Constructor.
	 * @param xmlBytes XML proxy.
	 * @throws InvalidMessageException Thrown if an error is encountered.
	 */
	MaterialRequirement(MaterialRequirementType proxy) throws InvalidMessageException
	{
		m_materialDefinitionIdentifiers = new ArrayList<>();
		m_materialLotIdentifiers = new ArrayList<>();
		m_quantities = new ArrayList<>();
		m_assemblyRequirements = new ArrayList<>();
		
		// Reading material definition ID
		if (proxy.getMaterialDefinitionID() != null)
		{
			for (MaterialDefinitionIDType defProxy : proxy.getMaterialDefinitionID())
			{
				m_materialDefinitionIdentifiers.add(new IdentifierType(defProxy));
			}
		}
		
		// Reading material lot ID
		if (proxy.getMaterialLotID() != null)
		{
			for (MaterialLotIDType idProxy : proxy.getMaterialLotID())
			{
				m_materialLotIdentifiers.add(new IdentifierType(idProxy));
			}
		}
		
		// Reading material use
		if (proxy.getMaterialUse() != null)
		{
			m_materialUse = new MaterialUse(proxy.getMaterialUse());
		}
		
		// Reading quantity
		if (proxy.getQuantity() != null)
        {
            for (QuantityValueType qItem : proxy.getQuantity())
            {
                QuantityValue quantity = new QuantityValue(qItem);
            	m_quantities.add(quantity);
            }
        }
		
		// Reading assembly requirements
		if (proxy.getAssemblyRequirement() != null)
		{
			for (MaterialRequirementType req : proxy.getAssemblyRequirement())
			{
				MaterialRequirement matReq = new MaterialRequirement(req);
				m_assemblyRequirements.add(matReq);
			}
		}
	}

	/**
	 * Material definition identifiers.
	 * @return Material definition identifiers.
	 */
	public ArrayList<IdentifierType> getMaterialDefinitionIdentifiers()
	{
		return m_materialDefinitionIdentifiers;
	}
	
	/**
	 * Material lot identifiers.
	 * @return Material lot identifiers.
	 */
	public ArrayList<IdentifierType> getMaterialLotIdentifiers()
	{
		return m_materialLotIdentifiers;
	}
	
	/**
	 * Material use.
	 * @return Material use.
	 */
	public MaterialUse getMaterialUse()
	{
		return m_materialUse;
	}
	
	/**
	 * Material use.
	 * @param m Material use.
	 */
	public void setMaterialUse(MaterialUse m)
	{
		m_materialUse = m;
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
	 * Enclosed material requirements. Use to specify the composition of a material.
	 * @return Material requirements.
	 */
	public ArrayList<MaterialRequirement> getAssemblyRequirements()
	{
		return m_assemblyRequirements;
	}
	
	/**
	 * Creates an XML proxy from the object.
	 * @return Proxy.
	 */
	MaterialRequirementType toXmlProxy()
	{
		MaterialRequirementType retval = new MaterialRequirementType();
		
		// Add material definition IDs
		for (IdentifierType id : m_materialDefinitionIdentifiers)
		{
			MaterialDefinitionIDType defIdProxy = new MaterialDefinitionIDType();
			id.populateXmlProxy(defIdProxy);
			retval.getMaterialDefinitionID().add(defIdProxy);
		}
		
		// Add material lot IDs
		for (IdentifierType id : m_materialLotIdentifiers)
		{
			MaterialLotIDType lotIdProxy = new MaterialLotIDType();
			id.populateXmlProxy(lotIdProxy);
			retval.getMaterialLotID().add(lotIdProxy);
		}
		
		// Add material use if defined
		if (m_materialUse != null)
		{
			retval.setMaterialUse(m_materialUse.toXmlProxy());
		}
		
		// Add quantities
		for (QuantityValue q : m_quantities)
		{
			retval.getQuantity().add(q.toXmlProxy());
		}
		
		// Add assembly requirements
		for (MaterialRequirement req : m_assemblyRequirements)
		{
			retval.getAssemblyRequirement().add(req.toXmlProxy());
		}
		
		return retval;
	}
}
