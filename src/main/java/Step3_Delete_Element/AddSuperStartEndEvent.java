package Step3_Delete_Element;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnEdge;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnPlane;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnShape;
import org.camunda.bpm.model.bpmn.instance.dc.Bounds;
import org.camunda.bpm.model.bpmn.instance.di.Waypoint;

import java.util.ArrayList;
import java.util.List;

public class AddSuperStartEndEvent {


    public AddSuperStartEndEvent() {
    }

    public static void addStart(BpmnModelInstance modelInstance){
        List<String> NoInputEvent = new ArrayList<>();
        List<String> NoInputActivity = new ArrayList<>();
        List<String> NoInputGateway = new ArrayList<>();
        List<String> NoInputSub = new ArrayList<>();
        List<String> startEvent = new ArrayList<>();
        BpmnPlane plane = modelInstance.getModelElementsByType(BpmnPlane.class).iterator().next();
        Process process = modelInstance.getModelElementsByType(Process.class).iterator().next();

        for (Event event : modelInstance.getModelElementsByType(Event.class)){
            if (event.getParentElement() instanceof SubProcess) continue;
            if (event instanceof BoundaryEvent) continue;
            if (event.getIncoming() == null || event.getIncoming().isEmpty()){
                if (event instanceof StartEvent){
                   startEvent.add(event.getId());
                }else {
                    NoInputEvent.add(event.getId());
                }
            }
        }
        for (Task task : modelInstance.getModelElementsByType(Task.class)){
            if (task.getParentElement() instanceof SubProcess) continue;
            if (task.getIncoming() == null || task.getIncoming().isEmpty()){
                NoInputActivity.add(task.getId());
            }
        }
        for (Gateway gateway : modelInstance.getModelElementsByType(Gateway.class)){
            if (gateway.getParentElement() instanceof  SubProcess) continue;
            if (gateway.getIncoming() == null || gateway.getIncoming().isEmpty()){
                NoInputGateway.add(gateway.getId());
            }
        }
        for (SubProcess subProcess : modelInstance.getModelElementsByType(SubProcess.class)){
            if (subProcess.getParentElement() instanceof  SubProcess) continue;
            if (subProcess.getIncoming() == null || subProcess.getIncoming().isEmpty()){
                NoInputSub.add(subProcess.getId());
            }
        }

        System.out.println(NoInputActivity.toString());
        System.out.println(NoInputEvent.toString());
        System.out.println(NoInputGateway.toString());
        System.out.println(NoInputSub.toString());
        System.out.println(startEvent.toString());


        if (NoInputEvent.size() == 0 && NoInputActivity.size() == 0 && NoInputGateway.size() == 0 && NoInputSub.size() == 0 && startEvent.size() == 1){
            return;
        }
        //creat new start event and set the attribute for it
        StartEvent processStart = modelInstance.newInstance(StartEvent.class);
        processStart.setId(GenerateID.getID("StartEvent_",modelInstance));
        processStart.setName("ProcessStart");
        process.addChildElement(processStart);
        //creat shape for start event
        BpmnShape processStartShape = modelInstance.newInstance(BpmnShape.class);
        processStartShape.setBpmnElement(processStart);
        processStartShape.setId(processStart.getId()+"_di");
        Bounds processStartBounds = modelInstance.newInstance(Bounds.class);
        processStartShape.setBounds(processStartBounds);
        //set the location of the start event shape
        processStartBounds.setX(10);
        processStartBounds.setY(10);
        processStartBounds.setHeight(36);
        processStartBounds.setWidth(36);
        plane.addChildElement(processStartShape);

        //creat new and gateway
        ParallelGateway parallelGateway = modelInstance.newInstance(ParallelGateway.class);
        parallelGateway.setId(GenerateID.getID("StartGateway_",modelInstance));
        parallelGateway.setName("ConnectStartGateway");
        process.addChildElement(parallelGateway);

        //creat shape for gateway
        BpmnShape parallerGatewayShape = modelInstance.newInstance(BpmnShape.class);
        parallerGatewayShape.setBpmnElement(parallelGateway);
        parallerGatewayShape.setId(parallelGateway.getId()+"_di");
        Bounds parallerGatewayBounds = modelInstance.newInstance(Bounds.class);
        parallerGatewayShape.setBounds(parallerGatewayBounds);
        //set the and gateway location
        parallerGatewayBounds.setX(10);
        parallerGatewayBounds.setY(100);
        parallerGatewayBounds.setWidth(50);
        parallerGatewayBounds.setHeight(50);
        plane.addChildElement(parallerGatewayShape);


        //creat a flow between gateway and process start event
        SequenceFlow startFlow = modelInstance.newInstance(SequenceFlow.class);
        startFlow.setId(GenerateID.getID("Flow_",modelInstance));
        startFlow.setSource(modelInstance.getModelElementById(processStart.getId()));
        startFlow.setTarget(modelInstance.getModelElementById(parallelGateway.getId()));
        process.addChildElement(startFlow);
        //update incoming and outgoing
        processStart.getOutgoing().add(startFlow);
        parallelGateway.getIncoming().add(startFlow);
        //creat flow edge
        BpmnEdge start2gateway = modelInstance.newInstance(BpmnEdge.class);
        start2gateway.setBpmnElement(startFlow);
        start2gateway.setId(startFlow.getId()+"_di");
        Waypoint startWaypoint1 = modelInstance.newInstance(Waypoint.class);
        Waypoint startWaypoint2 = modelInstance.newInstance(Waypoint.class);
        startWaypoint1.setX(28);
        startWaypoint1.setY(40);
        startWaypoint2.setX(35);
        startWaypoint2.setY(100);
        start2gateway.getWaypoints().add(startWaypoint1);
        start2gateway.getWaypoints().add(startWaypoint2);
        plane.addChildElement(start2gateway);

        for (String eventID : NoInputEvent){
            Event ac = modelInstance.getModelElementById(eventID);
            //add flow between andgateway and event
            SequenceFlow acFlow = modelInstance.newInstance(SequenceFlow.class);
            acFlow.setId(GenerateID.getID("Flow_",modelInstance));
            acFlow.setSource(modelInstance.getModelElementById(parallelGateway.getId()));
            acFlow.setTarget(modelInstance.getModelElementById(ac.getId()));

            process.addChildElement(acFlow);
            //update incoming and outgoing
            parallelGateway.getOutgoing().add(acFlow);
            ac.getIncoming().add(acFlow);
            //creat flow edge
            BpmnEdge gateway2ac = modelInstance.newInstance(BpmnEdge.class);
            gateway2ac.setBpmnElement(acFlow);
            gateway2ac.setId(acFlow.getId()+"_di");
            Waypoint acWaypoint1 = modelInstance.newInstance(Waypoint.class);
            Waypoint acWaypoint2 = modelInstance.newInstance(Waypoint.class);
            Bounds acBounds = ac.getDiagramElement().getBounds();
            acWaypoint1.setX(35);
            acWaypoint1.setY(125);
            acWaypoint2.setX(acBounds.getX() + acBounds.getWidth()/2);
            acWaypoint2.setY(acBounds.getY() + acBounds.getHeight()/2);
            gateway2ac.getWaypoints().add(acWaypoint1);
            gateway2ac.getWaypoints().add(acWaypoint2);

            plane.addChildElement(gateway2ac);

            System.out.println(acFlow.getId());
            System.out.println(gateway2ac.getId());
        }

        for (String gatewayID : NoInputGateway){
            Gateway ac = modelInstance.getModelElementById(gatewayID);
            //add flow between andgateway and event
            SequenceFlow acFlow = modelInstance.newInstance(SequenceFlow.class);
            acFlow.setId(GenerateID.getID("Flow_",modelInstance));
            acFlow.setSource(modelInstance.getModelElementById(parallelGateway.getId()));
            acFlow.setTarget(modelInstance.getModelElementById(ac.getId()));
            process.addChildElement(acFlow);
            //update incoming and outgoing
            parallelGateway.getOutgoing().add(acFlow);
            ac.getIncoming().add(acFlow);
            //creat flow edge
            BpmnEdge gateway2ac = modelInstance.newInstance(BpmnEdge.class);
            gateway2ac.setBpmnElement(acFlow);
            gateway2ac.setId(acFlow.getId()+"_di");
            Waypoint acWaypoint1 = modelInstance.newInstance(Waypoint.class);
            Waypoint acWaypoint2 = modelInstance.newInstance(Waypoint.class);
            Bounds acBounds = ac.getDiagramElement().getBounds();
            acWaypoint1.setX(35);
            acWaypoint1.setY(125);
            acWaypoint2.setX(acBounds.getX() + acBounds.getWidth()/2);
            acWaypoint2.setY(acBounds.getY() + acBounds.getHeight()/2);
            gateway2ac.getWaypoints().add(acWaypoint1);
            gateway2ac.getWaypoints().add(acWaypoint2);
            plane.addChildElement(gateway2ac);
        }

        for (String taskID : NoInputActivity){
            Task ac = modelInstance.getModelElementById(taskID);
            //add flow between andgateway and event
            SequenceFlow acFlow = modelInstance.newInstance(SequenceFlow.class);
            acFlow.setId(GenerateID.getID("Flow_",modelInstance));
            acFlow.setSource(modelInstance.getModelElementById(parallelGateway.getId()));
            acFlow.setTarget(modelInstance.getModelElementById(ac.getId()));
            process.addChildElement(acFlow);
            //update incoming and outgoing
            parallelGateway.getOutgoing().add(acFlow);
            ac.getIncoming().add(acFlow);
            //creat flow edge
            BpmnEdge gateway2ac = modelInstance.newInstance(BpmnEdge.class);
            gateway2ac.setBpmnElement(acFlow);
            gateway2ac.setId(acFlow.getId()+"_di");
            Waypoint acWaypoint1 = modelInstance.newInstance(Waypoint.class);
            Waypoint acWaypoint2 = modelInstance.newInstance(Waypoint.class);
            Bounds acBounds = ac.getDiagramElement().getBounds();
            acWaypoint1.setX(35);
            acWaypoint1.setY(125);
            acWaypoint2.setX(acBounds.getX() + acBounds.getWidth()/2);
            acWaypoint2.setY(acBounds.getY() + acBounds.getHeight()/2);
            gateway2ac.getWaypoints().add(acWaypoint1);
            gateway2ac.getWaypoints().add(acWaypoint2);
            plane.addChildElement(gateway2ac);
        }
        for (String subID : NoInputSub){
            SubProcess ac = modelInstance.getModelElementById(subID);
            //add flow between andgateway and event
            SequenceFlow acFlow = modelInstance.newInstance(SequenceFlow.class);
            acFlow.setId(GenerateID.getID("Flow_",modelInstance));
            acFlow.setSource(modelInstance.getModelElementById(parallelGateway.getId()));
            acFlow.setTarget(modelInstance.getModelElementById(ac.getId()));
            process.addChildElement(acFlow);
            //update incoming and outgoing
            parallelGateway.getOutgoing().add(acFlow);
            ac.getIncoming().add(acFlow);
            //creat flow edge
            BpmnEdge gateway2ac = modelInstance.newInstance(BpmnEdge.class);
            gateway2ac.setBpmnElement(acFlow);
            gateway2ac.setId(acFlow.getId()+"_di");
            Waypoint acWaypoint1 = modelInstance.newInstance(Waypoint.class);
            Waypoint acWaypoint2 = modelInstance.newInstance(Waypoint.class);
            BpmnShape acShape = modelInstance.getModelElementById(ac.getDiagramElement().getId());
            Bounds acBounds = acShape.getBounds();
            acWaypoint1.setX(35);
            acWaypoint1.setY(125);
            acWaypoint2.setX(acBounds.getX() + acBounds.getWidth()/2);
            acWaypoint2.setY(acBounds.getY() + acBounds.getHeight()/2);
            gateway2ac.getWaypoints().add(acWaypoint1);
            gateway2ac.getWaypoints().add(acWaypoint2);
            plane.addChildElement(gateway2ac);
        }

        int count = 0;
        for (String startID : startEvent){
            StartEvent acpre = modelInstance.getModelElementById(startID);
            IntermediateCatchEvent ac = modelInstance.newInstance(IntermediateCatchEvent.class);
            if (acpre.getName() != null){
                ac.setName(acpre.getName()+"_changed");
            }else {
                ac.setName("StartEvent_"+count);
                count ++;
            }
            ac.setId(acpre.getId());
            for (EventDefinition eventDefinition : acpre.getEventDefinitions()){
                ac.getEventDefinitions().add(eventDefinition);
            }
            for (DataOutputAssociation dataOutputAssociation : acpre.getDataOutputAssociations()){
                ac.getDataOutputAssociations().add(dataOutputAssociation);
            }
            for (Property property : acpre.getProperties()){
                ac.getProperties().add(property);
            }
            acpre.replaceWithElement(ac);


            acpre.getDiagramElement().setBpmnElement(ac);

            //add flow between andgateway and event
            SequenceFlow acFlow = modelInstance.newInstance(SequenceFlow.class);
            acFlow.setId(GenerateID.getID("Flow_",modelInstance));
            acFlow.setSource(modelInstance.getModelElementById(parallelGateway.getId()));
            acFlow.setTarget(modelInstance.getModelElementById(ac.getId()));
            process.addChildElement(acFlow);
            //update incoming and outgoing
            parallelGateway.getOutgoing().add(acFlow);
            ac.getIncoming().add(acFlow);
            //creat flow edge
            BpmnEdge gateway2ac = modelInstance.newInstance(BpmnEdge.class);
            gateway2ac.setBpmnElement(acFlow);
            gateway2ac.setId(acFlow.getId()+"_di");
            Waypoint acWaypoint1 = modelInstance.newInstance(Waypoint.class);
            Waypoint acWaypoint2 = modelInstance.newInstance(Waypoint.class);
            Bounds acBounds = ac.getDiagramElement().getBounds();
            acWaypoint1.setX(35);
            acWaypoint1.setY(125);
            acWaypoint2.setX(acBounds.getX() + acBounds.getWidth()/2);
            acWaypoint2.setY(acBounds.getY() + acBounds.getHeight()/2);
            gateway2ac.getWaypoints().add(acWaypoint1);
            gateway2ac.getWaypoints().add(acWaypoint2);
            plane.addChildElement(gateway2ac);
        }
    }





