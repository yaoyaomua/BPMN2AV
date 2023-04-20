package Step3_Delete_Element;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnEdge;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnPlane;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DeleteEvent {

    public DeleteEvent() {
    }

    public static void delete(BpmnModelInstance modelInstance){
        List<String> StartEventToDelete = new ArrayList<>();
        List<String> EndEventToDelete = new ArrayList<>();
        List<String> MidEventToDelete = new ArrayList<>();

        for (StartEvent event : modelInstance.getModelElementsByType(StartEvent.class)){
            System.out.println(event.getId());
            if (event.getDataOutputAssociations().isEmpty()){
                StartEventToDelete.add(event.getId());
                System.out.println("need delete : " + event.getId());
            }
        }
        for (EndEvent event : modelInstance.getModelElementsByType(EndEvent.class)){
            System.out.println(event.getId());
            if (event.getDataInputAssociations().isEmpty()){
                EndEventToDelete.add(event.getId());
                System.out.println("need delete : " + event.getId());
            }
        }
        for (IntermediateThrowEvent event : modelInstance.getModelElementsByType(IntermediateThrowEvent.class)){
            System.out.println(event.getId());
            if(event.getDataInputAssociations().isEmpty()){
                MidEventToDelete.add(event.getId());
                System.out.println("need delete : " + event.getId());
            }
        }

        for (IntermediateCatchEvent event : modelInstance.getModelElementsByType(IntermediateCatchEvent.class)){
            System.out.println(event.getId());
            if(event.getDataOutputAssociations().isEmpty()) {
                MidEventToDelete.add(event.getId());
                System.out.println("need delete : " + event.getId());
            }
        }
        System.out.println(StartEventToDelete.toString());
        System.out.println(EndEventToDelete.toString());
        System.out.println(MidEventToDelete.toString());
        System.out.println("************* start delete event ***********");
        HashMap<String,String> element2graph = BPMNElement2Graph.map(modelInstance);
        System.out.println(element2graph.toString());

        for (String id : StartEventToDelete){
            modelInstance.getModelElementById(id).getParentElement().removeChildElement(modelInstance.getModelElementById(id));
            modelInstance.getModelElementById(element2graph.get(id)).getParentElement().removeChildElement(modelInstance.getModelElementById(element2graph.get(id)));
        }

        for (String id : EndEventToDelete){
            modelInstance.getModelElementById(id).getParentElement().removeChildElement(modelInstance.getModelElementById(id));
            modelInstance.getModelElementById(element2graph.get(id)).getParentElement().removeChildElement(modelInstance.getModelElementById(element2graph.get(id)));
        }

        for (String id : MidEventToDelete){
            Event event = modelInstance.getModelElementById(id);
            SequenceFlow sourceFlow = event.getIncoming().iterator().next();
            SequenceFlow targetFlow = event.getOutgoing().iterator().next();
            //add new flow for deleting the event
            SequenceFlow newFlow = modelInstance.newInstance(SequenceFlow.class);
            String newFlowID;
            do {
                newFlowID = Generate7ID.generate();
            }while ( modelInstance.getModelElementById("Flow_" + newFlowID) != null);
            newFlow.setId("Flow_" + newFlowID);

            newFlow.setSource(sourceFlow.getSource());
            newFlow.setTarget(targetFlow.getTarget());

            modelInstance.getModelElementsByType(Process.class).iterator().next().addChildElement(newFlow);

            //update incoming
            newFlow.getTarget().getIncoming().add(newFlow);
            newFlow.getSource().getOutgoing().add(newFlow);

            //add graph
            BpmnPlane plane = modelInstance.getModelElementsByType(BpmnPlane.class).iterator().next();
            BpmnEdge edge = DeleteElement.createEdgeForNewSequenceFlow(modelInstance, newFlow, sourceFlow.getSource().getId(), targetFlow.getTarget().getId(),element2graph);
            plane.addChildElement(edge);

            //delete flows and event
            System.out.println("delete event: " + id);
            modelInstance.getModelElementById(id).getParentElement().removeChildElement(modelInstance.getModelElementById(id));
            System.out.println("delete event graph: " + id + "_di");
            modelInstance.getModelElementById(element2graph.get(id)).getParentElement().removeChildElement(modelInstance.getModelElementById(element2graph.get(id)));
            System.out.println("delete flow: " + sourceFlow.getId());
            System.out.println("delete flow: " + targetFlow.getId());
            String src = sourceFlow.getId();
            String tgt = targetFlow.getId();
            sourceFlow.getParentElement().removeChildElement(sourceFlow);
            targetFlow.getParentElement().removeChildElement(targetFlow);
//            System.out.println(element2graph.get(src));
//            System.out.println(element2graph.get(tgt));
//            modelInstance.getModelElementById(sourceFlow.getId()+"_di").getParentElement().removeChildElement(modelInstance.getModelElementById(sourceFlow.getId()+"_di"));
//            modelInstance.getModelElementById(targetFlow.getId()+"_di").getParentElement().removeChildElement(modelInstance.getModelElementById(targetFlow.getId()+"_di"));
            modelInstance.getModelElementById(element2graph.get(src)).getParentElement().removeChildElement(modelInstance.getModelElementById(element2graph.get(src)));
            modelInstance.getModelElementById(element2graph.get(tgt)).getParentElement().removeChildElement(modelInstance.getModelElementById(element2graph.get(tgt)));

        }

    }
}
