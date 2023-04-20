package Step2_Flow_Transform;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;
import org.jdom.Element;

public class AddIncomingOrOutcoming {
    public static void AddIncomingToElement(BpmnModelInstance modelInstance,String elementId,SequenceFlow newOutgoingFlow)
    {
        String elementType=modelInstance.getModelElementById(elementId).getElementType().getTypeName();
        System.out.println("incoming:"+elementType);
        switch (elementType)
        {
            case "exclusiveGateway":
                Gateway sourceAGateway = modelInstance.getModelElementById(elementId);
                sourceAGateway.getIncoming().add(newOutgoingFlow);
                break;
            case "parallelGateway":
                Gateway sourceparallelGateway = modelInstance.getModelElementById(elementId);
                sourceparallelGateway.getIncoming().add(newOutgoingFlow);
                break;
            case "task":
                Task sourceTask = modelInstance.getModelElementById(elementId);
                sourceTask.getIncoming().add(newOutgoingFlow);
                break;
            case "startEvent":
                StartEvent sourcestartEvent = modelInstance.getModelElementById(elementId);
                sourcestartEvent.getIncoming().add(newOutgoingFlow);
                break;
            case "endEvent":
                EndEvent sourceendEvent = modelInstance.getModelElementById(elementId);
                sourceendEvent.getIncoming().add(newOutgoingFlow);
                break;
            default:
                System.out.println("Type error!");
        }    }
    public static void AddOutgoingToElement(BpmnModelInstance modelInstance, String elementId,SequenceFlow newIncmoingFlow)
    {
        String elementType=modelInstance.getModelElementById(elementId).getElementType().getTypeName();
        System.out.println("outgoing:"+elementType);
        switch (elementType)
        {
            case "inclusiveGateway":
                Gateway sourceInclusiveGateway = modelInstance.getModelElementById(elementId);
                sourceInclusiveGateway.getOutgoing().add(newIncmoingFlow);
                break;
            case "exclusiveGateway":
                Gateway sourceAGateway = modelInstance.getModelElementById(elementId);
                sourceAGateway.getOutgoing().add(newIncmoingFlow);
                break;
            case "parallelGateway":
                Gateway sourceparallelGateway = modelInstance.getModelElementById(elementId);
                sourceparallelGateway.getOutgoing().add(newIncmoingFlow);
                break;
            case "task":
                Task sourceTask = modelInstance.getModelElementById(elementId);
                sourceTask.getOutgoing().add(newIncmoingFlow);
                break;
            case "startEvent":
                StartEvent sourcestartEvent = modelInstance.getModelElementById(elementId);
                sourcestartEvent.getOutgoing().add(newIncmoingFlow);
                break;
            case "endEvent":
                EndEvent sourceendEvent = modelInstance.getModelElementById(elementId);
                sourceendEvent.getOutgoing().add(newIncmoingFlow);
                break;
            default:
                System.out.println("Type error!");
        }
    }
}
