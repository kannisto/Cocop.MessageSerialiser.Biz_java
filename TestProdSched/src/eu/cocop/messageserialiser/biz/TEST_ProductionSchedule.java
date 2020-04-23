//
// Please make sure to read and understand the files README.md and LICENSE.txt.
// 
// This file was prepared in the research project COCOP (Coordinating
// Optimisation of Complex Industrial Processes).
// https://cocop-spire.eu/
//
// Author: Petri Kannisto, Tampere University, Finland
// File created: 5/2019
// Last modified: 4/2020

package eu.cocop.messageserialiser.biz;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import eu.cocop.messageserialiser.biz.DataType;
import eu.cocop.messageserialiser.biz.EquipmentElementLevelType;
import eu.cocop.messageserialiser.biz.EquipmentRequirement;
import eu.cocop.messageserialiser.biz.HierarchyScope;
import eu.cocop.messageserialiser.biz.IdentifierType;
import eu.cocop.messageserialiser.biz.MaterialRequirement;
import eu.cocop.messageserialiser.biz.MaterialUse;
import eu.cocop.messageserialiser.biz.MaterialUseType;
import eu.cocop.messageserialiser.biz.ProcessProductionSchedule;
import eu.cocop.messageserialiser.biz.ProductionRequest;
import eu.cocop.messageserialiser.biz.ProductionSchedule;
import eu.cocop.messageserialiser.biz.QuantityValue;
import eu.cocop.messageserialiser.biz.SegmentRequirement;
import eu.cocop.messageserialiser.meas.Item_Count;
import eu.cocop.messageserialiser.meas.Item_DataRecord;
import eu.cocop.messageserialiser.meas.Item_Measurement;
import eu.cocop.messageserialiser.biz.InvalidMessageException;

public class TEST_ProductionSchedule
{
	private static Validator m_validator = null;
	
	
	@Test
	public void testWrite() throws InvalidMessageException
	{
		// Serialising, validating and deserialising
		byte[] xmlData = createObjectForTestWrite().toXmlBytes();
		validateXmlDoc(xmlData);
		ProcessProductionSchedule testObject2 = new ProcessProductionSchedule(xmlData);
		
		// Assert creation time
		assertTimeInstantExplUtc(getUtcTime("2019-05-09T12:20:19Z"), testObject2.getCreationDateTime());
		
		ProductionSchedule schedule = testObject2.getProductionSchedules().get(0);
		
		// Assert request count
		assertEquals(2, schedule.getProductionRequests().size());
		
		// Asserting a production request
		ProductionRequest request1 = schedule.getProductionRequests().get(0);
		assertEquals(2, request1.getSegmentRequirements().size());
		
		// Asserting identifier
        assertEquals("some-id", request1.getIdentifier().getValue());
		
		// Asserting a hierarchy scope
		assertEquals("psc3", request1.getHierarchyScope().getEquipmentIdentifier().getValue());
		assertEquals(EquipmentElementLevelType.ProcessCell, request1.getHierarchyScope().getEquipmentElementLevel());
		
		// Asserting a segment requirement
		SegmentRequirement segReq = request1.getSegmentRequirements().get(0);
		assertEquals("1", segReq.getProcessSegmentIdentifier().getValue());
		assertTimeInstantExplUtc(getUtcTime("2019-05-09T13:36:02Z"), segReq.getEarliestStartTime());
		assertTimeInstantExplUtc(getUtcTime("2019-05-09T13:37:02Z"), segReq.getLatestEndTime());
		assertEquals(1, segReq.getMaterialRequirements().size());
		assertEquals(1, segReq.getEquipmentRequirements().size());
		
		// Asserting nested segment requirement
        SegmentRequirement segReqNested = segReq.getSegmentRequirements().get(0);
        assertTimeInstantExplUtc(getUtcTime("2019-08-29T15:31:38Z"), segReqNested.getEarliestStartTime());
		
		// Asserting equipment requirement
		EquipmentRequirement eqReq = segReq.getEquipmentRequirements().get(0);
		assertEquals(1, eqReq.getQuantities().size());
		assertTrue(eqReq.getQuantities().get(0).tryParseValueAsXmlBoolean());
		
		MaterialRequirement matReq = segReq.getMaterialRequirements().get(0);
		
		// Asserting material definition ID
        assertEquals(1, matReq.getMaterialDefinitionIdentifiers().size());
        assertEquals("slag", matReq.getMaterialDefinitionIdentifiers().get(0).getValue());
		
		// Asserting a material lot ID
		assertEquals(1, matReq.getMaterialLotIdentifiers().size());
		assertEquals("my-lot-1", matReq.getMaterialLotIdentifiers().get(0).getValue());
		
		// Asserting material use
		assertEquals(MaterialUseType.Produced, matReq.getMaterialUse().getValue());
		
		// Asserting a material quantity
		assertEquals(1, matReq.getQuantities().size());
		QuantityValue quantity = matReq.getQuantities().get(0);
		assertEquals("12.2", quantity.getRawQuantityString());
		assertEquals(12.2, quantity.tryParseValueAsXmlDouble(), 0.001);
		assertEquals("t", quantity.getUnitOfMeasure());
		assertEquals(DataType.TypeType.doubleXml, quantity.getDataType().getType());
		assertEquals("my-mat-key", quantity.getKey().getValue());
		
		// Asserting an assembly requirement
		assertEquals(1, matReq.getAssemblyRequirements().size());
		MaterialRequirement assemblyReq = matReq.getAssemblyRequirements().get(0);
		assertEquals("Ni", assemblyReq.getMaterialDefinitionIdentifiers().get(0).getValue());
	}
	
