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

import eu.cocop.messageserialiser.biz.InvalidMessageException;

/**
 * Represents a data type.
 * @author Petri Kannisto
 */
public final class DataType
{
	private final String SuffixUnCefact = "_UN_CEFACT";
	private final String SuffixXml = "Xml";
	
	/**
	 * Represents a data type. Some types come from UN/CEFACT, whereas others come
	 * from the XML standard.
	 * @author Petri Kannisto
	 */
	public enum TypeType
	{
		Other,
		Amount_UN_CEFACT,
		BinaryObject_UN_CEFACT,
		Code_UN_CEFACT,
		DateTime_UN_CEFACT,
		Identifier_UN_CEFACT,
		Indicator_UN_CEFACT,
		Measure_UN_CEFACT,
		Numeric_UN_CEFACT,
		Quantity_UN_CEFACT,
		Text_UN_CEFACT,
		stringXml,
		byteXml,
		unsignedByteXml,
		binaryXml,
		integerXml,
		positiveIntegerXml,
		negativeIntegerXml,
		nonNegativeIntegerXml,
		nonPositiveIntegerXml,
		intXml,
		unsignedIntXml,
		longXml,
		unsignedLongXml,
		shortXml,
		unsignedShortXml,
		decimalXml,
		floatXml,
		doubleXml,
		booleanXml,
		timeXml,
		timeInstantXml,
		timePeriodXml,
		durationXml,
		dateXml,
		dateTimeXml,
		monthXml,
		yearXml,
		centuryXml,
		recurringDayXml,
		recurringDateXml,
		recurringDurationXml,
		NameXml,
		QNameXml,
		NCNameXml,
		uriReferenceXml,
		languageXml,
		IDXml,
		IDREFXml,
		IDREFSXml,
		ENTITYXml,
		ENTITIESXml,
		NOTATIONXml,
		NMTOKENXml,
		NMTOKENSXml,
		EnumerationXml,
		SVGXml
	}
	
	private final TypeType m_type;
	
	
	/**
	 * Constructor.
	 * @param t Data type.
	 */
	public DataType(TypeType t)
	{
		m_type = t;
	}
	
	/**
	 * Constructor.
	 * @param proxy XML proxy.
	 * @throws InvalidMessageException Thrown if an error is encountered.
	 */
	DataType(DataTypeType proxy) throws InvalidMessageException
	{
		if (proxy.getValue() == null)
		{
			throw new InvalidMessageException("If datatype element is present, it must have a value");
		}
		
		m_type = parseType(proxy.getValue());
	}
	
	/**
	 * Type.
	 * @return Type.
	 */
	public TypeType getType()
	{
		return m_type;
	}
	
	/**
	 * Generates an XML proxy from the object.
	 * @return XML proxy.
	 */
	DataTypeType toXmlProxy()
	{
		DataTypeType proxy = new DataTypeType();
		proxy.setValue(typeToString(m_type));
		
		return proxy;
	}
	
	private TypeType parseType(String raw) throws InvalidMessageException
	{
		// A) other
		if (raw.equals(TypeType.Other.toString()))
		{
			return TypeType.Other;
		}
		
		// B) Try to parse as an XML type
		try
		{
			return TypeType.valueOf(raw + SuffixXml);
		}
		catch (IllegalArgumentException e)
		{} // Do nothing, try with another
		
		// C) Try to parse as a UN/CEFACT type
		try
		{
			return TypeType.valueOf(raw + SuffixUnCefact);
		}
		catch (IllegalArgumentException e)
		{
			throw new InvalidMessageException("Failed to parse datatype", e);
		}
	}
	
	private String typeToString(TypeType t)
	{
		if (t == TypeType.Other)
		{
			// This value requires no additional processing
			return t.toString();
		}
		
		// 1) Just convert to string
		String raw = t.toString();
		
		// 2 A) Remove suffix of XML types
		if (raw.endsWith(SuffixXml))
		{
			return removeSuffix(raw, SuffixXml);
		}
		// 2 B) Remove suffix of UN/CEFACT types
		else if (raw.endsWith(SuffixUnCefact))
		{
			return removeSuffix(raw, SuffixUnCefact);
		}
		else
		{
			// If this happens, there is a bug somewhere
			throw new RuntimeException("Unexpected datatype value \"" + t.toString() + "\"");
		}
	}
	
	private String removeSuffix(String str, String suffix)
	{
		int endIndexMinus1 = str.length() - suffix.length();
		return str.substring(0, endIndexMinus1);
	}
}
