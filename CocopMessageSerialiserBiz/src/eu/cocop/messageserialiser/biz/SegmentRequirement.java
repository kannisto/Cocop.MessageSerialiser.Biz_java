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

import org.mesa.xml.b2mml_v0600.DateTimeType;
import org.mesa.xml.b2mml_v0600.EarliestStartTimeType;
import org.mesa.xml.b2mml_v0600.EquipmentRequirementType;
import org.mesa.xml.b2mml_v0600.LatestEndTimeType;
import org.mesa.xml.b2mml_v0600.MaterialRequirementType;
import org.mesa.xml.b2mml_v0600.ProcessSegmentIDType;
import org.mesa.xml.b2mml_v0600.SegmentRequirementType;

import eu.cocop.messageserialiser.biz.EquipmentRequirement;
import eu.cocop.messageserialiser.biz.IdentifierType;
import eu.cocop.messageserialiser.biz.MaterialRequirement;
import eu.cocop.messageserialiser.biz.SegmentRequirement;
import eu.cocop.messageserialiser.biz.InvalidMessageException;

/**
 * Represents a production segment.
 * @author Petri Kannisto
 */
public final class SegmentRequirement
{
	private final ArrayList<EquipmentRequirement> m_equipmentRequirements;
	private final ArrayList<MaterialRequirement> m_materialRequirements;
	private final ArrayList<SegmentRequirement> m_segmentRequirements;
	
	private IdentifierType m_processSegmentId = null;
	private TimeInstant m_earliestStartTime = null;
	private TimeInstant m_latestEndTime = null;
	
	
	/**
	 * Constructor.
	 */
	public SegmentRequirement()
	{
		m_equipmentRequirements = new ArrayList<>();
		m_materialRequirements = new ArrayList<>();
		m_segmentRequirements = new ArrayList<>();
	}
	
	/**
	 * Constructor.
	 * @param xmlBytes XML proxy.
	 * @throws InvalidMessageException Thrown if an error is encountered.
	 */
	SegmentRequirement(SegmentRequirementType proxy) throws InvalidMessageException
	{
		m_equipmentRequirements = new ArrayList<>();
		m_materialRequirements = new ArrayList<>();
		m_segmentRequirements = new ArrayList<>();
		
		if (proxy.getProcessSegmentID() != null)
		{
			m_processSegmentId = new IdentifierType(proxy.getProcessSegmentID());
		}
		
        m_earliestStartTime = tryGetTime(proxy.getEarliestStartTime());
        m_latestEndTime = tryGetTime(proxy.getLatestEndTime());

        if (m_earliestStartTime != null && m_latestEndTime != null &&
        		m_latestEndTime.getValue().isBefore(m_earliestStartTime.getValue()))
        {
        	throw new InvalidMessageException("Segment end must not be before start");
        }
        
        if (proxy.getEquipmentRequirement() != null)
        {
        	// Read equipment requirements
        	for (EquipmentRequirementType reqRaw : proxy.getEquipmentRequirement())
        	{
        		EquipmentRequirement req = new EquipmentRequirement(reqRaw); // throws InvalidMessageException
        		m_equipmentRequirements.add(req);
        	}
        }
        
        if (proxy.getMaterialRequirement() != null)
        {
            // Read material requirements
        	for (MaterialRequirementType reqRaw : proxy.getMaterialRequirement())
            {
                MaterialRequirement req = new MaterialRequirement(reqRaw); // throws InvalidMessageException
            	m_materialRequirements.add(req);
            }
        }
        
        if (proxy.getSegmentRequirement() != null)
        {
        	// Read nested segment requirements recursively
        	for (SegmentRequirementType segRaw : proxy.getSegmentRequirement())
        	{
        		m_segmentRequirements.add(new SegmentRequirement(segRaw));
        	}
        }
	}
	
	private TimeInstant tryGetTime(DateTimeType dtRaw) throws InvalidMessageException
	{
		if (dtRaw == null || dtRaw.getValue() == null)
		{
			return null;
		}
		else
		{
			try
			{
				return new TimeInstant(dtRaw.getValue()); // throws IllegalArgumentException
			}
			catch (IllegalArgumentException e)
			{
				throw new InvalidMessageException("Failed to parse datetime value", e);
			}
		}
	}
	
	/**
	 * Earliest start time.
	 * @return Earliest start time.
	 */
	public TimeInstant getEarliestStartTime()
	{
		return m_earliestStartTime;
	}
	
	/**
	 * Earliest start time.
	 * @param dt Earliest start time.
	 */
	public void setEarliestStartTime(TimeInstant dt)
	{
		m_earliestStartTime = dt;
	}

	/**
	 * Latest end time.
	 * @return Latest end time.
	 */
	public TimeInstant getLatestEndTime()
	{
		return m_latestEndTime;
	}
	
	/**
	 * Latest end time.
	 * @param dt Latest end time.
	 */
	public void setLatestEndTime(TimeInstant dt)
	{
		m_latestEndTime = dt;
	}

	/**
	 * Equipment requirements.
	 * @return Equipment requirements.
	 */
	public ArrayList<EquipmentRequirement> getEquipmentRequirements()
	{
		return m_equipmentRequirements;
	}
	
	/**
	 * Material requirements.
	 * @return Material requirements.
	 */
	public ArrayList<MaterialRequirement> getMaterialRequirements()
	{
		return m_materialRequirements;
	}
	
	/**
	 * Segment requirements.
	 * @return Segment requirements.
	 */
	public ArrayList<SegmentRequirement> getSegmentRequirements()
	{
		return m_segmentRequirements;
	}

	/**
	 * Process segment ID.
	 * @return Process segment ID.
	 */
	public IdentifierType getProcessSegmentIdentifier()
	{
		return m_processSegmentId;
	}
	
	/**
	 * Process segment ID.
	 * @param id Process segment ID.
	 */
	public void setProcessSegmentIdentifier(IdentifierType id)
	{
		m_processSegmentId = id;
	}
	
	/**
	 * Creates an XML proxy from the object.
	 * @return Proxy.
	 */
	SegmentRequirementType toXmlProxy()
	{
		SegmentRequirementType retval = new SegmentRequirementType();
		
		// Adding equipment requirements
		for (EquipmentRequirement eqReq : m_equipmentRequirements)
		{
			retval.getEquipmentRequirement().add(eqReq.toXmlProxy());
		}
		
		// Adding material requirements
		for (MaterialRequirement matReq : m_materialRequirements)
		{
			retval.getMaterialRequirement().add(matReq.toXmlProxy());
		}
		
		// Add segment requirements
		for (SegmentRequirement segReq : m_segmentRequirements)
		{
			retval.getSegmentRequirement().add(segReq.toXmlProxy());
		}
		
		// Set earliest start
		if (m_earliestStartTime != null)
		{
			EarliestStartTimeType earlProxy = new EarliestStartTimeType();
			earlProxy.setValue(m_earliestStartTime.toXsdDateTime());
			retval.setEarliestStartTime(earlProxy);
		}
		
		// Set latest end
		if (m_latestEndTime != null)
		{
			LatestEndTimeType lateProxy = new LatestEndTimeType();
			lateProxy.setValue(m_latestEndTime.toXsdDateTime());
			retval.setLatestEndTime(lateProxy);
		}
		
		// Set process segment ID
		if (m_processSegmentId != null)
		{
			ProcessSegmentIDType segmProxy = new ProcessSegmentIDType();
			m_processSegmentId.populateXmlProxy(segmProxy);
			retval.setProcessSegmentID(segmProxy);
		}
		
		return retval;
	}
}
