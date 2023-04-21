package Step3_Delete_Element;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnEdge;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnShape;

import java.util.HashMap;

public class BPMNElement2Graph {


    public BPMNElement2Graph() {
    }

    public static HashMap<String,String> map(BpmnModelInstance modelInstance){
        HashMap<String,String> element2graph = new HashMap<>();
        for(BpmnShape shape : modelInstance.getModelElementsByType(BpmnShape.class)){
            if (shape.getBpmnElement() != null) {
                element2graph.put(shape.getBpmnElement().getId(), shape.getId());
            }
        }
        for (BpmnEdge edge : modelInstance.getModelElementsByType(BpmnEdge.class)){
            if (edge.getBpmnElement() != null) {
                element2graph.put(edge.getBpmnElement().getId(), edge.getId());
            }
        }
        return element2graph;
    }
}
