package Step1_Delete_Pool;

import Step2_Flow_Transform.CreateBPMNEdge;
import Step2_Flow_Transform.CreateBPMNShape;
import Step3_Delete_Element.GenerateID;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnEdge;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnPlane;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnShape;
import org.camunda.bpm.model.bpmn.instance.dc.Bounds;

import java.util.HashMap;

public class AddExclusiveGatewayForEndEvent {

    public AddExclusiveGatewayForEndEvent() {
    }

    public static void add(BpmnModelInstance modelInstance){
        HashMap<String,String> process2participant = new HashMap<>();
        BpmnPlane plane = modelInstance.getModelElementsByType(BpmnPlane.class).iterator().next();
        for (Participant participant : modelInstance.getModelElementsByType(Participant.class)){
            process2participant.put(participant.getProcess().getId(),participant.getId());
        }
        int count = 0;
        for(Process process : modelInstance.getModelElementsByType(Process.class)){
            if(process.getChildElementsByType(EndEvent.class).size() == 1){
                continue;
            }else {
                //creat end event and its shape
                EndEvent processEnd = modelInstance.newInstance(EndEvent.class);
                processEnd.setId(GenerateID.getID("EndEvent_", modelInstance));
                if (process.getName() != null){
                    processEnd.setName(process.getName() + " end");
                }else {
                    processEnd.setName("ProcessEnd");
                }
                process.addChildElement(processEnd);
                double endX = 900;
                double endY = 10;
                if(process2participant.containsKey(process.getId())){
                    Participant participant = modelInstance.getModelElementById(process2participant.get(process.getId()));
                    BpmnShape participantShape= modelInstance.getModelElementById(participant.getDiagramElement().getId());
                    Bounds bounds = participantShape.getBounds();
                    endX = bounds.getX() + bounds.getWidth() - 50;
                    endY = bounds.getY() -10;
                }
                BpmnShape processEndShape = CreateBPMNShape.create(modelInstance,processEnd.getId(), endX, endY, 36.0,36.0);
                plane.addChildElement(processEndShape);

                //creat exclusive gateway and its shape
                ExclusiveGateway exclusiveGateway = modelInstance.newInstance(ExclusiveGateway.class);
                exclusiveGateway.setId(GenerateID.getID("ExclusiveEndGateway_", modelInstance));
                exclusiveGateway.setName("Exclusive Gateway for Multiple End Event");
                process.addChildElement(exclusiveGateway);
                BpmnShape exclusiveFGatewayShape = CreateBPMNShape.create(modelInstance,exclusiveGateway.getId(),endX,endY+90,50.0,50.0);
                plane.addChildElement(exclusiveFGatewayShape);

                //connect gateway and end event
                //creat sequence flow
                SequenceFlow endFlow = modelInstance.newInstance(SequenceFlow.class);
                endFlow.setId(GenerateID.getID("Flow_", modelInstance));
                endFlow.setTarget(modelInstance.getModelElementById(processEnd.getId()));
                endFlow.setSource(modelInstance.getModelElementById(exclusiveGateway.getId()));
                process.addChildElement(endFlow);
                //update incoming and out going
                processEnd.getIncoming().add(endFlow);
                exclusiveGateway.getOutgoing().add(endFlow);
                //creat flow edge
                BpmnEdge gateway2end = CreateBPMNEdge.create(modelInstance,endFlow, endX+25, endY+90,endX+25,endY+30);

                //for each end event, transform it and add flow between it and gateway
                for (EndEvent acpre : process.getChildElementsByType(EndEvent.class)){
                    //pass the end event we add before
                    if (acpre.getId() == processEnd.getId()) continue;
                    IntermediateThrowEvent ac = modelInstance.newInstance(IntermediateThrowEvent.class);
                    //replace end event with intermediate event
                    if (acpre.getName() != null) {
                        ac.setName(acpre.getName() + "_change");
                    } else {
                        ac.setName("EndEvent_" + count);
                        count++;
                    }
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
                    acpre.getDiagramElement().setBpmnElement(ac);

                    //add sequence flow between event and gateway
                    //creat sequence flow
                    SequenceFlow acFlow = modelInstance.newInstance(SequenceFlow.class);
                    acFlow.setId(GenerateID.getID("Flow_", modelInstance));
                    acFlow.setSource(modelInstance.getModelElementById(ac.getId()));
                    acFlow.setTarget(modelInstance.getModelElementById(exclusiveGateway.getId()));
                    process.addChildElement(acFlow);
                    exclusiveGateway.getIncoming().add(acFlow);
                    ac.getOutgoing().add(acFlow);

                    //creat sequence flow bpmn edge
                    Bounds acBounds = ac.getDiagramElement().getBounds();
                    BpmnEdge acEdge = CreateBPMNEdge.create(modelInstance,acFlow,acBounds.getX() + acBounds.getHeight() / 2, acBounds.getY() + acBounds.getWidth() / 2, endX+25,endY+115);
                    plane.addChildElement(acEdge);
                }
            }
        }
    }
}