	private ProcessProductionSchedule createObjectForTestWrite()
	{
		// Applying identifiers (such as "SCH") to items to enable a
		// verification that this test implementation has same items
		// as those in other environments, particularly C#.
		
		// SCH Creating a schedule
		ProductionSchedule schedule = new ProductionSchedule();
		// PROPS Creating object to be serialised
		ProcessProductionSchedule testObject1 = new ProcessProductionSchedule();
		testObject1.getProductionSchedules().add(schedule);
		
		// PROPS-CR Set creation time
		testObject1.setCreationDateTime(new TimeInstant(getUtcTime("2019-05-09T12:20:19Z")));
		
		// PROD1 Adding one production request (psc3)
		ProductionRequest request1 = new ProductionRequest();
		schedule.getProductionRequests().add(request1);
		
		// PROD1-ID Set identifier
		request1.setIdentifier(new IdentifierType("some-id"));
		
		// PROD1-HS Set hierarchy scope
		HierarchyScope hScope = new HierarchyScope(
				new IdentifierType("psc3"),
				EquipmentElementLevelType.ProcessCell);
		request1.setHierarchyScope(hScope);
		
		// SEG1 Add segment requirement
		SegmentRequirement segReq1 = new SegmentRequirement();
		segReq1.setProcessSegmentIdentifier(new IdentifierType("1"));
		segReq1.setEarliestStartTime(new TimeInstant("2019-05-09T13:36:02Z"));
		segReq1.setLatestEndTime(new TimeInstant("2019-05-09T13:37:02Z"));
		request1.getSegmentRequirements().add(segReq1);
		
		// EQ1 Add equipment requirement
		EquipmentRequirement eqReq = new EquipmentRequirement();
		QuantityValue eqReqQuantity = new QuantityValue(true);
		eqReq.getQuantities().add(eqReqQuantity);
		segReq1.getEquipmentRequirements().add(eqReq);
		
		// MAT1 Add material requirement
		MaterialRequirement matReq = new MaterialRequirement();
		matReq.getMaterialDefinitionIdentifiers().add(new IdentifierType("slag"));
		matReq.getMaterialLotIdentifiers().add(new IdentifierType("my-lot-1"));
		matReq.setMaterialUse(new MaterialUse(MaterialUseType.Produced));
		QuantityValue quantityValue = new QuantityValue(12.2);
		quantityValue.setUnitOfMeasure("t");
		quantityValue.setKey(new IdentifierType("my-mat-key"));
		matReq.getQuantities().add(quantityValue);
		MaterialRequirement assemblyReq = new MaterialRequirement();
		assemblyReq.getMaterialDefinitionIdentifiers().add(new IdentifierType("Ni"));
		matReq.getAssemblyRequirements().add(assemblyReq);
		segReq1.getMaterialRequirements().add(matReq);
		
		// SEG2 Add another (empty) segment requirement
		SegmentRequirement segReq2 = new SegmentRequirement();
		request1.getSegmentRequirements().add(segReq2);
		
		// SEG1-1 Add nested segment requirement
		SegmentRequirement segReqNested = new SegmentRequirement();
		segReqNested.setEarliestStartTime(new TimeInstant("2019-08-29T15:31:38Z"));
		segReq1.getSegmentRequirements().add(segReqNested);
		
		// PROD2 Adding another production request
		ProductionRequest request2 = new ProductionRequest();
		schedule.getProductionRequests().add(request2);
		
		return testObject1;
	}
	
