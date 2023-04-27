package Step3_Delete_Element;

import Step2_Flow_Transform.AddIncomingOrOutcoming;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnEdge;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnPlane;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnShape;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DeleteEvent {

    public DeleteEvent() {
    }

    public static void delete(BpmnModelInstance modelInstance, String artifact){
        List<String> StartEventToDelete = new ArrayList<>();
        List<String> EndEventToDelete = new ArrayList<>();
        List<String> MidEventToDelete = new ArrayList<>();

        HashMap<String, String> dataState = DataTextState.getText(modelInstance);

        for (StartEvent event : modelInstance.getModelElementsByType(StartEvent.class)){
            System.out.println(event.getId());
            if (event.getDataOutputAssociations().isEmpty()){
                StartEventToDelete.add(event.getId());
                System.out.println("need delete : " + event.getId());
            }else{
                for (DataOutputAssociation dataOutputAssociation: event.getDataOutputAssociations()){
                    String dataId = dataOutputAssociation.getTarget().getId();
                    DataObjectReference data = modelInstance.getModelElementById(dataId);

                    if (dataState.get(dataId) == null){
                        if (data.getName().equals(artifact)){
                            StartEventToDelete.add(event.getId());
                            System.out.println("need delete : " + event.getId());
                        }
                    }else {
                        if (!data.getName().equals(artifact)){
                            StartEventToDelete.add(event.getId());
                            System.out.println("need delete : " + event.getId());
                        }
                    }
                }
            }
        }
        for (EndEvent event : modelInstance.getModelElementsByType(EndEvent.class)){
            System.out.println(event.getId());
            if (event.getDataInputAssociations().isEmpty()){
                EndEventToDelete.add(event.getId());
                System.out.println("need delete : " + event.getId());
            }else {
                for (DataInputAssociation dataInputAssociation : event.getDataInputAssociations()){
                    for (ItemAwareElement element : dataInputAssociation.getSources()){
                        String dataId = element.getId();
                        DataObjectReference data = modelInstance.getModelElementById(dataId);
                        if (dataState.get(dataId) == null){
                            if (data.getName().equals(artifact)){
                                EndEventToDelete.add(event.getId());
                            }
                        }else {
                            if (!data.getName().equals(artifact)){
                                EndEventToDelete.add(event.getId());
                            }
                        }
                    }
                }
            }
        }

        for (IntermediateThrowEvent event : modelInstance.getModelElementsByType(IntermediateThrowEvent.class)){
            System.out.println(event.getId());
            if(event.getDataInputAssociations().isEmpty()){
                MidEventToDelete.add(event.getId());
                System.out.println("need delete : " + event.getId());
            }else {
                for (DataInputAssociation dataInputAssociation : event.getDataInputAssociations()){
                    for (ItemAwareElement element : dataInputAssociation.getSources()){
                        String dataId = element.getId();
                        DataObjectReference data = modelInstance.getModelElementById(dataId);
                        if (dataState.get(dataId) == null){
                            if (data.getName().equals(artifact)){
                                MidEventToDelete.add(event.getId());
                            }
                        }else {
                            if (!data.getName().equals(artifact)){
                                MidEventToDelete.add(event.getId());
                            }
                        }
                    }
                }
            }
        }


        for (IntermediateCatchEvent event : modelInstance.getModelElementsByType(IntermediateCatchEvent.class)){
            System.out.println(event.getId());
            if(event.getDataOutputAssociations().isEmpty()) {
                MidEventToDelete.add(event.getId());
                System.out.println("need delete : " + event.getId());
            }else{
                for (DataOutputAssociation dataOutputAssociation: event.getDataOutputAssociations()){
                    String dataId = dataOutputAssociation.getTarget().getId();
                    DataObjectReference data = modelInstance.getModelElementById(dataId);

                    if (dataState.get(dataId) == null){
                        if (data.getName().equals(artifact)){
                            MidEventToDelete.add(event.getId());
                        }
                    }else {
                        if (!data.getName().equals(artifact)){
                            MidEventToDelete.add(event.getId());
                        }
                    }
                }
            }
        }


        System.out.println(StartEventToDelete.toString());
        System.out.println(EndEventToDelete.toString());
        System.out.println(MidEventToDelete.toString());
        System.out.println("************* start delete event ***********");
        HashMap<String,String> element2graph = BPMNElement2Graph.map(modelInstance);
        System.out.println(element2graph.toString());
//        System.out.println(modelInstance.getModelElementsByType(SequenceFlow.class).toString());

        System.out.println("***********delete mid event********");
        for (String id : MidEventToDelete){
            System.out.println(id);
            Event event = modelInstance.getModelElementById(id);
            if (event.getOutgoing().size() > 1 || event.getIncoming().size() > 1){
                System.out.println("flow size greater than 1, please add gateway first");
                return;
            }

            SequenceFlow sourceFlow = event.getIncoming().iterator().next();
            SequenceFlow targetFlow = event.getOutgoing().iterator().next();
//            if (sourceFlow.getSource() == null || targetFlow.getTarget() == null){
////                System.out.println("i am here!!!!!!!!!!!!");
//                sourceFlow.getParentElement().removeChildElement(sourceFlow);
//                targetFlow.getParentElement().removeChildElement(targetFlow);
//                event.getParentElement().removeChildElement(event);
//            }
            //add new flow for deleting the event
            SequenceFlow newFlow = modelInstance.newInstance(SequenceFlow.class);
            String newFlowID;
            do {
                newFlowID = Generate7ID.generate();
            }while ( modelInstance.getModelElementById("Flow_" + newFlowID) != null);
            newFlow.setId("Flow_" + newFlowID);
            System.out.println("new flow id: " + newFlow.getId());

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


            modelInstance.getModelElementsByType(Process.class).iterator().next().addChildElement(newFlow);

            //update incoming and outgoing
//            newFlow.getTarget().getIncoming().add(newFlow);
//            newFlow.getSource().getOutgoing().add(newFlow);
            AddIncomingOrOutcoming.AddIncomingToElement(modelInstance,newFlow.getTarget().getId(),newFlow);
            AddIncomingOrOutcoming.AddOutgoingToElement(modelInstance,newFlow.getSource().getId(),newFlow);

            //add graph
            BpmnPlane plane = modelInstance.getModelElementsByType(BpmnPlane.class).iterator().next();
            BpmnEdge edge = DeleteTask.createEdgeForNewSequenceFlow(modelInstance, newFlow, sourceFlow.getSource().getId(), targetFlow.getTarget().getId(),element2graph);
            plane.addChildElement(edge);

            //delete flows and event
            System.out.println("delete event graph: " + id + "_di");
            event.getDiagramElement().getParentElement().removeChildElement(event.getDiagramElement());
            System.out.println("delete event: " + id);
            event.getParentElement().removeChildElement(event);

//            modelInstance.getModelElementById(element2graph.get(id)).getParentElement().removeChildElement(modelInstance.getModelElementById(element2graph.get(id)));
            System.out.println("delete flow: " + sourceFlow.getId());
            System.out.println("delete flow: " + targetFlow.getId());
            String src = sourceFlow.getId();
            String tgt = targetFlow.getId();
            sourceFlow.getDiagramElement().getParentElement().removeChildElement(sourceFlow.getDiagramElement());
            targetFlow.getDiagramElement().getParentElement().removeChildElement(targetFlow.getDiagramElement());
            sourceFlow.getParentElement().removeChildElement(sourceFlow);
            targetFlow.getParentElement().removeChildElement(targetFlow);

            System.out.println("new flow target: " + newFlow.getTarget());

//            System.out.println(element2graph.get(src));
//            System.out.println(element2graph.get(tgt));
//            modelInstance.getModelElementById(sourceFlow.getId()+"_di").getParentElement().removeChildElement(modelInstance.getModelElementById(sourceFlow.getId()+"_di"));
//            modelInstance.getModelElementById(targetFlow.getId()+"_di").getParentElement().removeChildElement(modelInstance.getModelElementById(targetFlow.getId()+"_di"));

        }

        System.out.println("******delete start event******");
        for (String id : StartEventToDelete){
            System.out.println(id);
            Event event = modelInstance.getModelElementById(id);
            for (SequenceFlow sequenceFlow : event.getOutgoing()){
//                sequenceFlow.getDiagramElement().getParentElement().removeChildElement(sequenceFlow.getDiagramElement());
                sequenceFlow.getParentElement().removeChildElement(sequenceFlow);
            }
//            event.getDiagramElement().getParentElement().removeChildElement(event.getDiagramElement());
            event.getParentElement().removeChildElement(event);
        }

        System.out.println("*********delete end event*******");
        for (String id : EndEventToDelete){
            System.out.println(id);
            Event event = modelInstance.getModelElementById(id);
            for (SequenceFlow sequenceFlow : event.getIncoming()){
//                sequenceFlow.getDiagramElement().getParentElement().removeChildElement(sequenceFlow.getDiagramElement());
                System.out.println("delete old flow: " + sequenceFlow.getId());
                sequenceFlow.getParentElement().removeChildElement(sequenceFlow);
            }
//            event.getDiagramElement().getParentElement().removeChildElement(event.getDiagramElement());
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
