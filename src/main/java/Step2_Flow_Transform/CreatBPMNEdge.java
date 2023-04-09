package Step2_Flow_Transform;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnEdge;
import org.camunda.bpm.model.bpmn.instance.di.Waypoint;
public class CreatBPMNEdge {


    public CreatBPMNEdge() {
    }

    public static BpmnEdge create(BpmnModelInstance modelInstance, SequenceFlow sequenceflow, Double x1, Double y1, Double x2, Double y2) {
        //ModelElementInstance diagramElement = modelInstance.getModelElementsByType(BpmnDiagram.class).iterator().next();
        BpmnEdge edge = modelInstance.getModelElementsByType(BpmnEdge.class).iterator().next();
        Waypoint waypoint1 = modelInstance.getModelElementsByType(Waypoint.class).iterator().next();
        waypoint1.setX(x1);
        waypoint1.setY(y1);
        Waypoint waypoint2 = modelInstance.getModelElementsByType(Waypoint.class).iterator().next();
        waypoint2.setX(x2);
        waypoint2.setY(y2);
        edge.setId(sequenceflow.getId() + "_di");
        edge.setBpmnElement(modelInstance.getModelElementById(sequenceflow.getId()));
        edge.setTargetElement(modelInstance.getModelElementById("myParallelGateway"));
        edge.setSourceElement(modelInstance.getModelElementById(sequenceflow.getSource().getId()));

        edge.getWaypoints().add(waypoint1);
        edge.getWaypoints().add(waypoint2);
        //diagramElement.addChildElement(edge);
        return edge;
    }

}
