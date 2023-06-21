package Step4_Well_Structure;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnShape;

public class FlowManage {
    public FlowManage() {
    }

    public static void manage(BpmnModelInstance modelInstance){
        while (DeleteRepeatFlow.delete(modelInstance) || Delete121Gateway.delete(modelInstance) || DeleteParalleGatewaySequenceFlow.delete(modelInstance) || DeleteLoop.loop(modelInstance)){

        }
    }
}
