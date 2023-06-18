package StepTest;

import Step3_Delete_Element.*;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;

public class TEST {

    @Test
    public void test1(){
        try {
//            BpmnModelInstance bpmnModelInstance = Bpmn.readModelFromFile(new File("models/diagram (82).bpmn"));


            BpmnModelInstance bpmnModelInstance = Bpmn.readModelFromFile(new File("models/View/diagram20/diagram20.bpmn"));



            HashMap<String,String> datas = DataTextState.getText(bpmnModelInstance);
            for (DataObjectReference dataObjectReference: bpmnModelInstance.getModelElementsByType(DataObjectReference.class)){
                Association association = bpmnModelInstance.getModelElementById(datas.get(dataObjectReference.getId()));
                if (association == null) continue;
                TextAnnotation textAnnotation = bpmnModelInstance.getModelElementById(association.getTarget().getId());
                String state = textAnnotation.getTextContent();
                DataState dataState = bpmnModelInstance.newInstance(DataState.class);
                String stateId = GenerateID.getID("state",bpmnModelInstance);
                dataState.setId(stateId);
                dataState.setName(state);
                dataObjectReference.setDataState(dataState);
            }

            File step3output= new File("models/View/diagram20/diagram20.bpmn");


            Bpmn.writeModelToFile(step3output, bpmnModelInstance);


        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test2(){
        try {
//            BpmnModelInstance bpmnModelInstance = Bpmn.readModelFromFile(new File("models/diagram (82).bpmn"));

            BpmnModelInstance bpmnModelInstance = Bpmn.readModelFromFile(new File("models/View/diagram16/diagram16.bpmn"));
            for (InclusiveGateway gateway : bpmnModelInstance.getModelElementsByType(InclusiveGateway.class)){
                System.out.println(gateway instanceof InclusiveGateway);
                System.out.println(gateway.getId());
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test3(){

    }
}
