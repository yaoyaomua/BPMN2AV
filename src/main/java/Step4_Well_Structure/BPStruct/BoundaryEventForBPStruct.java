package Step4_Well_Structure.BPStruct;

import Step2_Flow_Transform.AddIncomingOrOutcoming;
import Step2_Flow_Transform.CreateBPMNEdge;
import Step2_Flow_Transform.CreateBPMNShape;
import Step2_Flow_Transform.GetBounds;
import Step3_Delete_Element.GenerateID;
import Step4_Well_Structure.Delete121Gateway;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnEdge;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnPlane;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnShape;
import org.camunda.bpm.model.bpmn.instance.dc.Bounds;
import org.camunda.bpm.model.bpmn.instance.di.Waypoint;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class BoundaryEventForBPStruct {
    public BoundaryEventForBPStruct() {
    }

    public static HashMap<BoundaryEvent, BpmnShape> store(BpmnModelInstance modelInstance){
        HashMap<BoundaryEvent, BpmnShape> shapes = new HashMap<>();
        for (BoundaryEvent event : modelInstance.getModelElementsByType(BoundaryEvent.class)){
//            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//            System.out.println("boundary event data object: " + event.getDataOutputAssociations().size());
            shapes.put(event, event.getDiagramElement());
        }
        return shapes;
    }

    public static HashMap<IntermediateCatchEvent,BoundaryEvent> pre(BpmnModelInstance modelInstance){
        HashMap<IntermediateCatchEvent,BoundaryEvent> events = new HashMap<>();
        BpmnPlane plane = modelInstance.getModelElementsByType(BpmnPlane.class).iterator().next();
        Process process = modelInstance.getModelElementsByType(Process.class).iterator().next();

        if (modelInstance.getModelElementsByType(BoundaryEvent.class).size() == 0) return events;

        for (BoundaryEvent boundaryEvent : modelInstance.getModelElementsByType(BoundaryEvent.class)){
            IntermediateCatchEvent event = modelInstance.newInstance(IntermediateCatchEvent.class);

            //creat event
            event.setId(GenerateID.getID("BoundaryEvent_",modelInstance));
            event.setName(boundaryEvent.getName());
            process.addChildElement(event);

            SequenceFlow oldFlow1 = boundaryEvent.getOutgoing().iterator().next();
            SequenceFlow oldFlow2 = boundaryEvent.getAttachedTo().getOutgoing().iterator().next();

            Gateway gateway;
            if (boundaryEvent.cancelActivity()){
                gateway = modelInstance.newInstance(ExclusiveGateway.class);
            }else {
                gateway = modelInstance.newInstance(ParallelGateway.class);
            }
            process.addChildElement(gateway);
            gateway.setId(GenerateID.getID("Gateway_", modelInstance));

            Bounds attachedBounds = GetBounds.get(modelInstance,boundaryEvent.getAttachedTo().getId());
            //creat event shape
            BpmnShape eventShape = CreateBPMNShape.create(modelInstance,event.getId(), attachedBounds.getX() + attachedBounds.getWidth() + 50, attachedBounds.getY() + attachedBounds.getHeight() + 20, 36.0, 36.0);
            plane.addChildElement(eventShape);
            //creat gateway shape
            BpmnShape gatewayShape = CreateBPMNShape.create(modelInstance,gateway.getId(),attachedBounds.getX()+attachedBounds.getWidth()+50, attachedBounds.getY() + 20, 50.0,50.0 );
            plane.addChildElement(gatewayShape);

            //creat four new flows
            SequenceFlow attached2gateway = modelInstance.newInstance(SequenceFlow.class);
            SequenceFlow gateway2normal = modelInstance.newInstance(SequenceFlow.class);
            SequenceFlow gateway2event = modelInstance.newInstance(SequenceFlow.class);
            SequenceFlow event2end = modelInstance.newInstance(SequenceFlow.class);

            //add flows in process
            process.addChildElement(attached2gateway);
            process.addChildElement(gateway2normal);
            process.addChildElement(gateway2event);
            process.addChildElement(event2end);

            attached2gateway.setId(GenerateID.getID("Flow_",modelInstance));
            gateway2normal.setId(GenerateID.getID("Flow_",modelInstance));
            gateway2event.setId(GenerateID.getID("Flow_",modelInstance));
            event2end.setId(GenerateID.getID("Flow_",modelInstance));

            //update gateway incoming outgoing
            gateway.getIncoming().add(attached2gateway);
            gateway.getOutgoing().add(gateway2event);
            gateway.getOutgoing().add(gateway2normal);

            //set source and target for the sequence flows
            attached2gateway.setSource(boundaryEvent.getAttachedTo());
            attached2gateway.setTarget(gateway);

            gateway2normal.setSource(gateway);
            gateway2normal.setTarget(boundaryEvent.getAttachedTo().getOutgoing().iterator().next().getTarget());

            gateway2event.setSource(gateway);
            gateway2event.setTarget(event);

            event2end.setSource(event);
            event2end.setTarget(oldFlow1.getTarget());

            //update incoming and out going
            AddIncomingOrOutcoming.AddOutgoingToElement(modelInstance, boundaryEvent.getAttachedTo().getId(), attached2gateway);
            AddIncomingOrOutcoming.AddIncomingToElement(modelInstance, event.getId(), gateway2event);
            AddIncomingOrOutcoming.AddOutgoingToElement(modelInstance, event.getId(), event2end);
            AddIncomingOrOutcoming.AddIncomingToElement(modelInstance, oldFlow1.getTarget().getId(), event2end);
            AddIncomingOrOutcoming.AddIncomingToElement(modelInstance, boundaryEvent.getAttachedTo().getOutgoing().iterator().next().getTarget().getId(), gateway2normal);

            //creat edges
            Bounds gatewayBounds = GetBounds.get(modelInstance,gateway.getId());
            Bounds normalBounds = GetBounds.get(modelInstance,boundaryEvent.getAttachedTo().getOutgoing().iterator().next().getTarget().getId());
            Bounds endBounds = GetBounds.get(modelInstance,oldFlow1.getTarget().getId());
            Bounds eventBounds = GetBounds.get(modelInstance,event.getId());
            BpmnEdge attached2gatewayEdge = CreateBPMNEdge.create(modelInstance,attached2gateway,
                    attachedBounds.getX() + attachedBounds.getWidth() / 2, attachedBounds.getY() + attachedBounds.getHeight() / 2,
                    gatewayBounds.getX() + gatewayBounds.getWidth() / 2, gatewayBounds.getY() + gatewayBounds.getHeight() / 2);
            BpmnEdge gateway2normalEdge = CreateBPMNEdge.create(modelInstance,gateway2normal,
                    gatewayBounds.getX() + gatewayBounds.getWidth() / 2, gatewayBounds.getY() + gatewayBounds.getHeight() / 2,
                    normalBounds.getX() + normalBounds.getWidth() / 2, normalBounds.getY() + normalBounds.getHeight() / 2);
            BpmnEdge gateway2eventEdge = CreateBPMNEdge.create(modelInstance,gateway2event,
                    gatewayBounds.getX() + gatewayBounds.getWidth() / 2, gatewayBounds.getY() + gatewayBounds.getHeight() / 2,
                    eventBounds.getX() + eventBounds.getWidth() / 2, eventBounds.getY() + eventBounds.getHeight() / 2);
            BpmnEdge event2endEdge = CreateBPMNEdge.create(modelInstance,event2end,
                    eventBounds.getX() + eventBounds.getWidth() / 2, eventBounds.getY() + eventBounds.getHeight() / 2,
                    endBounds.getX() + endBounds.getWidth() / 2, endBounds.getY() + endBounds.getHeight() / 2);

            plane.addChildElement(attached2gatewayEdge);
            plane.addChildElement(gateway2normalEdge);
            plane.addChildElement(gateway2eventEdge);
            plane.addChildElement(event2endEdge);

            //delete old flows and event
            oldFlow1.getParentElement().removeChildElement(oldFlow1);
            oldFlow2.getParentElement().removeChildElement(oldFlow2);
            events.put(event,boundaryEvent);

            //handle corresponding data objects'
            System.out.println("boundary event:" + boundaryEvent.getDataOutputAssociations().size());
            for (DataOutputAssociation dataOutputAssociation: boundaryEvent.getDataOutputAssociations()){
                System.out.println("record boundary event data object");
                event.getDataOutputAssociations().add(dataOutputAssociation);
            }

            boundaryEvent.getParentElement().removeChildElement(boundaryEvent);
        }

        for (BpmnShape bpmnShape : modelInstance.getModelElementsByType(BpmnShape.class)){
            if (bpmnShape.getBpmnElement() == null){
                bpmnShape.getParentElement().removeChildElement(bpmnShape);
            }
        }

        for (BpmnEdge bpmnEdge : modelInstance.getModelElementsByType(BpmnEdge.class)){
            if (bpmnEdge.getBpmnElement() == null){
                bpmnEdge.getParentElement().removeChildElement(bpmnEdge);
            }
        }

        return events;
    }

    public static void after(BpmnModelInstance modelInstance, HashMap<IntermediateCatchEvent,BoundaryEvent> events, HashMap<BoundaryEvent, BpmnShape> shapes){
        Process process = modelInstance.getModelElementsByType(Process.class).iterator().next();
        BpmnPlane plane = modelInstance.getModelElementsByType(BpmnPlane.class).iterator().next();

        for (IntermediateCatchEvent event : modelInstance.getModelElementsByType(IntermediateCatchEvent.class)){
            if (!events.containsKey(event)) continue;
            BoundaryEvent boundaryEvent = events.get(event);
            process.addChildElement(boundaryEvent);
            plane.addChildElement(shapes.get(boundaryEvent));
            shapes.get(boundaryEvent).setBpmnElement(boundaryEvent);
//            System.out.println(boundaryEvent.getId());
//            System.out.println(shapes.get(boundaryEvent).getId());

            SequenceFlow oldFlow1 = event.getIncoming().iterator().next();
            SequenceFlow oldFlow2 = event.getOutgoing().iterator().next();

            SequenceFlow newFlow = modelInstance.newInstance(SequenceFlow.class);
            newFlow.setId(GenerateID.getID("Flow_",modelInstance));
            newFlow.setSource(boundaryEvent);
            newFlow.setTarget(oldFlow2.getTarget());
            process.addChildElement(newFlow);

            //update flows
            AddIncomingOrOutcoming.AddOutgoingToElement(modelInstance,boundaryEvent.getId(),newFlow);
            AddIncomingOrOutcoming.AddIncomingToElement(modelInstance,oldFlow2.getTarget().getId(),newFlow);


            Bounds bounds1 = GetBounds.get(modelInstance, boundaryEvent.getId());
            Bounds bounds2 = GetBounds.get(modelInstance,oldFlow2.getTarget().getId());

            BpmnEdge edge = CreateBPMNEdge.create(modelInstance,newFlow,
                    bounds1.getX() + bounds1.getWidth() / 2, bounds1.getY() + bounds1.getHeight() / 2,
                    bounds2.getX() + bounds2.getWidth() / 2, bounds2.getY() + bounds2.getHeight() / 2);
            plane.addChildElement(edge);

            oldFlow1.getParentElement().removeChildElement(oldFlow1);
            oldFlow2.getParentElement().removeChildElement(oldFlow2);
            for (DataOutputAssociation dataOutputAssociation: event.getDataOutputAssociations()){
                System.out.println("add boundary event data object");
                boundaryEvent.getDataOutputAssociations().add(dataOutputAssociation);
            }
            event.getParentElement().removeChildElement(event);

        }

        for (BpmnShape bpmnShape : modelInstance.getModelElementsByType(BpmnShape.class)){
            if (bpmnShape.getBpmnElement() == null){
                bpmnShape.getParentElement().removeChildElement(bpmnShape);
            }
        }

        for (BpmnEdge bpmnEdge : modelInstance.getModelElementsByType(BpmnEdge.class)){
            if (bpmnEdge.getBpmnElement() == null){
                bpmnEdge.getParentElement().removeChildElement(bpmnEdge);
            }
        }

        Delete121Gateway.delete(modelInstance);
    }
}
