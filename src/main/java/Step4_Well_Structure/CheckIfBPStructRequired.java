package Step4_Well_Structure;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.Gateway;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;

import java.util.Collection;

public class CheckIfBPStructRequired {
    public static boolean check(BpmnModelInstance modelInstance,BpmnModelInstance initialModelInstance) {

        Boolean isRequired = false;
        Boolean checkGatewayConnection = false;
        Boolean checkGatewayNum = false;

        Collection<SequenceFlow> sequenceFlows = modelInstance.getModelElementsByType(SequenceFlow.class);
        Collection<Gateway> gateways = modelInstance.getModelElementsByType(Gateway.class);
        Collection<Gateway> initialgateways = initialModelInstance.getModelElementsByType(Gateway.class);
        if (initialgateways.size() < gateways.size())
        {
            checkGatewayNum = true;
        }

        for (SequenceFlow sequenceFlow: sequenceFlows)
        {
            if ((sequenceFlow.getTarget() instanceof Gateway) && (sequenceFlow.getSource() instanceof Gateway))
            {
                checkGatewayConnection = true;
                break;
            }
        }

        if (checkGatewayNum && checkGatewayConnection)
        {
            isRequired = true;
        }
        System.out.println(isRequired);
        return  isRequired;
    }
}
