package Step4_Well_Structure;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.ExclusiveGateway;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DeleteLoop {
    public DeleteLoop() {
    }

    public static boolean loop(BpmnModelInstance modelInstance){
//        System.out.println("loop delete start");
        HashMap<String, String> flows = new HashMap<>();
        HashMap<String, SequenceFlow> flowSources = new HashMap<>();
        List<String> toDelete = new ArrayList<>();
        for (SequenceFlow flow : modelInstance.getModelElementsByType(SequenceFlow.class)){
//            System.out.println(flow.getId());
//            System.out.println(flow.getSource().getId());
//            System.out.println(flow.getTarget().getId());
//            System.out.println();
            if (flow.getSource() instanceof ExclusiveGateway && flow.getTarget() instanceof ExclusiveGateway){
                if (flows.containsKey(flow.getTarget().getId())){
                    if (flows.get(flow.getTarget().getId()).equals(flow.getSource().getId())){
                        String src = flow.getSource().getId();
                        String tgt = flow.getTarget().getId();
                        ExclusiveGateway gateway1 = modelInstance.getModelElementById(src);
                        ExclusiveGateway gateway2 = modelInstance.getModelElementById(tgt);
                        if (gateway1.getIncoming().size() > 1){
                            toDelete.add(flowSources.get(flow.getTarget().getId()).getId());
                        }
                        if (gateway2.getIncoming().size() > 1){
                            toDelete.add(flow.getId());
                        }
                    }
                }else {
                    flowSources.put(flow.getSource().getId(),flow);
                    flows.put(flow.getSource().getId(),flow.getTarget().getId());
                }
            }
        }
//        for (SequenceFlow flow : modelInstance.getModelElementsByType(SequenceFlow.class)){
//            System.out.println(flow.getId());
//            System.out.println(flow.getSource().getId());
//            System.out.println(flow.getTarget().getId());
//            System.out.println();
//        }

        if (toDelete.size() == 0) return false;

        for (String flowid : toDelete){
            SequenceFlow flow = modelInstance.getModelElementById(flowid);
//            System.out.println("删除的flow：" + flow.getId());
//            System.out.println(flow.getSource().getId());
//            System.out.println(flow.getTarget().getId());
            flow.getDiagramElement().getParentElement().removeChildElement(flow.getDiagramElement());
            flow.getParentElement().removeChildElement(flow);
        }

//        System.out.println("结束删除了");
//        for (SequenceFlow flow : modelInstance.getModelElementsByType(SequenceFlow.class)){
////            if (flow.getId().equals("SequenceFlow_0z5q1vm")) continue;
//            System.out.println(flow.getId());
//            System.out.println(flow.getSource().getId());
//            System.out.println(flow.getTarget().getId());
//            System.out.println();
//        }
        return true;


    }
}