	@Test
	public void testWriteEmptySchedule() throws InvalidMessageException
	{
		// Testing if write works when the schedule is empty
		
		// Creating an object to be serialised
		ProductionSchedule schedule = new ProductionSchedule();
		ProcessProductionSchedule testObject1 = new ProcessProductionSchedule();
		DateTime approximateCreationTime = DateTime.now();
		testObject1.getProductionSchedules().add(schedule);
		
		// Serialising validating and deserialising. The test will likely fails here if it fails.
		byte[] xmlData = testObject1.toXmlBytes();
		validateXmlDoc(xmlData);
		ProcessProductionSchedule testObjectIn = new ProcessProductionSchedule(xmlData);
		
		// Asserting
 		assertEmptyProcessMsg(testObjectIn);
 		
 		// Asserting creation time. Expecting it to be the creation time of the object.
 		long difference_ms = approximateCreationTime.getMillis() - testObjectIn.getCreationDateTime().getValue().getMillis();
 		assertTrue(Math.abs(difference_ms) < 500);
	}
	
	@Test
	public void testWriteEmptyItems() throws InvalidMessageException
	{
		// Testing if write works when there are empty items in the schedule
		
		// Create production requests
		ProductionRequest prodReq1 = new ProductionRequest(); // 1) This will remain empty
		ProductionRequest prodReq2 = new ProductionRequest();
		ProductionRequest prodReq3 = new ProductionRequest();
		
		// Production request 2: This has hierarchy scope and segment requirement specified
		prodReq2.setHierarchyScope(new HierarchyScope(new IdentifierType("psc2"), EquipmentElementLevelType.ProcessCell));
		prodReq2.getSegmentRequirements().add(new SegmentRequirement()); // 2) Empty segment requirement
		
		
		// Production request 3: populating
		
		SegmentRequirement segReq_3_1 = new SegmentRequirement();
		prodReq3.getSegmentRequirements().add(segReq_3_1);
		
		// 3) Create equipment requirement (empty)
		segReq_3_1.getEquipmentRequirements().add(new EquipmentRequirement());
		
		// 4) Create a material requirement (empty)
		segReq_3_1.getMaterialRequirements().add(new MaterialRequirement());
		
		// 5) Create a material requirement (with data)
		QuantityValue quantity = new QuantityValue((String)null);
		MaterialRequirement matReq = new MaterialRequirement();
		matReq.getQuantities().add(quantity);
		segReq_3_1.getMaterialRequirements().add(matReq);
		
		
		// Create schedule
		ProductionSchedule schedule = new ProductionSchedule();
		schedule.getProductionRequests().add(prodReq1);
		schedule.getProductionRequests().add(prodReq2);
		schedule.getProductionRequests().add(prodReq3);
		
		ProcessProductionSchedule testObject1 = new ProcessProductionSchedule();
		testObject1.getProductionSchedules().add(schedule);

		// Serialising, validating and deserialising. The test will likely fails here if it fails.
		byte[] xmlData = testObject1.toXmlBytes();
		validateXmlDoc(xmlData);
		ProcessProductionSchedule testObjectIn = new ProcessProductionSchedule(xmlData);
		
		// Asserting
		assertEmptyItemsDoc(testObjectIn);
	}
	
