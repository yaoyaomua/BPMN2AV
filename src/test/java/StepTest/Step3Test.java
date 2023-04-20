package StepTest;

import Step3_Delete_Element.AddSuperStartEndEvent;
import Step3_Delete_Element.DeleteElement;
import Step4_Well_Structure.DeleteEmptySubprocess;
import Step4_Well_Structure.Delete121Gateway;
import Step4_Well_Structure.DeleteRepeatFlow;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.Gateway;
import org.junit.Test;

import java.io.File;

public class Step3Test {

    @Test
    public void gatewaytest1(){
        BpmnModelInstance bpmnModelInstance = Bpmn.readModelFromFile(new File("models/diagram5.bpmn"));
        for (Gateway gateway: bpmnModelInstance.getModelElementsByType(Gateway.class)){
            System.out.println(gateway.getId());
        }
    }

    @Test
    public void gatewayTest2(){
        try {
            BpmnModelInstance bpmnModelInstance = Bpmn.readModelFromFile(new File("models/output1.bpmn"));
            Delete121Gateway.delete(bpmnModelInstance);
            File outputFile = new File("models/output1.bpmn");
            Bpmn.writeModelToFile(outputFile, bpmnModelInstance);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void deleteElementTest4(){
//        try {
//            BpmnModelInstance bpmnModelInstance = Bpmn.readModelFromFile(new File("models/diagram (15).bpmn"));
//            DeleteElement.delete(bpmnModelInstance);
//            File outputFile = new File("models/result.bpmn");
//            Bpmn.writeModelToFile(outputFile, bpmnModelInstance);
//        }catch (Exception e) {
//            e.printStackTrace();
//        }
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
            DeleteElement.delete(bpmnModelInstance,"truck");
            File outputFile = new File("models/Steps/step3_delete_boundary_event_result.bpmn");
            Bpmn.writeModelToFile(outputFile, bpmnModelInstance);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
