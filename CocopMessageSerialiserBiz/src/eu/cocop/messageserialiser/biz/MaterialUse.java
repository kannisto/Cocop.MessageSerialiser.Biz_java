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

import eu.cocop.messageserialiser.biz.InvalidMessageException;


/**
 * Represents the use of a material.
 * @author Petri Kannisto
 */
public final class MaterialUse
{
	private MaterialUseType m_value;
	
	
	/**
	 * Constructor.
	 * @param value Material use value.
	 */
	public MaterialUse(MaterialUseType value)
	{
		m_value = value;
	}
	
	/**
	 * Constructor.
	 * @param proxy XML proxy.
	 * @throws InvalidMessageException Thrown if an error is encountered.
	 */
	MaterialUse(org.mesa.xml.b2mml_v0600.MaterialUseType proxy) throws InvalidMessageException
	{
		m_value = parseMaterialUse(proxy.getValue());
	}
	
	/**
	 * Material use value.
	 * @return Material use value.
	 */
	public MaterialUseType getValue()
	{
		return m_value;
	}
	
	/**
	 * Creates an XML proxy from the object.
	 * @return Proxy.
	 */
	org.mesa.xml.b2mml_v0600.MaterialUseType toXmlProxy()
	{
		org.mesa.xml.b2mml_v0600.MaterialUseType matUseProxy = new org.mesa.xml.b2mml_v0600.MaterialUseType();
		String matUseString = materialUseToString(m_value);
		matUseProxy.setValue(matUseString);
		
		return matUseProxy;
	}
	
	private MaterialUseType parseMaterialUse(String s) throws InvalidMessageException
	{
		if (s == null || s.isEmpty())
		{
			throw new InvalidMessageException("Material use value cannot be an empty");
		}
		
		// Replace spaces with an underscore
		s = s.replace(" ", "_");
		
		try
		{
			// Parse
			return MaterialUseType.valueOf(s);
		}
		catch (IllegalArgumentException e)
		{
			throw new InvalidMessageException("Invalid material use value", e);
		}
	}
	
	private String materialUseToString(MaterialUseType input)
	{
		String myString = input.name();
		
		// Replace underscores with spaces
		return myString.replace("_", " ");
	}
}