	@Test
	public void testRead() throws InvalidMessageException
	{
		// Testing reading a regular XML file with all supported features included.
		
		// Getting test object
		ProcessProductionSchedule testObject = getTestObjectFromFile("ProcessProductionSchedule.xml");

		// Assert creation time
		assertTimeInstantExplUtc(getUtcTime("2019-04-24T14:10:25Z"), testObject.getCreationDateTime());
		
        // Assert schedule count
        assertEquals(1, testObject.getProductionSchedules().size());

        // Assert request count
        ProductionSchedule schedule1 = testObject.getProductionSchedules().get(0);
        assertEquals(2, schedule1.getProductionRequests().size());


        // Assert request 1

        ProductionRequest request1 = schedule1.getProductionRequests().get(0);
        assertEquals(2, request1.getSegmentRequirements().size());
        
        // Asserting identifier
        assertEquals("my-identifier-1", request1.getIdentifier().getValue());
        
        // Asserting hierarchy scope
        assertEquals("fsf", request1.getHierarchyScope().getEquipmentIdentifier().getValue());
        assertEquals(EquipmentElementLevelType.ProcessCell, request1.getHierarchyScope().getEquipmentElementLevel());
        
        
        // Asserting one segment requirement
        SegmentRequirement segReq1 = request1.getSegmentRequirements().get(0);
        
        // Assert process segment identifier
        assertEquals("1", segReq1.getProcessSegmentIdentifier().getValue());
        
        // Assert times
        assertTimeInstantExplUtc(getUtcTime("2019-04-24T15:00:00Z"), segReq1.getEarliestStartTime());
        assertTimeInstantExplUtc(getUtcTime("2019-04-24T15:30:00Z"), segReq1.getLatestEndTime());

        // Asserting equipment requirement
        assertEquals(1, segReq1.getEquipmentRequirements().size());
        EquipmentRequirement equipmentReq = segReq1.getEquipmentRequirements().get(0);
        QuantityValue quantityEquipmentAvailability1 = equipmentReq.getQuantities().get(0);
        QuantityValue quantityEquipmentAvailability2 = equipmentReq.getQuantities().get(1);
        assertEquals("false", quantityEquipmentAvailability1.getRawQuantityString());
        assertEquals("true", quantityEquipmentAvailability2.getRawQuantityString());
        assertFalse(quantityEquipmentAvailability1.tryParseValueAsXmlBoolean());
        assertTrue(quantityEquipmentAvailability2.tryParseValueAsXmlBoolean());
        assertEquals(DataType.TypeType.booleanXml, quantityEquipmentAvailability1.getDataType().getType());
        assertEquals(DataType.TypeType.booleanXml, quantityEquipmentAvailability2.getDataType().getType());
        
        // Asserting material requirement
        assertEquals(2, segReq1.getMaterialRequirements().size());
        MaterialRequirement matReq = segReq1.getMaterialRequirements().get(0);
        // Asserting material definition ID
        assertEquals("matte", matReq.getMaterialDefinitionIdentifiers().get(0).getValue());
        // Asserting material lot ID
        assertEquals(1, matReq.getMaterialLotIdentifiers().size());
        assertEquals("psc2-15", matReq.getMaterialLotIdentifiers().get(0).getValue());
        // Asserting material use
        assertEquals(MaterialUseType.Produced, matReq.getMaterialUse().getValue());
        // Assert quantity 1
        QuantityValue matProdQuantity1 = matReq.getQuantities().get(0);
        assertEquals("41.9", matProdQuantity1.getRawQuantityString());
        assertEquals(41.9, (double)matProdQuantity1.tryParseValueAsXmlDouble(), 0.001);
        assertEquals("t/h", matProdQuantity1.getUnitOfMeasure());
        assertEquals(DataType.TypeType.doubleXml, matProdQuantity1.getDataType().getType());
        assertEquals("ProdRate", matProdQuantity1.getKey().getValue());
        // Assert quantity 2
        QuantityValue matProdQuantity2 = matReq.getQuantities().get(1);
        assertEquals("11.9", matProdQuantity2.getRawQuantityString());
        // Not asserting other fields, because this would be redundant to the previous quantity value.
        // -- Asserting assembly requirements. Only one field is included in the test,
        // because the assembly requirements have a structure similar to the enclosing requirements.
        ArrayList<MaterialRequirement> assemblyReqs = matReq.getAssemblyRequirements();
        assertEquals(2, assemblyReqs.size());
        assertEquals("Cu", assemblyReqs.get(0).getMaterialDefinitionIdentifiers().get(0).getValue());
        assertEquals("S", assemblyReqs.get(1).getMaterialDefinitionIdentifiers().get(0).getValue());
        
        // Asserting another segment requirement (with nested segment requirement)
        
        SegmentRequirement segReq2_1 = request1.getSegmentRequirements().get(1).getSegmentRequirements().get(0);
        assertTimeInstantExplUtc(getUtcTime("2019-04-24T15:31:00Z"), segReq2_1.getEarliestStartTime()); // Just assert one field - a thorough assertion is done elsewhere
        
        
        // Assert request 2

        ProductionRequest request2 = schedule1.getProductionRequests().get(1);
        assertEquals("my-identifier-2", request2.getIdentifier().getValue());
	}
	
