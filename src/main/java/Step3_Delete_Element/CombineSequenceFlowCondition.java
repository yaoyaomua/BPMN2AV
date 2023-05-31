package Step3_Delete_Element;

import org.camunda.bpm.model.bpmn.instance.SequenceFlow;

public class CombineSequenceFlowCondition {


    public CombineSequenceFlowCondition() {
    }

    public static String combine(SequenceFlow flow1, SequenceFlow flow2){
        if (flow1.getName() == null && flow2.getName() == null){
            return null;
        }else if (flow1.getName() == null){
            return flow2.getName();
        } else if (flow2.getName() == null) {
            return flow1.getName();
        }else {
            return flow1.getName() + " and " + flow2.getName();
        }
    }
}
