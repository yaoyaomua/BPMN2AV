package StepTest;

import Add_Data_Object.AddDataObjectFromJSON;
import Add_Data_Object.AddDataObjectWithoutState;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.junit.Test;

import java.io.File;

public class DataObjectTest {
    @Test
    public void addDataObjectTest(){
        try {
            BpmnModelInstance bpmnModelInstance = Bpmn.readModelFromFile(new File("models/DataObject/DataObjectTest.bpmn"));
            File jsonFile = new File("models/DataObject/DataObject.json");
            AddDataObjectFromJSON.add(bpmnModelInstance,jsonFile);
            File outputFile = new File("models/DataObject/DataObjectTest_result.bpmn");
            Bpmn.writeModelToFile(outputFile, bpmnModelInstance);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    public void addDataObjectWithoutStateTest(){
        try {
            BpmnModelInstance bpmnModelInstance = Bpmn.readModelFromFile(new File("models/DataObject/DataObjectWithoutStateTest.bpmn"));
            AddDataObjectWithoutState.add(bpmnModelInstance,"Container");
            File outputFile = new File("models/DataObject/DataObjectWithoutStateTest_result.bpmn");
            Bpmn.writeModelToFile(outputFile, bpmnModelInstance);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