	@Test
	public void testReadEmptySched() throws InvalidMessageException
	{
		// The schedule is empty in this test

        // Getting test object
 		ProcessProductionSchedule testObject = getTestObjectFromFile("ProcessProductionSchedule_EmptySched.xml");
 		
 		// Asserting
 		assertEmptyProcessMsg(testObject);
	}
	
	@Test
	public void testReadEmptyItems() throws InvalidMessageException
	{
		// There are empty items in the XML document in this test

        // Getting test object
  		ProcessProductionSchedule testObject = getTestObjectFromFile("ProcessProductionSchedule_EmptyItems.xml");
        
  		// Asserting
  		assertEmptyItemsDoc(testObject);
	}
	
	@Test
	public void testReadInvalidDate()
	{
		// Testing reading a schedule with invalid values

        try
        {
        	// Getting test object
      		getTestObjectFromFile("Neg_ProcessProductionSchedule_InvalidDate.xml");
            fail("Expected exception");
        }
        catch (InvalidMessageException e)
        {
            assertEquals("Failed to parse datetime value", e.getMessage());
        }
	}
	
	@Test
	public void testReadInvalidQuantityValues() throws InvalidMessageException
	{
		// Testing reading an invalid quantity value

		// Getting test object
  		ProcessProductionSchedule testObject = getTestObjectFromFile("Neg_ProcessProductionSchedule_InvalidQuantityValue.xml");
		
  		MaterialRequirement materialReq = testObject.getProductionSchedules().get(0).
  	            getProductionRequests().get(0).
  	            getSegmentRequirements().get(0).
  	            getMaterialRequirements().get(0);
  		
  		QuantityValue quantityDouble = materialReq.getQuantities().get(0);
  		QuantityValue quantityBoolean = materialReq.getQuantities().get(1);
  		QuantityValue quantityInt = materialReq.getQuantities().get(2);
  		
  		// To make sure the raw values have been received as expected:
  		assertEquals("41fs.9", quantityDouble.getRawQuantityString());
  		assertEquals("faflse", quantityBoolean.getRawQuantityString());
  		assertEquals("0r3", quantityInt.getRawQuantityString());
  		
  		// Double
        try
        {
        	quantityDouble.tryParseValueAsXmlDouble();
        	fail("Expected exception");
        }
        catch (IllegalArgumentException e)
        {
            assertTrue(e.getMessage().startsWith("Failed to parse double from"));
        }
        
        // Boolean
        try
        {
            quantityBoolean.tryParseValueAsXmlBoolean();
        	fail("Expected exception");
        }
        catch (IllegalArgumentException e)
        {
            assertTrue(e.getMessage().startsWith("Failed to parse"));
        }
        
        // Int
        try
        {
            quantityInt.tryParseValueAsXmlInt();
        	fail("Expected exception");
        }
        catch (NumberFormatException e)
        {
            assertTrue(e.getMessage().startsWith("Not a number"));
        }
	}
	
	@Test
	public void testReadInvalidQuantityDataType()
	{
		// Testing reading an invalid quantity datatype
		
		try
        {
        	// Getting test object
      		getTestObjectFromFile("Neg_ProcessProductionSchedule_InvalidQuantityDataType.xml");
      		fail("Expected exception");
        }
        catch (InvalidMessageException e)
        {
            assertEquals("Failed to parse datatype", e.getMessage());
        }
	}
	
	@Test
	public void testReadInvalidEqElemLevel()
	{
		try
        {
        	// Getting test object
      		getTestObjectFromFile("Neg_ProcessProductionSchedule_InvalidEqElemLevel.xml");
      		fail("Expected exception");
        }
        catch (InvalidMessageException e)
        {
            assertEquals("Invalid equipment element level", e.getMessage());
        }
	}
	
