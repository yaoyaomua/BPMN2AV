package Step3_Delete_Element;

import de.hpi.bpt.process.epc.IProcessInterface;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnDiagram;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnEdge;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnShape;

import java.util.Collection;
import java.util.EmptyStackException;
import java.util.HashSet;
import java.util.stream.Collectors;

public class DeleteEmptySubprocess {
    public static void delete(BpmnModelInstance modelInstance, HashSet<String> addedEvent) {

        //get a collection for emptysubProcess
        Collection<SubProcess> emptySubProcesses = modelInstance.getModelElementsByType(SubProcess.class).stream().filter(subProcess -> subProcess.getFlowElements().isEmpty()).collect(Collectors.toList());

        for (SubProcess subProcess: modelInstance.getModelElementsByType(SubProcess.class)){
            if (emptySubProcesses.contains(subProcess)) continue;
            if (!subProcess.getChildElementsByType(Task.class).isEmpty()) continue;

            boolean irr = true;
            for (Event event : subProcess.getChildElementsByType(Event.class)){
                if (!addedEvent.contains(event.getId())){
                    irr = false;
                    break;
                }
                if (event instanceof StartEvent){
                    StartEvent startEvent = (StartEvent) event;
                    if (startEvent.getDataOutputAssociations().size() != 0){
                        irr = false;
                        break;
                    }
                }
                if (event instanceof EndEvent){
                    EndEvent endEvent = (EndEvent) event;
                    if (endEvent.getDataInputAssociations().size() != 0){
                        irr = false;
                        break;
                    }
                }
            }
            if (irr){
                emptySubProcesses.add(subProcess);
            }
        }

        System.out.println(emptySubProcesses.toString());



        //remove empty subprocess
        for (SubProcess emptySubProcess: emptySubProcesses){
            System.out.println(emptySubProcess.getName());
            SequenceFlow incomingFlow = emptySubProcess.getIncoming().iterator().next();
            SequenceFlow outgoingFlow = emptySubProcess.getOutgoing().iterator().next();
            //reconnect sequence flow
            incomingFlow.getSource().getOutgoing().remove(incomingFlow);
//            outgoingFlow.getTarget().getIncoming().remove(outgoingFlow);
            incomingFlow.getSource().getOutgoing().add(outgoingFlow);
//            outgoingFlow.getTarget().getIncoming().add(incomingFlow);

            outgoingFlow.setSource(incomingFlow.getSource());
            if (incomingFlow.getName() != null){
                outgoingFlow.setName(CombineSequenceFlowCondition.combine(incomingFlow,outgoingFlow));
            }
            incomingFlow.getParentElement().removeChildElement(incomingFlow);


            emptySubProcess.getParentElement().removeChildElement(emptySubProcess);
        }

        //remove all the diagrams of the sub process
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

        for (BpmnDiagram diagram : modelInstance.getModelElementsByType(BpmnDiagram.class)){
            if (diagram.getId() == null){
                if (diagram.getBpmnPlane().getBpmnElement() == null){
                    diagram.getParentElement().removeChildElement(diagram);
                }
            }
        }
    }
}
