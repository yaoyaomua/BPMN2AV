package Add_Data_Object;

import org.camunda.bpm.model.bpmn.instance.*;

public class GetAfterElement {
    public static BaseElement Get(BaseElement element) {
        BaseElement baseElement = null;
        if (element instanceof Task)
        {
            baseElement=((Task) element).getOutgoing().iterator().next().getTarget();
        }
        else if (element instanceof StartEvent) {
            baseElement=((StartEvent) element).getOutgoing().iterator().next().getTarget();
        }
        else if (element instanceof Event) {
            baseElement=((Event) element).getOutgoing().iterator().next().getTarget();
        }
        else if (element instanceof SubProcess) {
            baseElement=((SubProcess) element).getOutgoing().iterator().next().getTarget();
        }

        return baseElement;
    }
}