	@Test
	public void testReadInvalidMatUse()
	{
		try
        {
        	// Getting test object
      		getTestObjectFromFile("Neg_ProcessProductionSchedule_InvalidMatUse.xml");
      		fail("Expected exception");
        }
        catch (InvalidMessageException e)
        {
            assertEquals("Invalid material use value", e.getMessage());
        }
	}
	
	@Test
	public void testReadInvalidCreationTime()
	{
		try
        {
        	// Getting test object
      		getTestObjectFromFile("Neg_ProcessProductionSchedule_InvalidCreationTime.xml");
      		fail("Expected exception");
        }
        catch (InvalidMessageException e)
        {
            assertEquals("Invalid creation time", e.getMessage());
        }
	}
	
	@Test
	public void testReadSegmentEndBeforeStart()
	{
		try
        {
        	// Getting test object
      		getTestObjectFromFile("Neg_ProcessProductionSchedule_EndBeforeStart.xml");
      		fail("Expected exception");
        }
        catch (InvalidMessageException e)
        {
            assertEquals("Segment end must not be before start", e.getMessage());
        }
	}
	
	@Test
	public void testReadSchedulingParameters() throws InvalidMessageException, eu.cocop.messageserialiser.meas.InvalidMessageException
	{
		// Testing the reading of scheduling parameters
		
		ProcessProductionSchedule testObject = getTestObjectFromFile("ProcessProductionSchedule_SchedulingParams.xml");
		
		// Asserting just one parameter, because the Item_DataRecord class is
		// not a part of this application.
		ProductionRequest productionReq = testObject.getProductionSchedules().get(0).getProductionRequests().get(0);
		
		Item_DataRecord parameters = new Item_DataRecord((Node)productionReq.getSchedulingParameters());
		Item_Measurement parameter = (Item_Measurement)parameters.getItem("SomeParam1");
		
		assertEquals(10.6, parameter.getValue(), 0.0001);
		assertEquals("t/h", parameter.getUnitOfMeasure());
	}
	
	@Test
	public void testWriteSchedulingParameters() throws Exception
	{
		// Testing the writing of scheduling parameters
		
		// Creating a schedule to be serialised
		ProductionSchedule schedule = new ProductionSchedule();
		ProcessProductionSchedule testObject1 = new ProcessProductionSchedule();
		testObject1.getProductionSchedules().add(schedule);
		
		// Creating a production request
		ProductionRequest productionRequest = new ProductionRequest();
		schedule.getProductionRequests().add(productionRequest);
		
		// Setting scheduling parameters. Not testing the actual parameters
		// because the data record is tested elsewhere.
		Item_DataRecord parameters = new Item_DataRecord();
		parameters.addItem("myparam", new Item_Count(3));
		productionRequest.setSchedulingParameters(parameters.toDataRecordPropertyProxy());
		
		// Serialising validating and deserialising. The test will likely fails here if it fails.
		byte[] xmlData = testObject1.toXmlBytes();
		validateXmlDoc(xmlData);
		ProcessProductionSchedule testObjectIn = new ProcessProductionSchedule(xmlData);
		
		// Asserting parameters
		Object parametersInRaw = testObjectIn.getProductionSchedules().get(0).getProductionRequests().get(0).getSchedulingParameters();
		Item_DataRecord parametersIn = new Item_DataRecord((Node)parametersInRaw);
		assertEquals(1, parametersIn.getItemNames().size());
		Item_Count paramIn = (Item_Count)parametersIn.getItem("myparam");
		assertEquals(3, paramIn.getValue());
	}
	
