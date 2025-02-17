package Step3_Delete_Element;

import Step2_Flow_Transform.AddIncomingOrOutcoming;
import Step2_Flow_Transform.CreateBPMNEdge;
import Step2_Flow_Transform.GetBounds;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnEdge;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnPlane;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnShape;
import org.camunda.bpm.model.bpmn.instance.dc.Bounds;

import java.util.*;

public class DeleteEvent {

    public DeleteEvent() {
    }

    public static void delete(BpmnModelInstance modelInstance, String artifact, HashSet<String> addedEvent){
        List<String> StartEventToDelete = new ArrayList<>();
        List<String> EndEventToDelete = new ArrayList<>();
        List<String> MidEventToDelete = new ArrayList<>();

//        HashMap<String, String> dataState = DataTextState.getText(modelInstance);

        for (StartEvent event : modelInstance.getModelElementsByType(StartEvent.class)){
            if (addedEvent.contains(event.getId())) continue;
            if (event.getDataOutputAssociations().isEmpty()){
                StartEventToDelete.add(event.getId());
            }else{
                boolean irr = false;
                for (DataOutputAssociation dataOutputAssociation: event.getDataOutputAssociations()){
                    String dataId = dataOutputAssociation.getTarget().getId();
                    DataObjectReference data = modelInstance.getModelElementById(dataId);
                    if (data.getDataState() == null){
                        if (!data.getName().equals(artifact)){
                            irr = true;
//                            StartEventToDelete.add(event.getId());
                        }
                    }else {
                        if (data.getName().equals(artifact)){
//                            StartEventToDelete.add(event.getId());
                            irr = true;
                        }
                    }
                }
                if (!irr){
                    StartEventToDelete.add(event.getId());
                }
            }
        }

        for (EndEvent event : modelInstance.getModelElementsByType(EndEvent.class)){
            if (addedEvent.contains(event.getId())) continue;
            if (event.getDataInputAssociations().isEmpty()){
                EndEventToDelete.add(event.getId());
            }else {
                boolean irr = false;
                for (DataInputAssociation dataInputAssociation : event.getDataInputAssociations()){
                    for (ItemAwareElement element : dataInputAssociation.getSources()){
                        String dataId = element.getId();
                        DataObjectReference data = modelInstance.getModelElementById(dataId);
//                        if (dataState.get(dataId) == null){
                        if (data.getDataState() == null){
                            if (!data.getName().equals(artifact)){
                                irr = true;
//                                EndEventToDelete.add(event.getId());
                            }
                        }else {
                            if (data.getName().equals(artifact)){
                                irr = true;
//                                EndEventToDelete.add(event.getId());
                            }
                        }
                    }
                }
                if (!irr){
                    EndEventToDelete.add(event.getId());
                }
            }
        }

        for (IntermediateThrowEvent event : modelInstance.getModelElementsByType(IntermediateThrowEvent.class)){
            if (addedEvent.contains(event.getId())) continue;
            if(event.getDataInputAssociations().isEmpty()){
                MidEventToDelete.add(event.getId());
            }else {
                boolean irr = false;
                for (DataInputAssociation dataInputAssociation : event.getDataInputAssociations()){
                    for (ItemAwareElement element : dataInputAssociation.getSources()){
                        String dataId = element.getId();
                        DataObjectReference data = modelInstance.getModelElementById(dataId);
//                        if (dataState.get(dataId) == null){
                        if (data.getDataState() == null){
                            if (!data.getName().equals(artifact)){
                                irr = true;
//                                MidEventToDelete.add(event.getId());
                            }
                        }else {
                            if (data.getName().equals(artifact)){
                                irr = true;
//                                MidEventToDelete.add(event.getId());
                            }
                        }
                    }
                }
                if (!irr){
                    MidEventToDelete.add(event.getId());
                }
            }
        }


        for (IntermediateCatchEvent event : modelInstance.getModelElementsByType(IntermediateCatchEvent.class)){
            if (addedEvent.contains(event.getId())) continue;
            if(event.getDataOutputAssociations().isEmpty()) {
                MidEventToDelete.add(event.getId());
            }else{
                boolean irr = false;
                for (DataOutputAssociation dataOutputAssociation: event.getDataOutputAssociations()){
                    String dataId = dataOutputAssociation.getTarget().getId();
                    DataObjectReference data = modelInstance.getModelElementById(dataId);
                    if (data.getDataState() == null){
//                    if (dataState.get(dataId) == null){
                        if (!data.getName().equals(artifact)){
                            irr = true;
//                            MidEventToDelete.add(event.getId());
                        }
                    }else {
                        if (data.getName().equals(artifact)){
                            irr = true;
//                            MidEventToDelete.add(event.getId());
                        }
                    }
                }
                if (!irr){
                    MidEventToDelete.add(event.getId());
                }
            }
        }
        //start delete event
        for (String id : MidEventToDelete){
            Event event = modelInstance.getModelElementById(id);
            if (event.getOutgoing().size() > 1 || event.getIncoming().size() > 1){
                System.out.println("Warning: flow size greater than 1, please add gateway first!!");
                return;
            }

            System.out.println("event delete: " + event.getName());

            SequenceFlow sourceFlow = event.getIncoming().iterator().next();
            SequenceFlow targetFlow = event.getOutgoing().iterator().next();
            //add new flow for deleting the event
            SequenceFlow newFlow = modelInstance.newInstance(SequenceFlow.class);
            newFlow.setId(GenerateID.getID("Flow_",modelInstance));
            if (sourceFlow.getSource() != null){
                newFlow.setSource(sourceFlow.getSource());
            }else {
                System.out.println("No Source!!");
            }

            if (targetFlow.getTarget() != null){
                newFlow.setTarget(targetFlow.getTarget());
            }else {
                System.out.println("No Target!!");
            }
            newFlow.setName(CombineSequenceFlowCondition.combine(sourceFlow,targetFlow));

            event.getParentElement().addChildElement(newFlow);
            AddIncomingOrOutcoming.AddIncomingToElement(modelInstance,newFlow.getTarget().getId(),newFlow);
            AddIncomingOrOutcoming.AddOutgoingToElement(modelInstance,newFlow.getSource().getId(),newFlow);

            //add graph
            BpmnPlane plane = modelInstance.getModelElementsByType(BpmnPlane.class).iterator().next();

            Bounds bounds1 = GetBounds.get(modelInstance,newFlow.getSource().getId());
            Bounds bounds2 = GetBounds.get(modelInstance,newFlow.getTarget().getId());
            BpmnEdge edge = CreateBPMNEdge.create(modelInstance,newFlow,
                    bounds1.getX() + bounds1.getWidth()/2, bounds1.getY() + bounds1.getHeight()/2,
                    bounds2.getX() + bounds2.getWidth()/2, bounds2.getY() + bounds2.getHeight()/2);
            plane.addChildElement(edge);
            event.getParentElement().removeChildElement(event);
            String src = sourceFlow.getId();
            String tgt = targetFlow.getId();

            sourceFlow.getParentElement().removeChildElement(sourceFlow);
            targetFlow.getParentElement().removeChildElement(targetFlow);
        }

//      delete start event: transform first
        for (String id : StartEventToDelete){
//            System.out.println(id);
            Event event = modelInstance.getModelElementById(id);
            for (SequenceFlow sequenceFlow : event.getOutgoing()){
//                sequenceFlow.getDiagramElement().getParentElement().removeChildElement(sequenceFlow.getDiagramElement());
                sequenceFlow.getParentElement().removeChildElement(sequenceFlow);
            }
//            event.getDiagramElement().getParentElement().removeChildElement(event.getDiagramElement());
            event.getParentElement().removeChildElement(event);
        }

//        System.out.println("*********delete end event*******");
        for (String id : EndEventToDelete){
//            System.out.println(id);
            Event event = modelInstance.getModelElementById(id);
            for (SequenceFlow sequenceFlow : event.getIncoming()){
                sequenceFlow.getParentElement().removeChildElement(sequenceFlow);
            }
            event.getParentElement().removeChildElement(event);
        }

        for (Association association : modelInstance.getModelElementsByType(Association.class)){
            if (association.getSource() == null){
                association.getParentElement().removeChildElement(association);
            }
        }

        for (BpmnShape bpmnShape : modelInstance.getModelElementsByType(BpmnShape.class)){
            if (bpmnShape.getBpmnElement() == null){
                bpmnShape.getParentElement().removeChildElement(bpmnShape);
            }
        }

        for (BpmnEdge bpmnEdge : modelInstance.getModelElementsByType(BpmnEdge.class)){
            if (bpmnEdge.getBpmnElement() == null){
                bpmnEdge.getParentElement().removeChildElement(bpmnEdge);
            }
        }
//        System.out.println(modelInstance.getModelElementsByType(SequenceFlow.class).toString());

    }

}


