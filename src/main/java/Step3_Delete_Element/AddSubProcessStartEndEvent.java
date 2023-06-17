package Step3_Delete_Element;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnEdge;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnPlane;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnShape;
import org.camunda.bpm.model.bpmn.instance.dc.Bounds;
import org.camunda.bpm.model.bpmn.instance.di.Waypoint;

import java.util.HashSet;

public class AddSubProcessStartEndEvent {


    public AddSubProcessStartEndEvent() {
    }

    public static HashSet<String> add(BpmnModelInstance modelInstance){
        HashSet<String> added = new HashSet<>();
        for (SubProcess subProcess : modelInstance.getModelElementsByType(SubProcess.class)){

            if (subProcess.getChildElementsByType(Gateway.class).size() == 0 && subProcess.getChildElementsByType(Task.class).size() == 0
            && subProcess.getChildElementsByType(Event.class).size() == 0
            && subProcess.getChildElementsByType(SubProcess.class).size() == 0){
                continue;
            }

            if (subProcess.getChildElementsByType(StartEvent.class).size() == 1 && subProcess.getChildElementsByType(EndEvent.class).size() == 1){
                added.add(subProcess.getChildElementsByType(StartEvent.class).iterator().next().getId());
                added.add(subProcess.getChildElementsByType(EndEvent.class).iterator().next().getId());
                continue;
            }

            HashSet<String> addHashSet = addForSub(modelInstance,subProcess);
            if (addHashSet != null) {
                for (String s : addHashSet) {
                    added.add(s);
                }

            }

        }
        return added;
    }

