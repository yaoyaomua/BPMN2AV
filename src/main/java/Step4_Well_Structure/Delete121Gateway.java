package Step4_Well_Structure;

import Step2_Flow_Transform.AddIncomingOrOutcoming;
import Step2_Flow_Transform.CreateBPMNEdge;
import Step2_Flow_Transform.GetBounds;
import Step3_Delete_Element.Generate7ID;
import Step3_Delete_Element.GenerateID;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnEdge;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnPlane;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnShape;
import org.camunda.bpm.model.bpmn.instance.dc.Bounds;
import org.camunda.bpm.model.bpmn.instance.di.Waypoint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class Delete121Gateway {


    public Delete121Gateway() {
    }

    public static boolean delete(BpmnModelInstance modelInstance){
        List<String> toDeleteGateway = new ArrayList<>();
        BpmnPlane plane = modelInstance.getModelElementsByType(BpmnPlane.class).iterator().next();
        Process process = modelInstance.getModelElementsByType(Process.class).iterator().next();

        //all types of gateway can be get by Gateway's types.
        //so we do not need to get different gateway by the sub classes name.
        //get to delete gateway list
        for (Gateway gateway : modelInstance.getModelElementsByType(Gateway.class)){
            if (gateway.getIncoming().size() == 1 && gateway.getOutgoing().size() == 1){
                toDeleteGateway.add(gateway.getId());
            }
        }
        if (toDeleteGateway.size() == 0) return false;
        //delete gateway one by one
        for (String gatewayID : toDeleteGateway){
            Gateway gateway = modelInstance.getModelElementById(gatewayID);
            if (gateway.getOutgoing() != null && gateway.getIncoming() != null){
                //initial new flow
                SequenceFlow newFlow = modelInstance.newInstance(SequenceFlow.class);
                //set id for new flow
                newFlow.setId(GenerateID.getID("Flow_",modelInstance));
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
                incomingFlow.getParentElement().addChildElement(newFlow);
//                plane.addChildElement(newFlow);
                //update incoming and outgoing
                AddIncomingOrOutcoming.AddIncomingToElement(modelInstance,newFlow.getTarget().getId(),newFlow);
                AddIncomingOrOutcoming.AddOutgoingToElement(modelInstance,newFlow.getSource().getId(),newFlow);

                //find bounds
                Bounds bounds1 = GetBounds.get(modelInstance,newFlow.getSource().getId());
                Bounds bounds2 = GetBounds.get(modelInstance,newFlow.getTarget().getId());

                BpmnEdge edge = CreateBPMNEdge.create(modelInstance,newFlow,
                        bounds1.getX() + bounds1.getWidth()/2, bounds1.getY() + bounds1.getHeight()/2,
                        bounds2.getX() + bounds2.getWidth()/2, bounds2.getY() + bounds2.getHeight()/2);

                plane.addChildElement(edge);

                //delete flow and edge
                incomingFlow.getParentElement().removeChildElement(incomingFlow);
                outgoingFlow.getParentElement().removeChildElement(outgoingFlow);


            }
            gateway.getParentElement().removeChildElement(gateway);
        }

        for (BpmnEdge edge: modelInstance.getModelElementsByType(BpmnEdge.class)){
            if (edge.getBpmnElement() == null) {
                edge.getParentElement().removeChildElement(edge);
            }
        }
        for (BpmnShape shape : modelInstance.getModelElementsByType(BpmnShape.class)){
            if (shape.getBpmnElement() == null){
                shape.getParentElement().removeChildElement(shape);
            }
        }
        return true;
    }
}
