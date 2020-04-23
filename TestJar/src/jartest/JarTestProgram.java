//
// Please make sure to read and understand the files README.md and LICENSE.txt.
// 
// This file was prepared in the research project COCOP (Coordinating
// Optimisation of Complex Industrial Processes).
// https://cocop-spire.eu/
//
// Author: Petri Kannisto, Tampere University, Finland
// File created: 10/2018
// Last modified: 4/2020

package jartest;

import eu.cocop.messageserialiser.biz.IdentifierType;
import eu.cocop.messageserialiser.biz.InvalidMessageException;
import eu.cocop.messageserialiser.biz.ProcessProductionSchedule;
import eu.cocop.messageserialiser.biz.ProductionRequest;
import eu.cocop.messageserialiser.biz.ProductionSchedule;

/**
 * This application was created to test if the JAR export of CocopMessageSerialiser has succeeded.
 * @author Petri Kannisto
 */
public class JarTestProgram
{
	public static void main(String[] args) throws InvalidMessageException
	{
		// The default transformer factory has caused issues in Matlab.
		// In Matlab, you may have to explicitly set the transformer.
		String propName = "javax.xml.transform.TransformerFactory";
		System.out.println(propName + " is:");
		// If this prints "null", there is no explicit setting
		System.out.println(System.getProperty(propName));
		
		// Create some objects to be serialised
		ProcessProductionSchedule processSchedOut = new ProcessProductionSchedule();
		ProductionRequest prodReqOut = new ProductionRequest();
		prodReqOut.setIdentifier(new IdentifierType("Hello-ID"));
		processSchedOut.getProductionSchedules().add(new ProductionSchedule());
		processSchedOut.getProductionSchedules().get(0).getProductionRequests().add(prodReqOut);
		
		// Serialising and deserialising
		ProcessProductionSchedule processSchedIn = new ProcessProductionSchedule(processSchedOut.toXmlBytes());
		String id1In = processSchedIn.getProductionSchedules().get(0).getProductionRequests().get(0).getIdentifier().getValue();
		
		System.out.println("ID of the first production request: " + id1In);
		System.out.println("Serialisation with the JAR seems to have succeeded as expected. Great!");
	}
}
