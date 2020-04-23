//
// Please make sure to read and understand the files README.md and LICENSE.txt.
// 
// This file was prepared in the research project COCOP (Coordinating
// Optimisation of Complex Industrial Processes).
// https://cocop-spire.eu/
//
// Author: Petri Kannisto, Tampere University, Finland
// File created: 7/2019
// Last modified: 4/2020

package eu.cocop.messageserialiser.biz;

import static org.junit.Assert.*;

import org.junit.Test;

public class TEST_XmlHelper
{
	// Not testing all methods. Only testing serialisation/parsing-related methods
	// that are considered most likely to have errors.
	
	
	@Test
	public void parseXmlBoolean()
	{
		assertTrue(XmlHelper.parseXmlBoolean("  1 "));
		assertTrue(XmlHelper.parseXmlBoolean("true  "));
		assertFalse(XmlHelper.parseXmlBoolean("  0"));
		assertFalse(XmlHelper.parseXmlBoolean(" false"));
		
		assertIllegalArgumentException(() ->
		{
			XmlHelper.parseXmlBoolean("fafse");
		},
		"Failed to parse");
	}
	
	@Test
	public void serialiseXmlBoolean()
	{
		assertEquals("true", XmlHelper.serialiseXmlBoolean(true));
		assertEquals("false", XmlHelper.serialiseXmlBoolean(false));
	}
	
	@Test
	public void parseXmlDouble()
	{
		String errMsgStart = "Failed to parse double from";
		
		assertEquals(0, XmlHelper.parseXmlDouble("0"), 0.001);
		assertEquals(10.1, XmlHelper.parseXmlDouble("10.1"), 0.001);
		assertEquals(-3.8, XmlHelper.parseXmlDouble("-3.8"), 0.001);
		assertEquals(9e15, XmlHelper.parseXmlDouble("9e15"), 0.001);
		assertTrue(Double.isNaN(XmlHelper.parseXmlDouble("NaN")));
		
		// Expecting errors
		assertIllegalArgumentException(() ->
		{
			XmlHelper.parseXmlDouble("0,4");
		}
		, errMsgStart);
		assertIllegalArgumentException(() ->
		{
			XmlHelper.parseXmlDouble("");
		}
		, errMsgStart);
		assertIllegalArgumentException(() ->
		{
			XmlHelper.parseXmlDouble("  ");
		}
		, errMsgStart);
		assertIllegalArgumentException(() ->
		{
			XmlHelper.parseXmlDouble("a");
		}
		, errMsgStart);
	}
	
	@Test
	public void serialiseXmlDouble()
	{
		assertEquals("0.0", XmlHelper.serialiseXmlDouble(0));
		assertEquals("10.1", XmlHelper.serialiseXmlDouble(10.1));
		assertEquals("-3.8", XmlHelper.serialiseXmlDouble(-3.8));
		assertEquals("2.0E10", XmlHelper.serialiseXmlDouble(2e10));
		assertEquals("NaN", XmlHelper.serialiseXmlDouble(Double.NaN));
	}
	
	interface ITestInterface
	{
		void invoke() throws Exception;
	}
	
	private void assertIllegalArgumentException(ITestInterface testFcn, String expectedErrorStart)
	{
		try
		{
			testFcn.invoke();
			fail("Expected exception");
		}
		catch (IllegalArgumentException e)
		{
			String msg = e.getMessage();
			assertTrue("Unexpected message '" + msg + "'", msg.startsWith(expectedErrorStart));
		}
		catch (Exception e)
		{
			fail("Unexpected exception " + e.getClass().getName());
		}
	}
}
