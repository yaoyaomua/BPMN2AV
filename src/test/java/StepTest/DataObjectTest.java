package StepTest;

import Add_Data_Object.AddDataObjectFromJSON;
import Step3_Delete_Element.DeleteTask;
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
}
