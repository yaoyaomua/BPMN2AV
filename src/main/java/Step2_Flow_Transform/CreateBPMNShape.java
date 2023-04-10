package Step2_Flow_Transform;


import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnPlane;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnShape;
import org.camunda.bpm.model.bpmn.instance.dc.Bounds;


public class CreateBPMNShape {
    public static BpmnShape create(BpmnModelInstance modelInstance, String elementId, Double x, Double y, Double width, Double height) {
        Bounds bounds = modelInstance.newInstance(Bounds.class);
        bounds.setWidth(width);
        bounds.setHeight(height);
        bounds.setX(x);
        bounds.setY(y);
        BpmnShape shape = modelInstance.newInstance(BpmnShape.class);
        shape.setId(elementId + "_di");
        shape.setBpmnElement(modelInstance.getModelElementById(elementId));
        shape.setBounds(bounds);
        modelInstance.getModelElementsByType(BpmnPlane.class).iterator().next().addChildElement(shape);
        return shape;
    }
}
