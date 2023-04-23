package StepTest;

import Step3_Delete_Element.AddSuperStartEndEvent;
import Step3_Delete_Element.DeleteTask;
import Step3_Delete_Element.DeleteBoundaryEvent;
import Step3_Delete_Element.DeleteEvent;
import Step4_Well_Structure.DeleteEmptySubprocess;
import Step4_Well_Structure.Delete121Gateway;
import Step4_Well_Structure.DeleteRepeatFlow;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.junit.Test;

import java.io.File;

public class Step3Test {

    @Test
    public void deleteElementTest(){
        try {
            BpmnModelInstance bpmnModelInstance = Bpmn.readModelFromFile(new File("models/Steps/step3.bpmn"));
            DeleteTask.delete(bpmnModelInstance, "DATA1");
            File outputFile = new File("models/Steps/step3_delete_Task_result.bpmn");
            Bpmn.writeModelToFile(outputFile, bpmnModelInstance);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void deleteEventTest(){
        BpmnModelInstance bpmnModelInstance = Bpmn.readModelFromFile(new File("models/diagram (46).bpmn"));
        DeleteEvent.delete(bpmnModelInstance, "DATA1");
        try {
            File outputFile = new File("models/Steps/step3_delete_Event_result.bpmn");
            Bpmn.writeModelToFile(outputFile, bpmnModelInstance);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void delete121GatewayTest(){
        try {
            BpmnModelInstance bpmnModelInstance = Bpmn.readModelFromFile(new File("models/Steps/step3result.bpmn"));
            Delete121Gateway.delete(bpmnModelInstance);
            File outputFile = new File("models/Steps/step3_121_result.bpmn");
            Bpmn.writeModelToFile(outputFile, bpmnModelInstance);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void deleteRepeatedFlows(){
        try {
            BpmnModelInstance bpmnModelInstance = Bpmn.readModelFromFile(new File("models/Steps/step3_repeated_flows.bpmn"));
            DeleteRepeatFlow.delete(bpmnModelInstance);
            File outputFile = new File("models/Steps/step3_repeated_result.bpmn");
            Bpmn.writeModelToFile(outputFile, bpmnModelInstance);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void deleteEmptySubProcess(){
        try {
            BpmnModelInstance bpmnModelInstance = Bpmn.readModelFromFile(new File("models/Steps/step3_empty_subprocess.bpmn"));
            DeleteEmptySubprocess.delete(bpmnModelInstance);
            File outputFile = new File("models/Steps/step3_empty_subprocess_result.bpmn");
            Bpmn.writeModelToFile(outputFile, bpmnModelInstance);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void AddSuperStart(){
        BpmnModelInstance bpmnModelInstance = Bpmn.readModelFromFile(new File("models/Steps/step3_add_super_start.bpmn"));
        AddSuperStartEndEvent.addStart(bpmnModelInstance);
        try {
            File outputFile = new File("models/AddStartResult.bpmn");
            Bpmn.writeModelToFile(outputFile, bpmnModelInstance);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void AddSuperEnd(){
        BpmnModelInstance bpmnModelInstance = Bpmn.readModelFromFile(new File("models/Steps/step3_add_super_end.bpmn"));
        AddSuperStartEndEvent.addEnd(bpmnModelInstance);
        try {
            File outputFile = new File("models/AddEndResult.bpmn");
            Bpmn.writeModelToFile(outputFile, bpmnModelInstance);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    public void deleteBoundaryEvent(){
        try {
            BpmnModelInstance bpmnModelInstance = Bpmn.readModelFromFile(new File("models/Steps/step3_delete_boundary_event.bpmn"));
            DeleteBoundaryEvent.delete(bpmnModelInstance,"DATA1");
            File outputFile = new File("models/Steps/step3_delete_boundary_event_result.bpmn");
            Bpmn.writeModelToFile(outputFile, bpmnModelInstance);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    public void deleteElement(){
        try {
            BpmnModelInstance bpmnModelInstance = Bpmn.readModelFromFile(new File("models/Steps/step3_delete_boundary_event_result.bpmn"));
            DeleteTask.delete(bpmnModelInstance,"DATA1");
            File outputFile = new File("models/Steps/step3_delete_element_result.bpmn");
            Bpmn.writeModelToFile(outputFile, bpmnModelInstance);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
