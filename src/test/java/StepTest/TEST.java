package StepTest;

import Step3_Delete_Element.DataTextState;
import Step3_Delete_Element.DeleteElement;
import Step3_Delete_Element.DeleteEvent;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;
import org.junit.FixMethodOrder;
import org.junit.Test;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class TEST {

    @Test
    public void test1(){
        BpmnModelInstance bpmnModelInstance = Bpmn.readModelFromFile(new File("models/diagram (19).bpmn"));
//        for (DataState state:bpmnModelInstance.getModelElementsByType(DataState.class)) {
//            System.out.println(state.getId());
//        }
        for (DataObjectReference dataObjectReference : bpmnModelInstance.getModelElementsByType(DataObjectReference.class)){
            System.out.println(dataObjectReference.getId());
        }
        for (DataObject dataObject: bpmnModelInstance.getModelElementsByType(DataObject.class)){
            System.out.println(dataObject.getId());
        }
        for (Task task :bpmnModelInstance.getModelElementsByType(Task.class)){
            for (DataInputAssociation dataInputAssociation : task.getDataInputAssociations()){
                for (ItemAwareElement itemAwareElement : dataInputAssociation.getSources()){
                    System.out.println(itemAwareElement.getId());
                }
            }
        }


        System.out.println("***************start get text state******************");
        String artifact = "DATA1";
        HashMap<String,Boolean> textState = DataTextState.getText(bpmnModelInstance);
        System.out.println(textState.toString());
        System.out.println("***************start delete activity******************");

        DeleteElement.delete(bpmnModelInstance,artifact);
        try {
            File outputFile = new File("models/result.bpmn");
            Bpmn.writeModelToFile(outputFile, bpmnModelInstance);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test2(){
        BpmnModelInstance bpmnModelInstance = Bpmn.readModelFromFile(new File("models/diagram (22).bpmn"));
//        for (StartEvent event:bpmnModelInstance.getModelElementsByType(StartEvent.class)){
//            System.out.println(event.getId());
//        }
        for (Event event: bpmnModelInstance.getModelElementsByType(Event.class)){
            System.out.println(event.getId());
        }
    }

    @Test
    public void test3(){
        BpmnModelInstance bpmnModelInstance = Bpmn.readModelFromFile(new File("models/diagram (27).bpmn"));
        DeleteEvent.delete(bpmnModelInstance);
        try {
            File outputFile = new File("models/result.bpmn");
            Bpmn.writeModelToFile(outputFile, bpmnModelInstance);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