    public static void addEnd(BpmnModelInstance modelInstance){
        List<String> NoOutputEvent = new ArrayList<>();
        List<String> NoOutputActivity = new ArrayList<>();
        List<String> NoOutputGateway = new ArrayList<>();
        List<String> NoOutputSub = new ArrayList<>();
        List<String> endEvent = new ArrayList<>();
        BpmnPlane plane = modelInstance.getModelElementsByType(BpmnPlane.class).iterator().next();
        Process process = modelInstance.getModelElementsByType(Process.class).iterator().next();

        for (Event event : modelInstance.getModelElementsByType(Event.class)){
            if (event.getParentElement() instanceof SubProcess) continue;
            if (event instanceof BoundaryEvent) continue;
            if (event.getOutgoing()== null || event.getOutgoing().isEmpty()){
                if (event instanceof EndEvent){
                    endEvent.add(event.getId());
                }else {
                    NoOutputEvent.add(event.getId());
                }
            }
        }
        for (Task task : modelInstance.getModelElementsByType(Task.class)){
            if (task.getParentElement() instanceof SubProcess) continue;
            if (task.getOutgoing() == null || task.getOutgoing().isEmpty()){
                NoOutputActivity.add(task.getId());
            }
        }
        for (Gateway gateway : modelInstance.getModelElementsByType(Gateway.class)){
            if (gateway.getParentElement() instanceof SubProcess) continue;
            if (gateway.getOutgoing() == null || gateway.getOutgoing().isEmpty()){
                NoOutputGateway.add(gateway.getId());
            }
        }
        for (SubProcess subProcess : modelInstance.getModelElementsByType(SubProcess.class)){
            if (subProcess.getParentElement() instanceof SubProcess) continue;
            if (subProcess.getOutgoing() == null || subProcess.getOutgoing().isEmpty()){
                NoOutputSub.add(subProcess.getId());
            }
        }

        System.out.println(NoOutputEvent.toString());
        System.out.println(NoOutputActivity.toString());
        System.out.println(NoOutputGateway.toString());
        System.out.println(endEvent.toString());


        if (NoOutputEvent.size() == 0 && NoOutputActivity.size() == 0 && NoOutputGateway.size() == 0 && NoOutputSub.size() == 0 && endEvent.size() == 1){
            return;
        }
        //creat new start event and set the attribute for it
        EndEvent processEnd = modelInstance.newInstance(EndEvent.class);
        processEnd.setId(GenerateID.getID("EndEvent_",modelInstance));
        processEnd.setName("ProcessEnd");
        process.addChildElement(processEnd);
        //creat shape for start event
        BpmnShape processEndShape = modelInstance.newInstance(BpmnShape.class);
        processEndShape.setBpmnElement(processEnd);
        processEndShape.setId(processEnd.getId()+"_di");

        Bounds processEndBounds = modelInstance.newInstance(Bounds.class);
        processEndShape.setBounds(processEndBounds);
        //set the location of the start event shape
        processEndBounds.setX(1000);
        processEndBounds.setY(10);
        processEndBounds.setHeight(36);
        processEndBounds.setWidth(36);
        plane.addChildElement(processEndShape);

        //creat new and gateway
        ParallelGateway parallelGateway = modelInstance.newInstance(ParallelGateway.class);
        parallelGateway.setId(GenerateID.getID("EndGateway_",modelInstance));
        parallelGateway.setName("ConnectEndGateway");
        process.addChildElement(parallelGateway);

        //creat shape for gateway
        BpmnShape parallerGatewayShape = modelInstance.newInstance(BpmnShape.class);
        parallerGatewayShape.setBpmnElement(parallelGateway);
        parallerGatewayShape.setId(parallelGateway.getId()+"_di");
        Bounds parallerGatewayBounds = modelInstance.newInstance(Bounds.class);
        parallerGatewayShape.setBounds(parallerGatewayBounds);
        //set the and gateway location
        parallerGatewayBounds.setX(1000);
        parallerGatewayBounds.setY(100);
        parallerGatewayBounds.setWidth(50);
        parallerGatewayBounds.setHeight(50);
        plane.addChildElement(parallerGatewayShape);


        //creat a flow between gateway and process start event
        SequenceFlow endFlow = modelInstance.newInstance(SequenceFlow.class);
        endFlow.setId(GenerateID.getID("Flow_",modelInstance));
        endFlow.setTarget(modelInstance.getModelElementById(processEnd.getId()));
        endFlow.setSource(modelInstance.getModelElementById(parallelGateway.getId()));
        process.addChildElement(endFlow);
        //update incoming and outgoing
        processEnd.getIncoming().add(endFlow);
        parallelGateway.getOutgoing().add(endFlow);
        //creat flow edge
        BpmnEdge end2gateway = modelInstance.newInstance(BpmnEdge.class);
        end2gateway.setBpmnElement(endFlow);
        end2gateway.setId(endFlow.getId()+"_di");
        Waypoint startWaypoint1 = modelInstance.newInstance(Waypoint.class);
        Waypoint startWaypoint2 = modelInstance.newInstance(Waypoint.class);
        startWaypoint1.setX(1025);
        startWaypoint1.setY(40);
        startWaypoint2.setX(1025);
        startWaypoint2.setY(100);
        end2gateway.getWaypoints().add(startWaypoint2);
        end2gateway.getWaypoints().add(startWaypoint1);
        plane.addChildElement(end2gateway);

        for (String eventID : NoOutputEvent){
            Event ac = modelInstance.getModelElementById(eventID);
            //add flow between andgateway and event
            SequenceFlow acFlow = modelInstance.newInstance(SequenceFlow.class);
            acFlow.setId(GenerateID.getID("Flow_",modelInstance));
            acFlow.setSource(modelInstance.getModelElementById(ac.getId()));
            acFlow.setTarget(modelInstance.getModelElementById(parallelGateway.getId()));

            process.addChildElement(acFlow);
            //update incoming and outgoing
            parallelGateway.getIncoming().add(acFlow);
            ac.getIncoming().add(acFlow);
            //creat flow edge
            BpmnEdge gateway2ac = modelInstance.newInstance(BpmnEdge.class);
            gateway2ac.setBpmnElement(acFlow);
            gateway2ac.setId(acFlow.getId()+"_di");
            Waypoint acWaypoint1 = modelInstance.newInstance(Waypoint.class);
            Waypoint acWaypoint2 = modelInstance.newInstance(Waypoint.class);
            Bounds acBounds = ac.getDiagramElement().getBounds();
            acWaypoint1.setX(1025);
            acWaypoint1.setY(125);
            acWaypoint2.setX(acBounds.getX() + acBounds.getHeight()/2);
            acWaypoint2.setY(acBounds.getY() + acBounds.getWidth()/2);
            gateway2ac.getWaypoints().add(acWaypoint2);
            gateway2ac.getWaypoints().add(acWaypoint1);

            plane.addChildElement(gateway2ac);

            System.out.println(acFlow.getId());
            System.out.println(gateway2ac.getId());
        }

        for (String gatewayID : NoOutputGateway){
            Gateway ac = modelInstance.getModelElementById(gatewayID);
            //add flow between andgateway and event
            SequenceFlow acFlow = modelInstance.newInstance(SequenceFlow.class);
            acFlow.setId(GenerateID.getID("Flow_",modelInstance));
            acFlow.setSource(modelInstance.getModelElementById(ac.getId()));
            acFlow.setTarget(modelInstance.getModelElementById(parallelGateway.getId()));
            process.addChildElement(acFlow);
            //update incoming and outgoing
            parallelGateway.getIncoming().add(acFlow);
            ac.getIncoming().add(acFlow);
            //creat flow edge
            BpmnEdge gateway2ac = modelInstance.newInstance(BpmnEdge.class);
            gateway2ac.setBpmnElement(acFlow);
            gateway2ac.setId(acFlow.getId()+"_di");
            Waypoint acWaypoint1 = modelInstance.newInstance(Waypoint.class);
            Waypoint acWaypoint2 = modelInstance.newInstance(Waypoint.class);
            Bounds acBounds = ac.getDiagramElement().getBounds();
            acWaypoint1.setX(1025);
            acWaypoint1.setY(125);
            acWaypoint2.setX(acBounds.getX() + acBounds.getHeight()/2);
            acWaypoint2.setY(acBounds.getY() + acBounds.getWidth()/2);
            gateway2ac.getWaypoints().add(acWaypoint2);
            gateway2ac.getWaypoints().add(acWaypoint1);
            plane.addChildElement(gateway2ac);
        }

        for (String taskID : NoOutputActivity){
            Task ac = modelInstance.getModelElementById(taskID);
            //add flow between andgateway and event
            SequenceFlow acFlow = modelInstance.newInstance(SequenceFlow.class);
            acFlow.setId(GenerateID.getID("Flow_",modelInstance));
            acFlow.setSource(modelInstance.getModelElementById(ac.getId()));
            acFlow.setTarget(modelInstance.getModelElementById(parallelGateway.getId()));
            process.addChildElement(acFlow);
            //update incoming and outgoing
            parallelGateway.getIncoming().add(acFlow);
            ac.getIncoming().add(acFlow);
            //creat flow edge
            BpmnEdge gateway2ac = modelInstance.newInstance(BpmnEdge.class);
            gateway2ac.setBpmnElement(acFlow);
            gateway2ac.setId(acFlow.getId()+"_di");
            Waypoint acWaypoint1 = modelInstance.newInstance(Waypoint.class);
            Waypoint acWaypoint2 = modelInstance.newInstance(Waypoint.class);
            Bounds acBounds = ac.getDiagramElement().getBounds();
            acWaypoint1.setX(1025);
            acWaypoint1.setY(125);
            acWaypoint2.setX(acBounds.getX() + acBounds.getHeight()/2);
            acWaypoint2.setY(acBounds.getY() + acBounds.getWidth()/2);
            gateway2ac.getWaypoints().add(acWaypoint2);
            gateway2ac.getWaypoints().add(acWaypoint1);
            plane.addChildElement(gateway2ac);
        }

        for (String subId: NoOutputSub){
            SubProcess ac = modelInstance.getModelElementById(subId);
            //add flow between andgateway and event
            SequenceFlow acFlow = modelInstance.newInstance(SequenceFlow.class);
            acFlow.setId(GenerateID.getID("Flow_",modelInstance));
            acFlow.setSource(modelInstance.getModelElementById(ac.getId()));
            acFlow.setTarget(modelInstance.getModelElementById(parallelGateway.getId()));
            process.addChildElement(acFlow);
            //update incoming and outgoing
            parallelGateway.getIncoming().add(acFlow);
            ac.getIncoming().add(acFlow);
            //creat flow edge
            BpmnEdge gateway2ac = modelInstance.newInstance(BpmnEdge.class);
            gateway2ac.setBpmnElement(acFlow);
            gateway2ac.setId(acFlow.getId()+"_di");
            Waypoint acWaypoint1 = modelInstance.newInstance(Waypoint.class);
            Waypoint acWaypoint2 = modelInstance.newInstance(Waypoint.class);
            BpmnShape acDiagram = modelInstance.getModelElementById(ac.getDiagramElement().getId());
            Bounds acBounds = acDiagram.getBounds();
            acWaypoint1.setX(1025);
            acWaypoint1.setY(125);
            acWaypoint2.setX(acBounds.getX() + acBounds.getHeight()/2);
            acWaypoint2.setY(acBounds.getY() + acBounds.getWidth()/2);
            gateway2ac.getWaypoints().add(acWaypoint2);
            gateway2ac.getWaypoints().add(acWaypoint1);
            plane.addChildElement(gateway2ac);
        }

        int count = 0;
        for (String endID : endEvent){
            EndEvent acpre = modelInstance.getModelElementById(endID);
            IntermediateThrowEvent ac = modelInstance.newInstance(IntermediateThrowEvent.class);
            if (acpre.getName() != null){
                ac.setName(acpre.getName()+"_change");
            }else {
                ac.setName("EndEvent_"+count);
                count ++;
            }
            ac.setId(acpre.getId());
            for (EventDefinition eventDefinition : acpre.getEventDefinitions()){
                ac.getEventDefinitions().add(eventDefinition);
            }
            for (DataInputAssociation dataInputAssociation: acpre.getDataInputAssociations()){
                ac.getDataInputAssociations().add(dataInputAssociation);
            }
            for (Property property : acpre.getProperties()){
                ac.getProperties().add(property);
            }
            acpre.replaceWithElement(ac);

            acpre.getDiagramElement().setBpmnElement(ac);

            //add flow between andgateway and event
            SequenceFlow acFlow = modelInstance.newInstance(SequenceFlow.class);
            acFlow.setId(GenerateID.getID("Flow_",modelInstance));
            acFlow.setSource(modelInstance.getModelElementById(ac.getId()));
            acFlow.setTarget(modelInstance.getModelElementById(parallelGateway.getId()));
            process.addChildElement(acFlow);
            //update incoming and outgoing
            parallelGateway.getIncoming().add(acFlow);
            ac.getIncoming().add(acFlow);
            //creat flow edge
            BpmnEdge gateway2ac = modelInstance.newInstance(BpmnEdge.class);
            gateway2ac.setBpmnElement(acFlow);
            gateway2ac.setId(acFlow.getId()+"_di");
            Waypoint acWaypoint1 = modelInstance.newInstance(Waypoint.class);
            Waypoint acWaypoint2 = modelInstance.newInstance(Waypoint.class);
            Bounds acBounds = ac.getDiagramElement().getBounds();
            acWaypoint1.setX(1025);
            acWaypoint1.setY(125);
            acWaypoint2.setX(acBounds.getX() + acBounds.getHeight()/2);
            acWaypoint2.setY(acBounds.getY() + acBounds.getWidth()/2);
            gateway2ac.getWaypoints().add(acWaypoint2);
            gateway2ac.getWaypoints().add(acWaypoint1);
            plane.addChildElement(gateway2ac);
        }
    }
}
