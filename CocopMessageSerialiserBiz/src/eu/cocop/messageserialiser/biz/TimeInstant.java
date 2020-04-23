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

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * Date/time class. This exists to provide information if the original
 * DateTime had an explicit offset relative to UTC. 
 * @author Petri Kannisto
 */
public class TimeInstant
{
	private final DateTime m_dateTime;
	private final boolean m_hasExplicitOffset;
	
	/**
	 * Constructor.
	 * @param dt Timestamp value.
	 * @exception IllegalDateTimeException Thrown if the time zone of dt is not UTC.
	 */
	public TimeInstant(DateTime dt) throws IllegalDateTimeException
	{
		if (!dt.getZone().equals(DateTimeZone.UTC))
		{
			throw new IllegalDateTimeException("DateTime must have UTC as time zone");
		}
		
		m_hasExplicitOffset = true;
		m_dateTime = dt;
	}
	
	/**
	 * Constructor.
	 * @param xsdDateTime DateTime in XML schema format.
	 * @throws IllegalArgumentException Thrown if parsing fails.
	 */
	TimeInstant(String xsdDateTime) throws IllegalArgumentException
	{
		try
		{
			XsdDateTimeParser parser = new XsdDateTimeParser(xsdDateTime); // throws IllegalArgumentException
			m_hasExplicitOffset = parser.explicitOffset;
			m_dateTime = parser.parsed;
		}
		catch (IllegalArgumentException e)
		{
			throw new IllegalArgumentException("Failed to parse DateTime string", e);
		}
	}
	
	/**
	 * The value of the timestamp.
	 * @return The value of the timestamp.
	 */
	public DateTime getValue()
	{
		return m_dateTime;
	}
	
	/**
	 * Whether the object has an explicitly defined UTC offset. The offset is not
	 * always explicit in XML. In such a case, the DateTime type assumes an
	 * offset (presumably local time). However, such implicit actions can lead to
	 * errors. Check this flag to make sure you can rely on the offset value.
	 * @return True if explicit, otherwise false.
	 */
	public boolean getHasExplicitUtcOffset()
	{
		return m_hasExplicitOffset;
	}
	
	/**
	 * Serialises the object using the XML Schema DateTime format.
	 * @return Value serialised as string.
	 */
	String toXsdDateTime()
	{
		return m_dateTime.toString();
	}
	
	
	
	// Parser class for XML schema datetimes
	private class XsdDateTimeParser
	{
		public final boolean explicitOffset;
		public final DateTime parsed;
		
		XsdDateTimeParser(String xsdDateTime)
		{
			explicitOffset = xsdDateTimeHasTimeZone(xsdDateTime);
			parsed = parseXsdDateTime(xsdDateTime, explicitOffset);
		}
		
		private boolean xsdDateTimeHasTimeZone(String input)
		{
			// Expecting <date> "T" <time>
			String[] parts = input.split("T");
			
			if (parts.length != 2) throw new IllegalArgumentException("Failed to parse date and time from string");
			
			String timepart = parts[1];
			
			// If the time zone is known, it is either "Z" for UTC or [+|-]hr:min offset
			return
					// UTC time?
					timepart.endsWith("Z") ||
					// Offset specified?
					timepart.contains("+") || timepart.contains("-");
		}
		
		private DateTime parseXsdDateTime(String xsdDateTime, boolean explicitZone)
		{
			try
			{
				DateTime dt = DateTime.parse(xsdDateTime); // throws IllegalArgumentException?
				
				if (explicitZone)
				{
					return dt.withZone(DateTimeZone.UTC); // Time zone is known -> convert to UTC
				}
				else
				{
					return dt; // Unknown time zone -> leave as such
				}
			}
			catch (Exception e)
			{
				throw new IllegalArgumentException("Failed to parse DateTime string", e);
			}
		}
	}
}
