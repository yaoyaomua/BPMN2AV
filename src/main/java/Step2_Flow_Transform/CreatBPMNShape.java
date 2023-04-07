package Step2_Flow_Transform;


import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnShape;
import org.camunda.bpm.model.bpmn.instance.dc.Bounds;


public class CreatBPMNShape {
    public static BpmnShape createBPMNShape(BpmnModelInstance modelInstance, String elementId, Double x, Double y, Double width, Double height) {
        Bounds bounds = modelInstance.newInstance(Bounds.class);
        bounds.setWidth(width);
        bounds.setHeight(height);
        bounds.setX(x);
        bounds.setY(y);
        //BpmnLabel bpmnLabel = modelInstance.newInstance(BpmnLabel.class);

        //ModelElementInstance diagramElement = modelInstance.getModelElementsByType(BpmnDiagram.class).iterator().next();
        BpmnShape shape = modelInstance.getModelElementsByType(BpmnShape.class).iterator().next();
        shape.setId(elementId + "_di");
        shape.setBpmnElement(modelInstance.getModelElementById(elementId));
        shape.setBounds(bounds);
        //shape.setBpmnLabel(bpmnLabel);
        //diagramElement.addChildElement(shape);
        return shape;
    }
}
