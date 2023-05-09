package Step2_Flow_Transform;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.Event;
import org.camunda.bpm.model.bpmn.instance.Gateway;
import org.camunda.bpm.model.bpmn.instance.SubProcess;
import org.camunda.bpm.model.bpmn.instance.Task;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnShape;
import org.camunda.bpm.model.bpmn.instance.dc.Bounds;

public class GetBounds {
    public GetBounds() {
    }

    public static Bounds get(BpmnModelInstance modelInstance, String elementId){
        if (modelInstance.getModelElementById(elementId) instanceof Event){
            Event ac = modelInstance.getModelElementById(elementId);
            return ac.getDiagramElement().getBounds();
        } else if (modelInstance.getModelElementById(elementId) instanceof Task) {
            Task ac = modelInstance.getModelElementById(elementId);
            return ac.getDiagramElement().getBounds();
        } else if (modelInstance.getModelElementById(elementId) instanceof Gateway) {
            Gateway ac = modelInstance.getModelElementById(elementId);
            return ac.getDiagramElement().getBounds();
        } else if (modelInstance.getModelElementById(elementId) instanceof SubProcess) {
            BpmnShape ac = modelInstance.getModelElementById(elementId+"_di");
            return ac.getBounds();
        } else {
            BpmnShape ac = modelInstance.getModelElementById(elementId+"_di");
            if(ac != null){
                return ac.getBounds();
            }else {
                System.out.println("this element has no bounds!");
                return null;
            }
        }
    }
}
