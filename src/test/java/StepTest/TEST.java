package StepTest;

import Step3_Delete_Element.*;
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
        HashMap<String,String> textState = DataTextState.getText(bpmnModelInstance);
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

    @Test
    public void test4(){
        BpmnModelInstance bpmnModelInstance = Bpmn.readModelFromFile(new File("models/diagram (29).bpmn"));
        for (BoundaryEvent event : bpmnModelInstance.getModelElementsByType(BoundaryEvent.class)){
            System.out.println(event.getId());
        }
    }

    @Test
    public void test5(){
        BpmnModelInstance bpmnModelInstance = Bpmn.readModelFromFile(new File("models/diagram (31).bpmn"));
        HashMap<String,String> textData = DataTextState.getText(bpmnModelInstance);
        HashSet<String> set = DataTextState.getAssociatedDataObject(bpmnModelInstance,"DATA1");
        System.out.println(set.toString());
        DeleteDataObject.delete(bpmnModelInstance,set,textData);
        try {
            File outputFile = new File("models/result.bpmn");
            Bpmn.writeModelToFile(outputFile, bpmnModelInstance);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test6(){
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
    public void test7(){
        BpmnModelInstance bpmnModelInstance = Bpmn.readModelFromFile(new File("models/diagram (34).bpmn"));
        AddSuperStartEndEvent.addEnd(bpmnModelInstance);
        try {
            File outputFile = new File("models/result.bpmn");
            Bpmn.writeModelToFile(outputFile, bpmnModelInstance);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
