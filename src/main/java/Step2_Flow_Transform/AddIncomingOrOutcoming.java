package Step2_Flow_Transform;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;
import org.jdom.Element;

public class AddIncomingOrOutcoming {
    public static void AddIncomingToElement(BpmnModelInstance modelInstance,String elementId,SequenceFlow newOutgoingFlow)
    {
        if (modelInstance.getModelElementById(elementId) instanceof Event){
            Event ac = modelInstance.getModelElementById(elementId);
            ac.getIncoming().add(newOutgoingFlow);
        }else if (modelInstance.getModelElementById(elementId) instanceof Gateway){
            Gateway ac = modelInstance.getModelElementById(elementId);
            ac.getIncoming().add(newOutgoingFlow);
        }else if (modelInstance.getModelElementById(elementId) instanceof Task){
            Task ac = modelInstance.getModelElementById(elementId);
            ac.getIncoming().add(newOutgoingFlow);
        }else if (modelInstance.getModelElementById(elementId) instanceof SubProcess){
            SubProcess ac = modelInstance.getModelElementById(elementId);
            ac.getIncoming().add(newOutgoingFlow);
        }else if (modelInstance.getModelElementById(elementId) instanceof BoundaryEvent){
            BoundaryEvent ac = modelInstance.getModelElementById(elementId);
            ac.getIncoming().add(newOutgoingFlow);
        }else {
            System.out.println(modelInstance.getModelElementById(elementId).getElementType());
            System.out.println("Warning : can not add incoming!!!!!!");
        }
//        String elementType=modelInstance.getModelElementById(elementId).getElementType().getTypeName();
//        System.out.println("incoming:"+elementType);
//        switch (elementType)
//        {
//            case "inclusiveGateway":
//                Gateway sourceinclusiveGateway = modelInstance.getModelElementById(elementId);
//                sourceinclusiveGateway.getIncoming().add(newOutgoingFlow);
//                break;
//            case "exclusiveGateway":
//                Gateway sourceAGateway = modelInstance.getModelElementById(elementId);
//                sourceAGateway.getIncoming().add(newOutgoingFlow);
//                break;
//            case "parallelGateway":
//                Gateway sourceparallelGateway = modelInstance.getModelElementById(elementId);
//                sourceparallelGateway.getIncoming().add(newOutgoingFlow);
//                break;
//            case "task":
//                Task sourceTask = modelInstance.getModelElementById(elementId);
//                sourceTask.getIncoming().add(newOutgoingFlow);
//                break;
//            case "startEvent":
//                StartEvent sourcestartEvent = modelInstance.getModelElementById(elementId);
//                sourcestartEvent.getIncoming().add(newOutgoingFlow);
//                break;
//            case "endEvent":
//                EndEvent sourceendEvent = modelInstance.getModelElementById(elementId);
//                sourceendEvent.getIncoming().add(newOutgoingFlow);
//                break;
//            case "subProcess":
//                SubProcess sourcesubProcess = modelInstance.getModelElementById(elementId);
//                sourcesubProcess.getIncoming().add(newOutgoingFlow);
//                break;
//            default:
//                System.out.println("Type error!");
//        }
    }
    public static void AddOutgoingToElement(BpmnModelInstance modelInstance, String elementId,SequenceFlow newIncmoingFlow)
    {
        if (modelInstance.getModelElementById(elementId) instanceof StartEvent){
            StartEvent ac = modelInstance.getModelElementById(elementId);
            ac.getOutgoing().add(newIncmoingFlow);
        }else if (modelInstance.getModelElementById(elementId) instanceof EndEvent){
            EndEvent ac = modelInstance.getModelElementById(elementId);
            ac.getOutgoing().add(newIncmoingFlow);
        }else if (modelInstance.getModelElementById(elementId) instanceof Gateway){
            Gateway ac = modelInstance.getModelElementById(elementId);
            ac.getOutgoing().add(newIncmoingFlow);
        }else if (modelInstance.getModelElementById(elementId) instanceof IntermediateThrowEvent){
            IntermediateThrowEvent ac = modelInstance.getModelElementById(elementId);
            ac.getOutgoing().add(newIncmoingFlow);
        }else if (modelInstance.getModelElementById(elementId) instanceof IntermediateCatchEvent){
            IntermediateCatchEvent ac = modelInstance.getModelElementById(elementId);
            ac.getOutgoing().add(newIncmoingFlow);
        }else if (modelInstance.getModelElementById(elementId) instanceof Task){
            Task ac = modelInstance.getModelElementById(elementId);
            ac.getOutgoing().add(newIncmoingFlow);
        }else if (modelInstance.getModelElementById(elementId) instanceof SubProcess){
            SubProcess ac = modelInstance.getModelElementById(elementId);
            ac.getOutgoing().add(newIncmoingFlow);
        }else if (modelInstance.getModelElementById(elementId) instanceof BoundaryEvent){
            BoundaryEvent ac = modelInstance.getModelElementById(elementId);
            ac.getOutgoing().add(newIncmoingFlow);
        }else {
            System.out.println(modelInstance.getModelElementById(elementId).getElementType().getTypeName());
            System.out.println("Warning : can not add outgoing!!!!!!");
        }
//        String elementType=modelInstance.getModelElementById(elementId).getElementType().getTypeName();
//        System.out.println("outgoing:"+elementType);
//        switch (elementType)
//        {
//            case "inclusiveGateway":
//                Gateway sourceInclusiveGateway = modelInstance.getModelElementById(elementId);
//                sourceInclusiveGateway.getOutgoing().add(newIncmoingFlow);
//                break;
//            case "exclusiveGateway":
//                Gateway sourceAGateway = modelInstance.getModelElementById(elementId);
//                sourceAGateway.getOutgoing().add(newIncmoingFlow);
//                break;
//            case "parallelGateway":
//                Gateway sourceparallelGateway = modelInstance.getModelElementById(elementId);
//                sourceparallelGateway.getOutgoing().add(newIncmoingFlow);
//                break;
//            case "task":
//                Task sourceTask = modelInstance.getModelElementById(elementId);
//                sourceTask.getOutgoing().add(newIncmoingFlow);
//                break;
//            case "startEvent":
//                StartEvent sourcestartEvent = modelInstance.getModelElementById(elementId);
//                sourcestartEvent.getOutgoing().add(newIncmoingFlow);
//                break;
//            case "endEvent":
//                EndEvent sourceendEvent = modelInstance.getModelElementById(elementId);
//                sourceendEvent.getOutgoing().add(newIncmoingFlow);
//                break;
//            case "subProcess":
//                SubProcess sourcesubprocess = modelInstance.getModelElementById(elementId);
//                sourcesubprocess.getOutgoing().add(newIncmoingFlow);
//                break;
//            default:
//                System.out.println("Type error!");
//        }
    }
}
