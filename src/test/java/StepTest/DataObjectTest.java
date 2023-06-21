package StepTest;

import Add_Data_Object.AddDataObjectFromJSON;
import Add_Data_Object.AddDataObjectWithoutState;
import Add_Data_Object.ReadJson;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.junit.Test;

import java.io.File;

public class DataObjectTest {
    @Test
    public void preprocessJsonTest(){
        try {
            BpmnModelInstance bpmnModelInstance = Bpmn.readModelFromFile(new File("models/DataObject/testPart1.bpmn"));
            File jsonFile = new File("models/DataObject/testPart1.json");
            AddDataObjectFromJSON.add(bpmnModelInstance,jsonFile);
            File outputFile = new File("models/DataObject/PreprocessTestPart1_result.bpmn");
            Bpmn.writeModelToFile(outputFile, bpmnModelInstance);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    public void addDataObjectTest(){
        try {
            BpmnModelInstance bpmnModelInstance = Bpmn.readModelFromFile(new File("models/DataObject/testPart1.bpmn"));
            File jsonFile = new File("models/DataObject/testPart1.json");
            AddDataObjectFromJSON.add(bpmnModelInstance,jsonFile);
            File outputFile = new File("models/DataObject/testPart1_result.bpmn");
            Bpmn.writeModelToFile(outputFile, bpmnModelInstance);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    public void addDataObjectWithoutStateTest(){
        try {
            BpmnModelInstance bpmnModelInstance = Bpmn.readModelFromFile(new File("models/DataObject/testPart1_result.bpmn"));
            AddDataObjectWithoutState.add(bpmnModelInstance,"Truck");
            File outputFile = new File("models/DataObject/testPart1WithoutStateTest_result.bpmn");
            Bpmn.writeModelToFile(outputFile, bpmnModelInstance);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
