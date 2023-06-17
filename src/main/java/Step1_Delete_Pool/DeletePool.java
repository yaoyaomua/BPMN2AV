package Step1_Delete_Pool;

import java.util.ArrayList;
import java.util.Collection;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnEdge;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnShape;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;

public class DeletePool {

    public DeletePool() {
    }

    public static void deleteEmpty(BpmnModelInstance modelInstance){
        //Collaboration
        Collaboration collaboration = modelInstance.getModelElementsByType(Collaboration.class).iterator().next();
        if(collaboration != null) {

            Collection<Participant> participants = new ArrayList<>();
            //delete empty pool
            for (Participant participant : collaboration.getParticipants()){
                if (participant.getProcess() == null){
                    participants.add(participant);
                }else {
                    Process process = participant.getProcess();
                    if (process.getChildElementsByType(Task.class).isEmpty() && process.getChildElementsByType(Event.class).isEmpty() && process.getChildElementsByType(SubProcess.class).isEmpty()){
                        participants.add(participant);
                    }
                }

            }

            if (participants != null){
                for (MessageFlow messageFlow : collaboration.getMessageFlows()){
                    if (participants.contains(messageFlow.getTarget()) || participants.contains(messageFlow.getSource())){
                        collaboration.removeChildElement(messageFlow);
                    }
                }

                for (Participant participant : participants){
                    if (participant.getProcess() != null){
                        participant.getProcess().getParentElement().removeChildElement(participant.getProcess());
                    }
                    collaboration.removeChildElement(participant);
                }
            }


//            for (Association association : modelInstance.getModelElementsByType(Association.class)){
//                if (association.getSource() == null){
//                    association.getParentElement().removeChildElement(association);
//                }
//            }
//
//            for (BpmnShape bpmnShape : modelInstance.getModelElementsByType(BpmnShape.class)){
//                if (bpmnShape.getBpmnElement() == null){
//                    bpmnShape.getParentElement().removeChildElement(bpmnShape);
//                }
//            }
//
//            for (BpmnEdge bpmnEdge : modelInstance.getModelElementsByType(BpmnEdge.class)){
//                if (bpmnEdge.getBpmnElement() == null){
//                    bpmnEdge.getParentElement().removeChildElement(bpmnEdge);
//                }
//            }
        }


    }

    //Delete Pool
    public static Collection<MessageFlow> delete(BpmnModelInstance modelInstance){

        //Definitions
        Definitions definitions = modelInstance.getDefinitions();
        //Collaboration
        Collaboration collaboration = modelInstance.getModelElementsByType(Collaboration.class).iterator().next();
        if(collaboration != null) {
            //Get all messageflow
            Collection<MessageFlow> messageflows = collaboration.getMessageFlows();

            Collaboration collaborationToDelete = null;
            for (ModelElementInstance element : definitions.getRootElements()) {
                if (element instanceof Collaboration) {
                    Collaboration collaboration_element = (Collaboration) element;
                    collaborationToDelete = collaboration_element;
                }
            }

            // delete pool
            if (collaborationToDelete != null) {
                collaborationToDelete.getParentElement().removeChildElement(collaborationToDelete);
            }

//            for (Association association : modelInstance.getModelElementsByType(Association.class)){
//                if (association.getSource() == null){
//                    association.getParentElement().removeChildElement(association);
//                }
//            }
//
//            for (BpmnShape bpmnShape : modelInstance.getModelElementsByType(BpmnShape.class)){
//                if (bpmnShape.getBpmnElement() == null){
//                    bpmnShape.getParentElement().removeChildElement(bpmnShape);
//                }
//            }
//
//            for (BpmnEdge bpmnEdge : modelInstance.getModelElementsByType(BpmnEdge.class)){
//                if (bpmnEdge.getBpmnElement() == null){
//                    bpmnEdge.getParentElement().removeChildElement(bpmnEdge);
//                }
//            }
            return messageflows;
        }

        return  null;
    }
}
