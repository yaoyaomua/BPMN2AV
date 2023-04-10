package Step2_Flow_Transform;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.BaseElement;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnEdge;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnPlane;
import org.camunda.bpm.model.bpmn.instance.di.Waypoint;
public class CreateBPMNEdge {

    public static BpmnEdge create(BpmnModelInstance modelInstance, SequenceFlow sequenceflow, Double x1, Double y1, Double x2, Double y2) {
        //ModelElementInstance diagramElement = modelInstance.getModelElementsByType(BpmnDiagram.class).iterator().next();
        BaseElement sourceElement = modelInstance.getModelElementById(sequenceflow.getSource().getId());
        BaseElement targetElement = modelInstance.getModelElementById(sequenceflow.getTarget().getId());
        BpmnEdge edge = modelInstance.newInstance(BpmnEdge.class);
        Waypoint waypoint1 = modelInstance.newInstance(Waypoint.class);
        waypoint1.setX(x1);
        waypoint1.setY(y1);
        Waypoint waypoint2 = modelInstance.newInstance(Waypoint.class);
        waypoint2.setX(x2);
        waypoint2.setY(y2);
        edge.setId(sequenceflow.getId() + "_di");
        edge.setBpmnElement(modelInstance.getModelElementById(sequenceflow.getId()));
        edge.setSourceElement(sourceElement.getDiagramElement());
        edge.setTargetElement(targetElement.getDiagramElement());
        edge.getWaypoints().add(waypoint1);
        edge.getWaypoints().add(waypoint2);
        //diagramElement.addChildElement(edge);
        modelInstance.getModelElementsByType(BpmnPlane.class).iterator().next().addChildElement(edge);
        return edge;
    }

}