    public static HashSet<String> addForSub(BpmnModelInstance modelInstance, SubProcess subProcess){
        HashSet<String> added = new HashSet<>();
        BpmnPlane plane = modelInstance.getModelElementsByType(BpmnPlane.class).iterator().next();
        if (true){
            //creat new start event and set the attribute for it
            StartEvent processStart = modelInstance.newInstance(StartEvent.class);
            processStart.setId(GenerateID.getID("StartEvent_",modelInstance));
            processStart.setName("SubProcessStart:"+ subProcess.getName());
            subProcess.addChildElement(processStart);
            added.add(processStart.getId());
            //creat shape for start event
            BpmnShape processStartShape = modelInstance.newInstance(BpmnShape.class);
            processStartShape.setBpmnElement(processStart);
            processStartShape.setId(processStart.getId()+"_di");
            Bounds processStartBounds = modelInstance.newInstance(Bounds.class);
            processStartShape.setBounds(processStartBounds);
            //set the location of the start event shape
            BpmnShape subShape = modelInstance.getModelElementById(subProcess.getDiagramElement().getId());
            Bounds subBounds = subShape.getBounds();
            processStartBounds.setX(10+subBounds.getX());
            processStartBounds.setY(10+subBounds.getY());
            processStartBounds.setHeight(36);
            processStartBounds.setWidth(36);
            plane.addChildElement(processStartShape);

            //creat new flow between new start and no input element
            SequenceFlow startFlow = modelInstance.newInstance(SequenceFlow.class);
            startFlow.setId(GenerateID.getID("Flow_",modelInstance));
            startFlow.setSource(modelInstance.getModelElementById(processStart.getId()));

            subProcess.addChildElement(startFlow);
            //update incoming and outgoing
            processStart.getOutgoing().add(startFlow);


//            String NoInputElement = null;
            Bounds acBounds = null;

            for (StartEvent acpre : subProcess.getChildElementsByType(StartEvent.class)){
                if (acpre.getId().equals(processStart.getId())) continue;
                System.out.println("Start event id: " + acpre.getId());
                IntermediateCatchEvent ac = modelInstance.newInstance(IntermediateCatchEvent.class);
                if (acpre.getName() != null){
                    ac.setName(acpre.getName()+"_changed");
                }else {
                    ac.setName("StartEvent_sub");
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
                if (ac.getIncoming() == null || ac.getIncoming().isEmpty()){
                    System.out.println("******this element need add start!****");
                    startFlow.setTarget(ac);
                    ac.getIncoming().add(startFlow);
                    BpmnShape acShape = ac.getDiagramElement();
                    acBounds = acShape.getBounds();
                }
            }

            for (Event ac : subProcess.getChildElementsByType(Event.class)){
                System.out.println("event id: " + ac.getId());
                if (ac.getParentElement() != subProcess) continue;
                if (ac instanceof BoundaryEvent || ac instanceof StartEvent) continue;
                if (ac.getIncoming() == null || ac.getIncoming().isEmpty()){
                    System.out.println("******this element need add start!****");
                    startFlow.setTarget(ac);
                    ac.getIncoming().add(startFlow);
                    BpmnShape acShape = ac.getDiagramElement();
                    acBounds = acShape.getBounds();
                }
            }
            for (Task ac : subProcess.getChildElementsByType(Task.class)){
                System.out.println("task id: " + ac.getId());
                if (ac.getParentElement() != subProcess) continue;
                if (ac.getIncoming() == null || ac.getIncoming().isEmpty()){
                    System.out.println("this element need add start!");
                    startFlow.setTarget(ac);
                    ac.getIncoming().add(startFlow);
                    BpmnShape acShape = ac.getDiagramElement();
                    acBounds = acShape.getBounds();
                }
            }
            for (Gateway ac : subProcess.getChildElementsByType(Gateway.class)){
                System.out.println("gateway id: " + ac.getId());
                if (ac.getParentElement() != subProcess) continue;
                if (ac.getIncoming() == null || ac.getIncoming().isEmpty()){
                    System.out.println("this element need add start!");
                    startFlow.setTarget(ac);
                    ac.getIncoming().add(startFlow);
                    BpmnShape acShape = ac.getDiagramElement();
                    acBounds = acShape.getBounds();
                }
            }
            for (SubProcess ac : subProcess.getChildElementsByType(SubProcess.class)){
                System.out.println("subprocess id: " + ac.getId());
                addForSub(modelInstance,ac);
                if (ac.getParentElement() != subProcess) continue;
                if (ac.getIncoming() == null || ac.getIncoming().isEmpty()){
                    System.out.println("this element need add start!");
                    startFlow.setTarget(ac);
                    ac.getIncoming().add(startFlow);
                    BpmnShape acShape = modelInstance.getModelElementById(ac.getDiagramElement().getId());
                    acBounds = acShape.getBounds();
                }
            }


//            System.out.println(startFlow.getTarget().getId());
            if (acBounds == null){
                System.out.println("this bpmn model does not have element that has no incoming flow");
                return null;
            }

//            startFlow.setTarget(modelInstance.getModelElementById(NoInputElement);
//            parallelGateway.getIncoming().add(startFlow);

            //creat flow edge
            BpmnEdge start2gateway = modelInstance.newInstance(BpmnEdge.class);
            start2gateway.setBpmnElement(startFlow);
            start2gateway.setId(startFlow.getId()+"_di");
            Waypoint startWaypoint1 = modelInstance.newInstance(Waypoint.class);
            Waypoint startWaypoint2 = modelInstance.newInstance(Waypoint.class);
            startWaypoint1.setX(28+subBounds.getX());
            startWaypoint1.setY(40+subBounds.getY());
            startWaypoint2.setX(acBounds.getX() + acBounds.getWidth()/2);
            startWaypoint2.setY(acBounds.getY() + acBounds.getHeight()/2);
            start2gateway.getWaypoints().add(startWaypoint1);
            start2gateway.getWaypoints().add(startWaypoint2);
            plane.addChildElement(start2gateway);

        }

        System.out.println("**********add start end**************");


        if (true){
            //creat new flow between new start and no input element
            SequenceFlow endFlow = modelInstance.newInstance(SequenceFlow.class);
            endFlow.setId(GenerateID.getID("Flow_",modelInstance));

            subProcess.addChildElement(endFlow);
            //update incoming and outgoing



//            String NoInputElement = null;
            Bounds acBounds = null;

            for (EndEvent acpre : subProcess.getChildElementsByType(EndEvent.class)){
//                if (acpre.getId().equals(.getId())) continue;
                System.out.println("End event id: " + acpre.getId());
                IntermediateThrowEvent ac = modelInstance.newInstance(IntermediateThrowEvent.class);
                if (acpre.getName() != null){
                    ac.setName(acpre.getName()+"_changed");
                }else {
                    ac.setName("EndEvent_sub");
                }
                ac.setId(acpre.getId());
                for (EventDefinition eventDefinition : acpre.getEventDefinitions()) {
                    ac.getEventDefinitions().add(eventDefinition);
                }
                for (DataInputAssociation dataInputAssociation : acpre.getDataInputAssociations()) {
                    ac.getDataInputAssociations().add(dataInputAssociation);
                }
                for (Property property : acpre.getProperties()) {
                    ac.getProperties().add(property);
                }
                acpre.replaceWithElement(ac);
                if (ac.getOutgoing() == null || ac.getOutgoing().isEmpty()){
                    System.out.println("******this element need add end!****");
                    endFlow.setSource(ac);
                    ac.getOutgoing().add(endFlow);
                    BpmnShape acShape = ac.getDiagramElement();
                    acBounds = acShape.getBounds();
                }
            }

            for (Event ac : subProcess.getChildElementsByType(Event.class)){
                System.out.println("event id: " + ac.getId());
                if (ac.getParentElement() != subProcess) continue;
                if (ac instanceof BoundaryEvent) continue;
                if (ac.getOutgoing() == null || ac.getOutgoing().isEmpty()){
                    System.out.println("******this element need add end!****");
                    endFlow.setSource(ac);
                    ac.getOutgoing().add(endFlow);
                    BpmnShape acShape = ac.getDiagramElement();
                    acBounds = acShape.getBounds();
                }
            }
            for (Task ac : subProcess.getChildElementsByType(Task.class)){
                System.out.println("task id: " + ac.getId());
                if (ac.getParentElement() != subProcess) continue;
                if (ac.getOutgoing() == null || ac.getOutgoing().isEmpty()){
                    System.out.println("******this element need add end!****");
                    endFlow.setSource(ac);
                    ac.getOutgoing().add(endFlow);
                    BpmnShape acShape = ac.getDiagramElement();
                    acBounds = acShape.getBounds();
                }
            }
            for (Gateway ac : subProcess.getChildElementsByType(Gateway.class)){
                System.out.println("gateway id: " + ac.getId());
                if (ac.getParentElement() != subProcess) continue;
                if (ac.getOutgoing() == null || ac.getOutgoing().isEmpty()){
                    System.out.println("******this element need add end!****");
                    endFlow.setSource(ac);
                    ac.getOutgoing().add(endFlow);
                    BpmnShape acShape = ac.getDiagramElement();
                    acBounds = acShape.getBounds();
                }
            }
            for (SubProcess ac : subProcess.getChildElementsByType(SubProcess.class)){
                System.out.println("subprocess id: " + ac.getId());
                addForSub(modelInstance,ac);
                if (ac.getParentElement() != subProcess) continue;
                if (ac.getOutgoing() == null || ac.getOutgoing().isEmpty()){
                    System.out.println("this element need add end!");
                    endFlow.setSource(ac);
                    ac.getOutgoing().add(endFlow);
                    BpmnShape acShape = modelInstance.getModelElementById(ac.getDiagramElement().getId());
                    acBounds = acShape.getBounds();
                }
            }


//            System.out.println(endFlow.getTarget().getId());
            if (acBounds == null){
                System.out.println("this bpmn model does not have element that has no incoming flow");
                return null;
            }

            //creat new start event and set the attribute for it
            EndEvent processEnd = modelInstance.newInstance(EndEvent.class);
            processEnd.setId(GenerateID.getID("EndEvent_",modelInstance));
            processEnd.setName("SubProcessEnd:"+ subProcess.getName());
            subProcess.addChildElement(processEnd);

            added.add(processEnd.getId());
            //creat shape for start event
            BpmnShape processEndShape = modelInstance.newInstance(BpmnShape.class);
            processEndShape.setBpmnElement(processEnd);
            processEndShape.setId(processEnd.getId()+"_di");
            Bounds processEndBounds = modelInstance.newInstance(Bounds.class);
            processEndShape.setBounds(processEndBounds);
            //set the location of the start event shape
            BpmnShape subShape = modelInstance.getModelElementById(subProcess.getDiagramElement().getId());
            Bounds subBounds = subShape.getBounds();
            processEndBounds.setX(subBounds.getX() + subBounds.getWidth() - 46);
            processEndBounds.setY(subBounds.getY() + subBounds.getHeight() - 46);
            processEndBounds.setHeight(36);
            processEndBounds.setWidth(36);
            plane.addChildElement(processEndShape);
            processEnd.getIncoming().add(endFlow);
            endFlow.setTarget(modelInstance.getModelElementById(processEnd.getId()));
//            startFlow.setTarget(modelInstance.getModelElementById(NoInputElement);
//            parallelGateway.getIncoming().add(startFlow);

            //creat flow edge
            BpmnEdge start2gateway = modelInstance.newInstance(BpmnEdge.class);
            start2gateway.setBpmnElement(endFlow);
            start2gateway.setId(endFlow.getId()+"_di");
            Waypoint startWaypoint1 = modelInstance.newInstance(Waypoint.class);
            Waypoint startWaypoint2 = modelInstance.newInstance(Waypoint.class);
            startWaypoint1.setX(processEndBounds.getX() + 18);
            startWaypoint1.setY(processEndBounds.getY() + 18);
            startWaypoint2.setX(acBounds.getX() + acBounds.getWidth()/2);
            startWaypoint2.setY(acBounds.getY() + acBounds.getHeight()/2);
            start2gateway.getWaypoints().add(startWaypoint2);
            start2gateway.getWaypoints().add(startWaypoint1);
            plane.addChildElement(start2gateway);

        }

        System.out.println("**********add end end**************");
        return added;
    }
}
