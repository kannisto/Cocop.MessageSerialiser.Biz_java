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

import org.mesa.xml.b2mml_v0600.DataTypeType;
import org.mesa.xml.b2mml_v0600.ObjectFactory;
import org.mesa.xml.b2mml_v0600.QuantityStringType;
import org.mesa.xml.b2mml_v0600.QuantityValueType;
import org.mesa.xml.b2mml_v0600.UnitOfMeasureType;

import eu.cocop.messageserialiser.biz.DataType;
import eu.cocop.messageserialiser.biz.IdentifierType;
import eu.cocop.messageserialiser.biz.InvalidMessageException;
import eu.cocop.messageserialiser.biz.XmlHelper;

/**
 * Represents a quantity value.
 * 
 * The respective XML type is loose and flexible in terms
 * of validating the string presentation of the quantity value.
 * Therefore, the application developers
 * *must* take care that the quantity string is encoded and
 * parsed correctly. Fortunately, this class provides help
 * for utilising some common data types.
 * 
 * Use the DataType property to indicate the type of the raw quantity
 * string. Please note that this class does not validate the
 * combination of DataType and the format of the quantity string
 * in any way. However, certain constructors accept only a certain
 * type for the value (such as QuantityValue(double val)). These
 * constructors are safe to use, because they not only encode
 * the value for XML but also set the DataType respectively.
 * 
 * There is also support for parsing the quantity string for
 * certain data types. This is realised with methods, such as
 * tryParseValueAsXmlDouble().
 * @author Petri Kannisto
 */
public final class QuantityValue
{
	private final String m_valueAsString;
	private final DataType m_dataType;
	
	private String m_unitOfMeasure = null;
	private IdentifierType m_key = null;
	
	
	/**
	 * Constructor.
	 * @param val Value.
	 */
	public QuantityValue(double val)
	{
		m_valueAsString = XmlHelper.serialiseXmlDouble(val);
		m_dataType = new DataType(DataType.TypeType.doubleXml);
	}
	
	/**
	 * Constructor.
	 * @param val Value.
	 */
	public QuantityValue(boolean val)
	{
		m_valueAsString = XmlHelper.serialiseXmlBoolean(val);
		m_dataType = new DataType(DataType.TypeType.booleanXml);
	}
	
	/**
	 * Constructor.
	 * @param val Value.
	 */
	public QuantityValue(int val)
	{
		m_valueAsString = XmlHelper.serialiseXmlInt(val);
		m_dataType = new DataType(DataType.TypeType.intXml);
	}
	
	/**
	 * Constructor.
	 * @param val Value.
	 * @param typ Data type.
	 */
	public QuantityValue(String val, DataType typ)
	{
		if (val == null)
		{
			val = "";
		}
		
		m_valueAsString = val;
		m_dataType = typ;
	}
	
	/**
	 * Constructor.
	 * @param val Value.
	 */
	public QuantityValue(String val)
	{
		if (val == null)
		{
			val = "";
		}
		
		m_valueAsString = val;
		m_dataType = null;
	}
	
	/**
	 * Constructor.
	 * @param xmlBytes XML proxy.
	 * @throws InvalidMessageException Thrown if an error is encountered.
	 */
	QuantityValue(QuantityValueType proxy) throws InvalidMessageException
	{
		if (proxy.getQuantityString() == null)
		{
			throw new InvalidMessageException("Quantity value is required");
		}
		
    	m_valueAsString = proxy.getQuantityString().getValue();
    	
    	if (proxy.getDataType() != null && proxy.getDataType().getValue() != null)
    	{
    		m_dataType = new DataType(proxy.getDataType().getValue()); // throws InvalidMessageException
    	}
    	else
    	{
    		m_dataType = null;
    	}
        
        if (proxy.getUnitOfMeasure() != null && proxy.getUnitOfMeasure().getValue() != null)
        {
        	m_unitOfMeasure = proxy.getUnitOfMeasure().getValue().getValue();
        }
        
        if (proxy.getKey() != null)
        {
        	m_key = new IdentifierType(proxy.getKey());
        }
	}
	
