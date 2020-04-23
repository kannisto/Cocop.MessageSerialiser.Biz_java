//
// Please make sure to read and understand the files README.md and LICENSE.txt.
// 
// This file was prepared in the research project COCOP (Coordinating
// Optimisation of Complex Industrial Processes).
// https://cocop-spire.eu/
//
// Author: Petri Kannisto, Tampere University, Finland
// File created: 2/2018
// Last modified: 4/2020

package eu.cocop.messageserialiser.biz;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.TreeMap;

import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.mesa.xml.b2mml_v0600.ProcessProductionScheduleType;

import eu.cocop.messageserialiser.biz.InvalidMessageException;
import eu.cocop.messageserialiser.biz.XmlHelper;

/**
 * Contains methods to help processing XML documents.
 * @author Petri Kannisto
 */
final class XmlHelper
{
	private static Object m_jaxbContextLock = new Object();
	
	// This caches JAXB context objects
	private static TreeMap<String, JAXBContext> m_jaxbContextCache = new TreeMap<>();
	
	
	private XmlHelper()
	{
		// Private ctor -> "static" class
	}
	
	/**
	 * Parses a boolean value from XML.
	 * @param v Value as string.
	 * @return Boolean.
	 * @throws IllegalArgumentException Thrown if parsing fails.
	 */
	static boolean parseXmlBoolean(String v)
	{
		// DatatypeConverter.parseBoolean works incorrectly!
		// Therefore, implemented a custom function.
		// Legal values: booleanRep ::= 'true' | 'false' | '1' | '0'
		// See https://www.w3.org/TR/xmlschema11-2/#boolean
		
		String str = v.trim();
		
		if (str.equals("0") || str.equals("false"))
		{
			return false;
		}
		else if (str.equals("1") || str.equals("true"))
		{
			return true;
		}
		else
		{
			throw new IllegalArgumentException("Failed to parse xsd:boolean from \"" + str + "\"");
		}
	}
	
	/**
	 * Serialises a boolean value to XML.
	 * @param v Value.
	 * @return Value as string.
	 */
	static String serialiseXmlBoolean(boolean v)
	{
		return DatatypeConverter.printBoolean(v);
	}
	
	/**
	 * Parses an int value from XML.
	 * @param v Value as string.
	 * @return Int.
	 * @throws NumberFormatException Thrown if parsing fails.
	 */
	static int parseXmlInt(String v)
	{
		return DatatypeConverter.parseInt(v);
	}
	
	/**
	 * Serialises an int value to XML.
	 * @param v Value.
	 * @return Value as string.
	 */
	static String serialiseXmlInt(int v)
	{
		return DatatypeConverter.printInt(v);
	}
	
	/**
	 * Parses a long value from XML.
	 * @param v Value as string.
	 * @return Long.
	 * @throws NumberFormatException Thrown if parsing fails.
	 */
	static long parseXmlLong(String v)
	{
		return DatatypeConverter.parseLong(v);
	}
	
	/**
	 * Serialises a long value to XML.
	 * @param v Value.
	 * @return Value as string.
	 */
	static String serialiseXmlLong(long v)
	{
		return DatatypeConverter.printLong(v);
	}
	
	/**
	 * Parses a double value from XML.
	 * @param v Value as string.
	 * @return Double.
	 * @throws IllegalArgumentException Thrown if parsing fails.
	 */
	static double parseXmlDouble(String v)
	{
		try
		{
			return DatatypeConverter.parseDouble(v);
		}
		catch (Exception e)
		{
			throw new IllegalArgumentException("Failed to parse double from \"" + v + "\"", e);
		}
	}
	
	/**
	 * Serialises a double value to XML.
	 * @param v Value.
	 * @return Value as string.
	 */
	static String serialiseXmlDouble(double v)
	{
		return DatatypeConverter.printDouble(v);
	}
	
	/**
	 * Serialises an object to XML.
	 * @param proxy Proxy to be serialised.
	 * @return Serialised presentation.
	 */
	static byte[] toXmlBytes(Object proxy)
	{
		return toXmlBytes(proxy, null);
	}
	
	/**
	 * Serialises an object to XML.
	 * @param proxy Proxy to be serialised.
	 * @param extraType Extra type to be applied in marshalling.
	 * @return Serialised presentation.
	 */
	static byte[] toXmlBytes(Object proxy, Class<?> extraType)
	{
		ByteArrayOutputStream stream = null;
		OutputStreamWriter writer = null;
		
		try
		{
			stream = new ByteArrayOutputStream();
			writer = new OutputStreamWriter(stream);
			
			// Do marshalling
			Marshaller marshaller = getJaxbContext(extraType).createMarshaller();
			marshaller.marshal(proxy, writer);
			
			writer.flush();
			return stream.toByteArray();
		}
		catch (IOException e)
		{
			throw new RuntimeException(e.getMessage(), e);
		}
		catch (JAXBException e)
		{
			throw new RuntimeException(e.getMessage(), e);
		}
		finally
		{
			if (writer != null) {
				try {
					writer.close();
				} catch (Exception ignore) {}
			}
			if (stream != null) {
				try {
					stream.close();
				} catch (Exception ignore) {}
			}
		}
	}
	
	/**
	 * Deserialises an object from XML.
	 * @param xmlBytes XML data.
	 * @return Proxy object.
	 * @throws InvalidMessageException Thrown if a message-related error is found.
	 */
	static Object deserialiseFromXml(byte[] xmlBytes) throws InvalidMessageException
	{
		try
		{
			// Get JAXB context
			JAXBContext jaxbContext = getJaxbContext(null);
			
			// Do JAXB unmarshalling
			ByteArrayInputStream reader = null;
			Object proxy = null;
			
			try
			{
				reader = new ByteArrayInputStream(xmlBytes);
				Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
				proxy = unmarshaller.unmarshal(reader);
			}
			finally
			{
				if (reader != null) reader.close();
			}
			
			return proxy;
		}
		catch (IOException e)
		{
			// This exception is not expected
			throw new RuntimeException("Failed to parse XML", e);
		}
		catch (JAXBException e)
		{
			throw new InvalidMessageException("Failed to deserialise from XML", e);
		}
	}
	
	/**
	 * Gets a JAXB context object.
	 * @param Extra type to be applied if any.
	 * @return JAXB context.
	 * @throws JAXBException (Not expected in normal conditions.)
	 */
	private static JAXBContext getJaxbContext(Class<?> extraType) throws JAXBException
	{
		Class<?>[] classes;
		
		// Extra type specified?
		if (extraType != null)
		{
			classes = new Class[2];
			classes[0] = ProcessProductionScheduleType.class;
			classes[1] = extraType;
		}
		else
		{
			classes = new Class[1];
			classes[0] = ProcessProductionScheduleType.class;
		}
		
		String classKey = buildKeyFromTypes(classes);
		
		// JAXBContext is thread-safe, but protecting the static cache object
		synchronized (m_jaxbContextLock)
		{
			// Create context if it does not yet exist
			if (!m_jaxbContextCache.containsKey(classKey))
			{
				m_jaxbContextCache.put(classKey, JAXBContext.newInstance(classes));
			}
			
			return m_jaxbContextCache.get(classKey);
		}
	}
	
	private static String buildKeyFromTypes(Class<?>[] classes)
	{
		// Builds a string from types. This can be used as a key in dictionaries.
		
		StringBuilder sb = new StringBuilder();
		
		for (Class<?> c : classes)
		{
			sb.append(c.getCanonicalName());
			sb.append(";");
		}
		
		return sb.toString();
	}
}
