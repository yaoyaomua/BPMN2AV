package Step3_Delete_Element;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnEdge;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnPlane;
import org.camunda.bpm.model.bpmn.instance.di.Waypoint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class Delete121Gateway {


    public Delete121Gateway() {
    }

    public static void delete(BpmnModelInstance modelInstance){
        List<String> toDeleteGateway = new ArrayList<>();
        BpmnPlane plane = modelInstance.getModelElementsByType(BpmnPlane.class).iterator().next();

        //all types of gateway can be get by Gateway's types.
        //so we do not need to get different gateway by the sub classes name.
        //get to delete gateway list
        for (Gateway gateway : modelInstance.getModelElementsByType(Gateway.class)){
            if ((gateway.getIncoming() == null || gateway.getIncoming().size() == 1) && (gateway.getOutgoing() == null || gateway.getOutgoing().size() == 1)){
                toDeleteGateway.add(gateway.getId());
            }
        }
        //delete gateway one by one
        for (String gatewayID : toDeleteGateway){
            Gateway gateway = modelInstance.getModelElementById(gatewayID);
            if (gateway.getOutgoing() != null && gateway.getIncoming() != null){
                //initial new flow
                SequenceFlow newFlow = modelInstance.newInstance(SequenceFlow.class);
                //set id for new flow
                String newFlowID;
                do {
                    newFlowID = Generate7ID.generate();
                }while ( modelInstance.getModelElementById("Flow_" + newFlowID) != null);
                newFlow.setId("Flow_" + newFlowID);
                //set flow's incoming activity
                //get gateways incoming flow
                SequenceFlow incomingFlow = gateway.getIncoming().iterator().next();
                //set source for new flow
                newFlow.setSource(incomingFlow.getSource());
                //get gateway outgoing flow
                SequenceFlow outgoingFlow = gateway.getOutgoing().iterator().next();
                //set target for new flow
                newFlow.setTarget(outgoingFlow.getTarget());

                //add sequence flow into model instance
                modelInstance.getModelElementsByType(Process.class).iterator().next().addChildElement(newFlow);

                String src = incomingFlow.getSource().getId();
                String tgt = outgoingFlow.getTarget().getId();
                System.out.println(src);
                System.out.println(tgt);

                //update incoming and outgoing
                //add outgoing
                if(modelInstance.getModelElementById(src) instanceof StartEvent){
                    StartEvent srcAc = modelInstance.getModelElementById(src);
                    srcAc.getOutgoing().add(newFlow);
                }else if (modelInstance.getModelElementById(src) instanceof Gateway){
                    Gateway srcAc = modelInstance.getModelElementById(src);
                    srcAc.getOutgoing().add(newFlow);
                }else {
                    Task srcAc = modelInstance.getModelElementById(src);
                    srcAc.getOutgoing().add(newFlow);
                }

                //add incoming
                if ( modelInstance.getModelElementById(tgt) instanceof EndEvent){
                    EndEvent tgtAc = modelInstance.getModelElementById(tgt);
                    tgtAc.getIncoming().add(newFlow);
                }else if (modelInstance.getModelElementById(tgt) instanceof  Gateway){
                    Gateway tgtAc = modelInstance.getModelElementById(tgt);
                    tgtAc.getIncoming().add(newFlow);
                }else {
                    Task tgtAc = modelInstance.getModelElementById(tgt);
                    tgtAc.getIncoming().add(newFlow);
                }



                //initial new flow graph
                BpmnEdge edge = modelInstance.newInstance(BpmnEdge.class);
                edge.setId(newFlowID + "_di");
                edge.setBpmnElement(modelInstance.getModelElementById(newFlow.getId()));

                //get source waypoint
                Waypoint waypoint1 = modelInstance.newInstance(Waypoint.class);
                Waypoint sourcePoint = modelInstance.getModelElementById(incomingFlow.getId()+"_di").getChildElementsByType(Waypoint.class).iterator().next();
                waypoint1.setX(sourcePoint.getX());
                waypoint1.setY(sourcePoint.getY());
                edge.getWaypoints().add(waypoint1);

                //get target waypoint
                Waypoint waypoint2 = modelInstance.newInstance(Waypoint.class);
                Collection<Waypoint> targetPoints = modelInstance.getModelElementById(outgoingFlow.getId()+"_di").getChildElementsByType(Waypoint.class);
                Iterator<Waypoint> iterator = targetPoints.iterator();
                Waypoint targetPoint = iterator.next();;
                if (iterator.hasNext()){
                    targetPoint = iterator.next();
                }
//                System.out.println(targetPoint.getX());
//                System.out.println(targetPoint.getY());
                waypoint2.setX(targetPoint.getX());
                waypoint2.setY(targetPoint.getY());
//                System.out.println(waypoint2.getX());
//                System.out.println(waypoint2.getY());
                edge.getWaypoints().add(waypoint2);
                //set id for edge
                edge.setId(newFlowID + "_di");
                edge.setBpmnElement(modelInstance.getModelElementById(newFlow.getId()));
                plane.addChildElement(edge);

                //delete flow and edge
                incomingFlow.getParentElement().removeChildElement(incomingFlow);
                outgoingFlow.getParentElement().removeChildElement(outgoingFlow);
//                modelInstance.getModelElementById(id).getParentElement().removeChildElement(modelInstance.getModelElementById(id));
                //delete flow in graph
                modelInstance.getModelElementById(incomingFlow.getId()+"_di").getParentElement().removeChildElement(modelInstance.getModelElementById(incomingFlow.getId()+"_di"));
                modelInstance.getModelElementById(outgoingFlow.getId()+"_di").getParentElement().removeChildElement(modelInstance.getModelElementById(outgoingFlow.getId()+"_di"));


            }
            gateway.getParentElement().removeChildElement(gateway);
        }
    }
}