	/**
	 * Data type.
	 * @return Data type.
	 */
	public DataType getDataType()
	{
		return m_dataType;
	}
	
	/**
	 * Unit of measure.
	 * @return Unit of measure.
	 */
	public String getUnitOfMeasure()
	{
		return m_unitOfMeasure;
	}
	
	/**
	 * Unit of measure.
	 * @param uom Unit of measure.
	 */
	public void setUnitOfMeasure(String uom)
	{
		m_unitOfMeasure = uom;
	}

	/**
	 * Returns the raw quantity string.
	 * @return Raw quantity string.
	 */
	public String getRawQuantityString()
	{
		return m_valueAsString;
	}
	
	/**
	 * Returns the key if any.
	 * @return Key.
	 */
	public IdentifierType getKey()
	{
		return m_key;
	}
	
	/**
	 * Sets the key.
	 * @param key Key.
	 */
	public void setKey(IdentifierType key)
	{
		m_key = key;
	}
	
	/**
	 * Attempts to parse the quantity string as an XML "double".
	 * @return Value as double.
	 * @throws NumberFormatException Thrown if parsing fails.
	 */
	public double tryParseValueAsXmlDouble() throws NumberFormatException
	{
		return XmlHelper.parseXmlDouble(m_valueAsString); // throws IllegalArgumentException
	}
	
	/**
	 * Attempts to parse the quantity string as an XML "boolean".
	 * @return Value as boolean.
	 * @throws IllegalArgumentException Thrown if parsing fails.
	 */
	public boolean tryParseValueAsXmlBoolean() throws IllegalArgumentException
	{
		return XmlHelper.parseXmlBoolean(m_valueAsString); // throws IllegalArgumentException
	}
	
	/**
	 * Attempts to parse the quantity string as an XML "int".
	 * @return Value as int.
	 * @throws NumberFormatException Thrown if parsing fails.
	 */
	public int tryParseValueAsXmlInt() throws NumberFormatException
	{
		return XmlHelper.parseXmlInt(m_valueAsString); // throws NumberFormatException
	}
	
	/**
	 * Attempts to parse the quantity string as an XML "long".
	 * @return Value as long.
	 * @throws NumberFormatException Thrown if parsing fails.
	 */
	public long tryParseValueAsXmlLong() throws NumberFormatException
	{
		return XmlHelper.parseXmlLong(m_valueAsString); // throws NumberFormatException
	}
	
	/**
	 * Creates an XML proxy from the object.
	 * @return Proxy.
	 */
	QuantityValueType toXmlProxy()
	{
		QuantityValueType retval = new QuantityValueType();
		
		// Set quantity string
		QuantityStringType quantityString = new QuantityStringType();
		quantityString.setValue(m_valueAsString);
		retval.setQuantityString(quantityString);
		
		ObjectFactory objFac = new ObjectFactory();
		
		if (m_dataType != null)
		{
			// Set datatype
			DataTypeType dataType = m_dataType.toXmlProxy();
			retval.setDataType(objFac.createQuantityValueTypeDataType(dataType));
		}
		
		// Set unit of measure if defined
		if (m_unitOfMeasure != null && !m_unitOfMeasure.isEmpty())
		{
			UnitOfMeasureType uomProxy = new UnitOfMeasureType();
			uomProxy.setValue(m_unitOfMeasure);
			retval.setUnitOfMeasure(objFac.createQuantityValueTypeUnitOfMeasure(uomProxy));
		}
		
		// Set key if defined
		if (m_key != null)
		{
			org.mesa.xml.b2mml_v0600.IdentifierType idProxy = new org.mesa.xml.b2mml_v0600.IdentifierType();
			m_key.populateXmlProxy(idProxy);
			retval.setKey(idProxy);
		}
		
		return retval;
	}
}
