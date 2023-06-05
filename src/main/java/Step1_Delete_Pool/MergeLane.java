package Step1_Delete_Pool;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.LaneSet;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnShape;

public class MergeLane {


    public MergeLane() {
    }

    public static void merge(BpmnModelInstance modelInstance){
        for (Process process : modelInstance.getModelElementsByType(Process.class)){
            if (process.getLaneSets() == null) continue;
            for (LaneSet laneSet : process.getLaneSets()){
//                laneSet.getDiagramElement().getParentElement().removeChildElement(laneSet.getDiagramElement());
                process.removeChildElement(laneSet);
            }
            for (BpmnShape bpmnShape: modelInstance.getModelElementsByType(BpmnShape.class)){
                if (bpmnShape.getBpmnElement() == null){
                    bpmnShape.getParentElement().removeChildElement(bpmnShape);
                }
            }
        }
    }
}
