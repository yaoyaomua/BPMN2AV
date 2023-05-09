package Step4_Well_Structure;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.ParallelGateway;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.StartEvent;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnEdge;

public class DeleteParalleGatewaySequenceFlow {


    public DeleteParalleGatewaySequenceFlow() {
    }

    public static void delete(BpmnModelInstance modelInstance){
        for (SequenceFlow sequenceFlow : modelInstance.getModelElementsByType(SequenceFlow.class)){
            if (sequenceFlow.getSource() instanceof ParallelGateway && sequenceFlow.getTarget() instanceof ParallelGateway){
                ParallelGateway src = modelInstance.getModelElementById(sequenceFlow.getSource().getId());
                ParallelGateway tgt = modelInstance.getModelElementById(sequenceFlow.getTarget().getId());
                if (src.getIncoming().size() == 1 && src.getOutgoing().size() > 1 && tgt.getIncoming().size() > 1 && tgt.getOutgoing().size() == 1){
                    System.out.println(dfs(modelInstance,sequenceFlow,tgt,src));
                    if (dfs(modelInstance,sequenceFlow,tgt,src)){
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

    private static boolean dfs(BpmnModelInstance modelInstance, SequenceFlow sequenceFlow, FlowNode start, FlowNode target){
        System.out.println();
        System.out.println("dfs start again");

        for (SequenceFlow flow : start.getIncoming()){
            System.out.println("遍历flow：" + flow.getId());
            System.out.println(start.getId());
            if (flow.getId() == sequenceFlow.getId()) continue;
            start = flow.getSource();
            System.out.println("change start:" + start.getId());
            System.out.println("target is :" + target.getId());

            if (start instanceof StartEvent) continue;
            if (start.getId().equals(target.getId())) {
                System.out.println("already find !!!!!!!");

                return true;
            }else {
                return dfs(modelInstance,sequenceFlow,start,target);
            }
        }
        return false;
    }
}