	private void assertEmptyItemsDoc(ProcessProductionSchedule testObjectIn)
	{
		// This function asserts the object in "ProcessProductionSchedule_EmptyItems.xml".
		// The same function is also used as writing is tested.
		
		// One schedule expected
  		assertEquals(1, testObjectIn.getProductionSchedules().size());
        ProductionSchedule schedule1 = testObjectIn.getProductionSchedules().get(0);
		
        // Three production requests expected
        assertEquals(3, schedule1.getProductionRequests().size());
        ProductionRequest request1 = schedule1.getProductionRequests().get(0);
        ProductionRequest request2 = schedule1.getProductionRequests().get(1);
        ProductionRequest request3 = schedule1.getProductionRequests().get(2);
        
		// 1) Assert empty production request (request 1)
        assertNull(request1.getIdentifier());
        assertNull(request1.getHierarchyScope());
        assertEquals(0, request1.getSegmentRequirements().size());

        // 2) Asserting segment requirement (request 2)
        assertEquals(1, request2.getSegmentRequirements().size());
        assertEmptySegmentRequirement(request2.getSegmentRequirements().get(0));
        
        // 3) Asserting empty equipment requirement (request 3)
        EquipmentRequirement eqReq_3_1 = request3.getSegmentRequirements().get(0).getEquipmentRequirements().get(0);
        assertEquals(0, eqReq_3_1.getQuantities().size());
        
        // 4) Asserting minimal material requirement (request 3)
        MaterialRequirement matReq_3_1 = request3.getSegmentRequirements().get(0).getMaterialRequirements().get(0);
        assertEquals(0, matReq_3_1.getMaterialDefinitionIdentifiers().size());
        assertEquals(0, matReq_3_1.getMaterialLotIdentifiers().size());
        assertNull(matReq_3_1.getMaterialUse());
        assertEquals(0, matReq_3_1.getQuantities().size());
        assertEquals(0, matReq_3_1.getAssemblyRequirements().size());
        
        // 5) Asserting minimal quantity value (request 3)
        MaterialRequirement matReq_3_2 = request3.getSegmentRequirements().get(0).getMaterialRequirements().get(1);
        QuantityValue quantityX = matReq_3_2.getQuantities().get(0);
        assertNull(quantityX.getDataType());
        assertTrue(quantityX.getUnitOfMeasure() == null);
        assertNull(quantityX.getKey());
	}
	
	private void assertEmptySegmentRequirement(SegmentRequirement segReq1)
	{
		// Process segment ID
        assertNull(segReq1.getProcessSegmentIdentifier());
		
		// Times
        assertNull(segReq1.getEarliestStartTime());
        assertNull(segReq1.getLatestEndTime());
        
        // Equipment
        assertEquals(0, segReq1.getEquipmentRequirements().size());
        
        // Material
        assertEquals(0, segReq1.getMaterialRequirements().size());
        
        // Segment (nested)
        assertEquals(0, segReq1.getSegmentRequirements().size());
	}
	
	private void assertEmptyProcessMsg(ProcessProductionSchedule testObject)
	{
		assertEquals(1, testObject.getProductionSchedules().size());
 		assertEquals(0, testObject.getProductionSchedules().get(0).getProductionRequests().size());
	}
	
	private DateTime getUtcTime(String xsdDateTime)
	{
		return DateTime.parse(xsdDateTime).withZone(DateTimeZone.UTC);
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
	
	private static ProcessProductionSchedule getTestObjectFromFile(String filename) throws InvalidMessageException
	{
		String filepath = System.getProperty("user.dir") + "/../common/testfiles/" + filename;
		
		try
		{
			byte[] raw = Files.readAllBytes(Paths.get(filepath));
			
			return new ProcessProductionSchedule(raw);
		}
		catch (IOException e)
		{
			// Throwing a runtime ex to avoid the needs for "throws" in this test
			throw new RuntimeException(e);
		}
	}
	
	private void validateXmlDoc(byte[] xmlBytes)
	{
		ByteArrayInputStream stream = null;
		
		try
		{
			stream = new ByteArrayInputStream(xmlBytes);
			Source xmlFile = new StreamSource(stream);
			
			if (m_validator == null)
			{
				SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
				String schemaPath = System.getProperty("user.dir") + "\\..\\Schemata\\helper.xsd";
				Schema observationSchema = schemaFactory.newSchema(new File(schemaPath));
				m_validator = observationSchema.newValidator();
			}
			
			m_validator.validate(xmlFile);
		}
		catch (SAXException e)
		{
			fail("XML validation failed: " + e.getMessage());
		}
		catch (Exception e)
		{
			// Use runtime ex to avoid considering exceptions in tests
			throw new RuntimeException(e.getMessage(), e);
		}
		finally
		{
			if (stream != null)
			{
				try {
					stream.close();
				} catch (Exception ignore) {}
			}
		}
	}
}
