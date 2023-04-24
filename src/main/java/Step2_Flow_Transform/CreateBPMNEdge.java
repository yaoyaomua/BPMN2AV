package Step2_Flow_Transform;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnEdge;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnPlane;
import org.camunda.bpm.model.bpmn.instance.di.Waypoint;
public class CreateBPMNEdge {

    public static BpmnEdge create(BpmnModelInstance modelInstance, SequenceFlow sequenceflow, Double x1, Double y1, Double x2, Double y2) {
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
        modelInstance.getModelElementsByType(BpmnPlane.class).iterator().next().addChildElement(edge);
        return edge;
    }

    public static BpmnEdge create(BpmnModelInstance modelInstance, Association association, Double x1, Double y1, Double x2, Double y2) {
        BaseElement sourceElement = modelInstance.getModelElementById(association.getSource().getId());
        BaseElement targetElement = modelInstance.getModelElementById(association.getTarget().getId());
        BpmnEdge edge = modelInstance.newInstance(BpmnEdge.class);
        Waypoint waypoint1 = modelInstance.newInstance(Waypoint.class);
        waypoint1.setX(x1);
        waypoint1.setY(y1);
        Waypoint waypoint2 = modelInstance.newInstance(Waypoint.class);
        waypoint2.setX(x2);
        waypoint2.setY(y2);
        edge.setId(association.getId() + "_di");
        edge.setBpmnElement(modelInstance.getModelElementById(association.getId()));
        edge.setSourceElement(sourceElement.getDiagramElement());
        edge.setTargetElement(targetElement.getDiagramElement());
        edge.getWaypoints().add(waypoint1);
        edge.getWaypoints().add(waypoint2);
        modelInstance.getModelElementsByType(BpmnPlane.class).iterator().next().addChildElement(edge);
        return edge;
    }
    public static BpmnEdge create(BpmnModelInstance modelInstance, DataOutputAssociation association, Double x1, Double y1, Double x2, Double y2) {
        BpmnEdge edge = modelInstance.newInstance(BpmnEdge.class);
        Waypoint waypoint1 = modelInstance.newInstance(Waypoint.class);
        waypoint1.setX(x1);
        waypoint1.setY(y1);
        Waypoint waypoint2 = modelInstance.newInstance(Waypoint.class);
        waypoint2.setX(x2);
        waypoint2.setY(y2);
        edge.setId(association.getId() + "_di");
        edge.setBpmnElement(modelInstance.getModelElementById(association.getId()));
        edge.getWaypoints().add(waypoint1);
        edge.getWaypoints().add(waypoint2);
        modelInstance.getModelElementsByType(BpmnPlane.class).iterator().next().addChildElement(edge);
        return edge;
    }
    public static BpmnEdge create(BpmnModelInstance modelInstance, DataInputAssociation association, Double x1, Double y1, Double x2, Double y2) {
        BpmnEdge edge = modelInstance.newInstance(BpmnEdge.class);
        Waypoint waypoint1 = modelInstance.newInstance(Waypoint.class);
        waypoint1.setX(x1);
        waypoint1.setY(y1);
        Waypoint waypoint2 = modelInstance.newInstance(Waypoint.class);
        waypoint2.setX(x2);
        waypoint2.setY(y2);
        edge.setId(association.getId() + "_di");
        edge.setBpmnElement(modelInstance.getModelElementById(association.getId()));
        edge.getWaypoints().add(waypoint1);
        edge.getWaypoints().add(waypoint2);
        modelInstance.getModelElementsByType(BpmnPlane.class).iterator().next().addChildElement(edge);
        return edge;
    }

}
