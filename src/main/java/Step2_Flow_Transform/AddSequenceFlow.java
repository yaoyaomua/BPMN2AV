package Step2_Flow_Transform;

import java.util.Collection;
import java.util.Iterator;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnEdge;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnShape;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;

public class AddSequenceFlow {


    public AddSequenceFlow() {
    }

    //covert message flow to sequence flow
    public static void add(BpmnModelInstance modelInstance,Collection<MessageFlow> messageflows) {
        // Iteration All MessageFlow
        Iterator<MessageFlow> iterator_messageflows = messageflows.iterator();
        //Check messageflow
        int i = 0;
        while (iterator_messageflows.hasNext()) {
            i++;
            MessageFlow messageflow = iterator_messageflows.next();
            // 对 messageflow对象进行操作
            if ((messageflow.getTarget().getElementType().getTypeName().equals("intermediateThrowEvent")) || (messageflow.getTarget().getElementType().getTypeName().equals("intermediateThrowEvent")))
            {

            }
            //whether include start event or end event
            //set the width and height of the new activity
            if((messageflow.getTarget().getElementType().getTypeName().equals("startEvent") ))
            {
                if(!(messageflow.getTarget().getParentElement().getElementType().getTypeName().equals("subProcess"))) {
                    StartEvent acpre = modelInstance.getModelElementById(messageflow.getTarget().getId());
                    IntermediateCatchEvent ac = modelInstance.newInstance(IntermediateCatchEvent.class);
                    ac.setName(acpre.getName());
                    ac.setId(acpre.getId());
                    for (EventDefinition eventDefinition : acpre.getEventDefinitions()) {
                        ac.getEventDefinitions().add(eventDefinition);
                    }
                    for (DataOutputAssociation dataOutputAssociation : acpre.getDataOutputAssociations()) {
                        ac.getDataOutputAssociations().add(dataOutputAssociation);
                    }
                    for (Property property : acpre.getProperties()) {
                        ac.getProperties().add(property);
                    }
                    acpre.replaceWithElement(ac);
                    messageflow.setTarget(ac);
                }
                //Subprocess
                else
                {
                    messageflow.setTarget((SubProcess)messageflow.getTarget().getParentElement());
                }
            }
            if(messageflow.getSource().getElementType().getTypeName().equals("endEvent"))
            {

                if (!(messageflow.getSource().getParentElement().getElementType().getTypeName().equals("subProcess"))) {
                    EndEvent acpre = modelInstance.getModelElementById(messageflow.getSource().getId());
                    IntermediateThrowEvent ac = modelInstance.newInstance(IntermediateThrowEvent.class);
                    ac.setName(acpre.getName());
                    ac.setId(acpre.getId());
                    for (EventDefinition eventDefinition : acpre.getEventDefinitions()) {
                        ac.getEventDefinitions().add(eventDefinition);
                    }
                    for (DataInputAssociation dataInputAssociation : acpre.getDataInputAssociations()) {
                        ac.getDataInputAssociations().add(dataInputAssociation);
                    }
                    for (Property property : acpre.getProperties()) {
                        ac.getProperties().add(property);
                    }
                    acpre.replaceWithElement(ac);
                    messageflow.setSource(ac);}

                /*EndEvent acpre = modelInstance.getModelElementById(messageflow.getSource().getId());
                IntermediateThrowEvent ac = modelInstance.newInstance(IntermediateThrowEvent.class);
                ac.setName("End"+i);
                ac.setId(acpre.getId());
                for (EventDefinition eventDefinition : acpre.getEventDefinitions()){
                    ac.getEventDefinitions().add(eventDefinition);
                }*/
                //Subprocess
                else
                {
                    messageflow.setSource((SubProcess)messageflow.getSource().getParentElement());
                }
            }


            // Create a new sequence flow element
            SequenceFlow sequenceFlow = modelInstance.newInstance(SequenceFlow.class);
            sequenceFlow.setId(messageflow.getId());
            //System.out.println(messageflow.getName());
            sequenceFlow.setName(messageflow.getName());
            //System.out.println(sequenceFlow.getName());

            if (!messageflow.getSource().getElementType().getTypeName().equals("endEvent")) {
                sequenceFlow.setSource(modelInstance.getModelElementById(messageflow.getSource().getId()));
            }
            else
            {
                ModelElementInstance subProcess = messageflow.getSource().getParentElement();
                sequenceFlow.setSource((FlowNode) subProcess);
            }
            if (!messageflow.getTarget().getElementType().getTypeName().equals("startEvent")) {
                sequenceFlow.setTarget(modelInstance.getModelElementById(messageflow.getTarget().getId()));
            }
            else
            {
                ModelElementInstance subProcess = messageflow.getTarget().getParentElement();
                sequenceFlow.setTarget((FlowNode) subProcess);
            }


            // Add the sequence flow element to the model instance
            modelInstance.getModelElementsByType(Process.class).iterator().next().addChildElement(sequenceFlow);

            // Add bpmnelement to bpmnEdge
            BpmnEdge bpmnEdge = modelInstance.getModelElementById(messageflow.getId()+"_di");
            BaseElement bpmnElement = modelInstance.getModelElementById(messageflow.getId());
            bpmnEdge.setBpmnElement(bpmnElement);


        }


    }
}
