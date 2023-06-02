package Step4_Well_Structure;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.ParallelGateway;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.StartEvent;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnEdge;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;

public class DeleteParalleGatewaySequenceFlow {


    public DeleteParalleGatewaySequenceFlow() {
    }

    public static void delete(BpmnModelInstance modelInstance){
        for (SequenceFlow sequenceFlow : modelInstance.getModelElementsByType(SequenceFlow.class)){
            if (sequenceFlow.getSource() instanceof ParallelGateway && sequenceFlow.getTarget() instanceof ParallelGateway){
                ParallelGateway src = modelInstance.getModelElementById(sequenceFlow.getSource().getId());
                ParallelGateway tgt = modelInstance.getModelElementById(sequenceFlow.getTarget().getId());
                HashSet<FlowNode> used = new HashSet<>();
                if (src.getIncoming().size() == 1 && src.getOutgoing().size() > 1 && tgt.getIncoming().size() > 1 && tgt.getOutgoing().size() == 1){
                    System.out.println(dfs(modelInstance,sequenceFlow,tgt,src,used));
                    if (dfs(modelInstance,sequenceFlow,tgt,src,used)){
//                        sequenceFlow.getDiagramElement().removeChildElement(sequenceFlow.getDiagramElement());
                        sequenceFlow.getParentElement().removeChildElement(sequenceFlow);
                    }
                }
            }else {
                continue;
            }
        }

        for (BpmnEdge edge : modelInstance.getModelElementsByType(BpmnEdge.class)){
            if (edge.getBpmnElement() == null){
                edge.getParentElement().removeChildElement(edge);
            }
        }
    }

    private static boolean dfs(BpmnModelInstance modelInstance, SequenceFlow sequenceFlow, FlowNode start, FlowNode target, HashSet<FlowNode> used){


        Deque<FlowNode> queue = new ArrayDeque<>();
        queue.addLast(start);
        used.add(start);
        while (!queue.isEmpty()){
            FlowNode temp = queue.getFirst();
            for (SequenceFlow flow : temp.getIncoming()){
                if (flow.getId().equals(sequenceFlow.getId())) continue;
                FlowNode src = flow.getSource();
                if (src.getId().equals(target.getId())){
                    return true;
                }
                if (!used.contains(src)){
                    queue.addLast(flow.getSource());
                }
            }
            queue.removeFirst();
        }
        return false;
    }
}
