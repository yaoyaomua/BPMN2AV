package Add_Data_Object;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.BaseElement;
import org.camunda.bpm.model.bpmn.instance.Gateway;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;

public class CheckCorrectness {
    public static boolean checkCorrectness(BaseElement sourceElement, BaseElement targetElement)
    {
        Boolean isCorrect = true;
        BaseElement nextElement = GetAfterElement.Get(sourceElement);
        if (!(nextElement instanceof Gateway))
        {
            if (nextElement.getId() != targetElement.getId())
            {
                isCorrect = false;
            }
        }
        else
        {
            for (SequenceFlow sequenceFlow: ((Gateway) nextElement).getOutgoing())
            {
                BaseElement elementAfterGateway = sequenceFlow.getTarget();
                if (!(elementAfterGateway instanceof Gateway))
                {
                    if (elementAfterGateway.getId() != targetElement.getId())
                    {
                        isCorrect = false;
                    }
                    else
                    {
                        isCorrect = true;
                        break;
                    }
                }
                else
                {
                    isCorrect = checkCorrectness(elementAfterGateway,targetElement);
                    if (isCorrect)
                    {
                        break;
                    }
                }
            }
        }

        return isCorrect;
    }
}
