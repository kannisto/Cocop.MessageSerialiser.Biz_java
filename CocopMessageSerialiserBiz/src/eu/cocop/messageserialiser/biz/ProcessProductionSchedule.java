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
import java.util.TreeMap;

import javax.xml.bind.JAXBElement;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.mesa.xml.b2mml_v0600.DateTimeType;
import org.mesa.xml.b2mml_v0600.ObjectFactory;
import org.mesa.xml.b2mml_v0600.ProcessProductionScheduleType;
import org.mesa.xml.b2mml_v0600.ProcessProductionScheduleType.DataArea;

import eu.cocop.messageserialiser.biz.ProductionSchedule;
import eu.cocop.messageserialiser.biz.InvalidMessageException;
import eu.cocop.messageserialiser.biz.XmlHelper;

import org.mesa.xml.b2mml_v0600.ProductionScheduleType;
import org.mesa.xml.b2mml_v0600.TransApplicationAreaType;
import org.mesa.xml.b2mml_v0600.TransProcessType;

/**
 * Represents an instruction to apply a production schedule.
 * @author Petri Kannisto
 */
public final class ProcessProductionSchedule
{
	private final ArrayList<ProductionSchedule> m_productionSchedules;
	
	private TimeInstant m_creationDateTime = new TimeInstant(DateTime.now().withZone(DateTimeZone.UTC));
	
	
	/**
	 * Constructor.
	 */
	public ProcessProductionSchedule()
	{
		m_productionSchedules = new ArrayList<>();
	}
	
	/**
	 * Constructor. Use this to deserialise from XML.
	 * @param xmlBytes XML data.
	 * @throws InvalidMessageException Thrown if an error is encountered.
	 */
	public ProcessProductionSchedule(byte[] xmlBytes) throws InvalidMessageException
	{
		m_productionSchedules = new ArrayList<>();
		
		try
		{
			@SuppressWarnings("unchecked")
			JAXBElement<ProcessProductionScheduleType> jaxbProxy = (JAXBElement<ProcessProductionScheduleType>)XmlHelper.deserialiseFromXml(xmlBytes);
			
			// Reading other values from XML
			readFieldValuesFromXmlProxy(jaxbProxy.getValue());
		}
		catch (ClassCastException e)
		{
			throw new InvalidMessageException("Failed to parse XML", e);
		}
	}
	
	private void readFieldValuesFromXmlProxy(ProcessProductionScheduleType proxy) throws InvalidMessageException
	{
		try
		{
			// Read creation time
			DateTimeType creationTimeRaw = proxy.getApplicationArea().getCreationDateTime();
			
			try
			{
				m_creationDateTime = new TimeInstant(creationTimeRaw.getValue()); // throws IllegalArgumentException
			}
			catch (IllegalArgumentException e)
			{
				throw new InvalidMessageException("Invalid creation time", e);
			}
			
			// Read schedules
			for (ProductionScheduleType scheduleRaw : proxy.getDataArea().getProductionSchedule())
			{
				ProductionSchedule schedule = new ProductionSchedule(scheduleRaw); // throws InvalidMessageException
				m_productionSchedules.add(schedule);
			}
		}
		catch (NullPointerException e)
		{
			throw new InvalidMessageException("Failed to read ProcessProductionSchedule - something expected is missing", e);
		}
	}
	
	/**
	 * Enclosed schedules.
	 * @return Enclosed schedules.
	 */
	public ArrayList<ProductionSchedule> getProductionSchedules()
	{
		return m_productionSchedules;
	}
	
	/**
	 * Creation time.
	 * @return Creation time.
	 */
	public TimeInstant getCreationDateTime()
	{
		return m_creationDateTime;
	}
	
	/**
	 * Creation time.
	 * @param dt Creation time.
	 */
	public void setCreationDateTime(TimeInstant dt)
	{
		m_creationDateTime = dt;
	}
	
	/**
	 * Serialises the object to XML.
	 * @return XML data.
	 */
	public byte[] toXmlBytes()
	{
		// Create proxy
		ProcessProductionScheduleType proxy = new ProcessProductionScheduleType();
		
		// Set release ID (obligatory)
		proxy.setReleaseID("1");
		
		// Create application area
		TransApplicationAreaType applicationArea = new TransApplicationAreaType();
		proxy.setApplicationArea(applicationArea);
		
		// Set creation datetime
		DateTimeType creationTimeProxy = new DateTimeType();
		creationTimeProxy.setValue(m_creationDateTime.toXsdDateTime());
		applicationArea.setCreationDateTime(creationTimeProxy);
		
		// Creating data area
		DataArea dataArea = new DataArea();
		dataArea.setProcess(new TransProcessType());
		proxy.setDataArea(dataArea);
		
		// This enables checking if any "extra types" are necessary in serialisation
		TreeMap<String, Class<?>> extraTypesInSer = new TreeMap<>();
		
		// Adding schedules
		for (int a = 0; a < m_productionSchedules.size(); ++a)
		{
			ProductionSchedule sched = m_productionSchedules.get(a);
			String idPrefix = "B2ProcProdSched_i" + (a+1) + "-";
			ProductionScheduleType schedProxy = sched.toXmlProxy(idPrefix);
			dataArea.getProductionSchedule().add(schedProxy);
			
			extraTypesInSer.putAll(sched.getExtraTypes());
		}
		
		// Serialising
		ObjectFactory objectFactory = new ObjectFactory();
		Object actualProxy = objectFactory.createProcessProductionSchedule(proxy);
		
		// Extra types needed in serialisation?
		if (extraTypesInSer.size() == 0)
		{
			return XmlHelper.toXmlBytes(actualProxy);
		}
		else if (extraTypesInSer.size() == 1)
		{
			return XmlHelper.toXmlBytes(actualProxy, extraTypesInSer.firstEntry().getValue());	
		}
		else
		{
			throw new RuntimeException("Only one extra type is currently supported in serialisation");
		}
	}
}
