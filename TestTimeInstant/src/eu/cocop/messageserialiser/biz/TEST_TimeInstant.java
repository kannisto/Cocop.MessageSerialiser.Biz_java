//
// Please make sure to read and understand the files README.md and LICENSE.txt.
// 
// This file was prepared in the research project COCOP (Coordinating
// Optimisation of Complex Industrial Processes).
// https://cocop-spire.eu/
//
// Author: Petri Kannisto, Tampere University, Finland
// File created: 4/2020
// Last modified: 4/2020

package eu.cocop.messageserialiser.biz;

import static org.junit.Assert.*;

import java.time.ZonedDateTime;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;


public class TEST_TimeInstant
{
	@Test
	public void timeInstant_createNotUtc() throws Exception
	{
		// Expecting an exception if the DateTime object is not in UTC
		
		assertIllegalDateTimeException(() ->
		{
			DateTime dt = DateTime.now(); // This has a local time zone
			new TimeInstant(dt);
		},
		"DateTime must have UTC as time zone");
	}
	
	@Test
	public void timeInstant_parseXsdDateTime() throws Exception
	{
		// Testing the parsing of a string rather than processing XML nodes
		
		DateTime expectedUtc = getUtcTime("2019-07-05T08:30:00Z");
		
		// Expecting that when a timestamp has no zone specified, Joda time assumes the local time zone
		Integer currentOffset  = ZonedDateTime.now().getOffset().getTotalSeconds();
		DateTime expectedNoZone = expectedUtc.minusSeconds(currentOffset);
		
		// UTC
		TimeInstant parsedExplicitUtc = new TimeInstant("2019-07-05T08:30:00Z");
		assertTimeInstantExplUtc(expectedUtc, parsedExplicitUtc);
		
		// Same as above but originally not UTC. Expecting a conversion.
		TimeInstant parsedConvertedToUtc = new TimeInstant("2019-07-05T11:30:00+03:00");
		assertTimeInstantExplUtc(expectedUtc, parsedConvertedToUtc);
		
		// No time zone specified
		TimeInstant dtNoZoneInfo = new TimeInstant("2019-07-05T08:30:00"); // No time zone specified
		assertTimeInstant(expectedNoZone, false, false, dtNoZoneInfo);
		
		// Expecting errors
		assertIllegalArgumentException(() ->
		{
			new TimeInstant("2019-07-05S11:30:00+03:00");
		}
		, "Failed to parse DateTime");
		assertIllegalArgumentException(() ->
		{
			new TimeInstant("");
		}
		, "Failed to parse DateTime");
		assertIllegalArgumentException(() ->
		{
			new TimeInstant("  ");
		}
		, "Failed to parse DateTime");
		assertIllegalArgumentException(() ->
		{
			new TimeInstant(" T ");
		}
		, "Failed to parse DateTime");
	}
	
	@Test
	public void timeInstant_toXsdDateTime() throws Exception
	{
		// Testing serialisation to XML Schema DateTime.
		
		DateTime dt = getUtcTime("2019-07-05T11:39:02+03:00");
		TimeInstant instant = new TimeInstant(dt);
		
		assertEquals("2019-07-05T08:39:02.000Z", instant.toXsdDateTime());
	}
	
	
	private void assertTimeInstantExplUtc(DateTime expDt, TimeInstant actual)
	{
		// Assert time instant. Expect explicit time zone UTC.
		assertTimeInstant(expDt, true, true, actual);
	}
	
	private void assertTimeInstant(DateTime expDt, boolean explZone, boolean expectUtc, TimeInstant actual)
	{
		DateTime actDt = actual.getValue();
		
		// Expecting UTC as the zone?
		assertEquals(expectUtc, actDt.getZone().equals(DateTimeZone.UTC));
		
		// Assert offset, difference and whether an explicit timezone was defined
		assertEquals(DateTimeZone.UTC.getOffset(expDt), DateTimeZone.UTC.getOffset(actDt));
		assertEquals("Expected " + expDt.toString() + ", got " + actDt.toString(), 0, expDt.compareTo(actDt));
		assertEquals(explZone, actual.getHasExplicitUtcOffset());
	}
	
	// Interface to allow function parameters
	interface ITestInterface
	{
		void invoke() throws Exception;
	}
	
	// This method asserts an exception
	private void assertIllegalDateTimeException(ITestInterface testFcn, String expectedErrorStart)
	{
		try
		{
			testFcn.invoke();
			fail("Expected exception");
		}
		catch (IllegalDateTimeException e)
		{
			String msg = e.getMessage();
			assertTrue("Unexpected message '" + msg + "'", msg.startsWith(expectedErrorStart));
		}
		catch (Exception e)
		{
			fail("Unexpected exception " + e.getClass().getName());
		}
	}
	
	// This method asserts an exception
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
	
	private DateTime getUtcTime(String xsdDateTime)
	{
		return DateTime.parse(xsdDateTime).withZone(DateTimeZone.UTC);
	}
}
