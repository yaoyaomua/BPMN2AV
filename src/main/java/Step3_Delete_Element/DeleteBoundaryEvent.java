package Step3_Delete_Element;

import Step2_Flow_Transform.AddIncomingOrOutcoming;
import Step2_Flow_Transform.CreateBPMNEdge;
import Step2_Flow_Transform.CreateBPMNShape;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;
import Step3_Delete_Element.Generate7ID;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnPlane;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnShape;

import java.util.*;

public class DeleteBoundaryEvent {
    public static List<NoAssociationTask> delete(BpmnModelInstance modelInstance, List<NoAssociationTask> tasksToDelete) {
        // Boundary blocking events <ActivityId,<EventIds>>
        Map<String, Collection<BoundaryEvent>> blockingEvents = new HashMap<>();
        // Boundary non-blocking events <ActivityId,<EventIds>>
        Map<String, Collection<BoundaryEvent>> nonBlockingEvents = new HashMap<>();
        //
        List<NoAssociationTask> newDeleteList =  new ArrayList<>();
        Process process = modelInstance.getModelElementById("mergedProcess");
        String newID;
        // Get all boundary events in process
        Collection<BoundaryEvent> allBoundaryEvents = modelInstance.getModelElementsByType(BoundaryEvent.class);
        // check if the activity have boundary event
        for (BoundaryEvent boundaryEvent: allBoundaryEvents)
        {
            for (NoAssociationTask taskToDelete : tasksToDelete) {
                System.out.println(boundaryEvent.getAttachedTo());
                if (boundaryEvent.getAttachedTo() == modelInstance.getModelElementById(taskToDelete.getId()))
                {
                    Task task = modelInstance.getModelElementById(taskToDelete.getId());
                    // non blocking event
                    if (boundaryEvent.cancelActivity())
                    {
                        List<SequenceFlow> newSequenceFlow = new ArrayList<SequenceFlow>();
                        Collection<SequenceFlow> outgoingFlows = boundaryEvent.getOutgoing();
                        outgoingFlows.addAll(task.getOutgoing());
                        // replace with exclusive split gateways
                        //add an empty exclusive gateway
                        ExclusiveGateway exclusiveGateway = modelInstance.newInstance(ExclusiveGateway.class);
                        do {
                            newID = Generate7ID.generate();
                        }while(modelInstance.getModelElementById("myExclusiveGateway_"+newID)!=null);
                        exclusiveGateway.setId("myExclusiveGateway_"+newID);
                        process.addChildElement(exclusiveGateway);

                        //add an sequence flow between activity and exclusive gateway(gateway incoming)
                        Collection<SequenceFlow> taskIncomings = task.getIncoming();
                        for (SequenceFlow taskIncoming :taskIncomings )
                        {
                            taskIncoming.setTarget(exclusiveGateway);
                            //set outgoing of the pre element
                            AddIncomingOrOutcoming.AddOutgoingToElement(modelInstance,task.getId(),taskIncoming);
                            //set incoming of the parallel gateway
                            exclusiveGateway.getIncoming().add(taskIncoming);
                        }
                        //add sequence flows between exclusive gateway and activity(gateway outcoming)
                        for (SequenceFlow outgoingFlow : outgoingFlows) {
                            SequenceFlow newOutgoingFlow = modelInstance.newInstance(SequenceFlow.class);
                            do {
                                newID = Generate7ID.generate();
                            }while(modelInstance.getModelElementById("Flow_"+newID)!=null);
                            newOutgoingFlow.setId("Flow_"+newID);
                            //parallelGatewayId++;
                            newOutgoingFlow.setSource(exclusiveGateway);
                            newOutgoingFlow.setTarget(outgoingFlow.getTarget());
                            process.addChildElement(newOutgoingFlow);
                            newSequenceFlow.add(newOutgoingFlow);
                            AddIncomingOrOutcoming.AddIncomingToElement(modelInstance,outgoingFlow.getTarget().getId(),newOutgoingFlow);
                            //set outcoming of the parallel gateway
                            exclusiveGateway.getOutgoing().add(newOutgoingFlow);
                        }
                        //remove origin outgoing flow
                        for (SequenceFlow outgoingFlow : outgoingFlows) {
                            if(modelInstance.getModelElementById(outgoingFlow.getId()) != null) {
                                modelInstance.getModelElementById(outgoingFlow.getSource().getId()).removeChildElement(outgoingFlow);
                                modelInstance.getModelElementById(outgoingFlow.getId()).getParentElement().removeChildElement(outgoingFlow);
                            }
                        }

                        //Get the position and bound of activities and gateway
                        //preactivity:key
                        BaseElement preTask = modelInstance.getModelElementById(task.getId());
                        BpmnShape preTaskShape = modelInstance.getModelElementById(preTask.getDiagramElement().getId());
                        Double preTaskX = preTaskShape.getBounds().getX();
                        Double preTaskY = preTaskShape.getBounds().getY();
                        Double preTaskWidth = preTaskShape.getBounds().getWidth();
                        Double preTaskHeight = preTaskShape.getBounds().getHeight();
                        //Add graphic information
                        // Get the BPMN diagram instance
                        BpmnPlane plane = modelInstance.getModelElementsByType(BpmnPlane.class).iterator().next();
                        // Create a new shape for the gateway
                        BpmnShape gatewayShape = CreateBPMNShape.create(modelInstance,exclusiveGateway.getId(),preTaskX,preTaskY,50.0,50.0);
                        // Add the shape to the BPMN diagram
                        plane.addChildElement(gatewayShape);

                        BaseElement exclusiveGatewayElement = modelInstance.getModelElementById(exclusiveGateway.getId());
                        BpmnShape exclusiveGatewayShape = modelInstance.getModelElementById(exclusiveGatewayElement.getDiagramElement().getId());
                        Double gatewayX = exclusiveGatewayShape.getBounds().getX();
                        Double gatewayY = exclusiveGatewayShape.getBounds().getY();
                        Double gatewayWidth = exclusiveGatewayShape.getBounds().getWidth();
                        Double gatewayHeight = exclusiveGatewayShape.getBounds().getHeight();

                        for (SequenceFlow newFlow : newSequenceFlow)
                        {
                            BaseElement nextTask = modelInstance.getModelElementById(newFlow.getTarget().getId());
                            BpmnShape nextTaskShape = modelInstance.getModelElementById(nextTask.getDiagramElement().getId());
                            Double nextTaskX = nextTaskShape.getBounds().getX();
                            Double nextTaskY = nextTaskShape.getBounds().getY();
                            Double nextTaskWidth = nextTaskShape.getBounds().getWidth();
                            Double nextTaskHeight = nextTaskShape.getBounds().getHeight();

                            CreateBPMNEdge.create(modelInstance, newFlow, gatewayX, gatewayY+gatewayHeight/2, nextTaskX, nextTaskY);
                        }
                        //delete element
                        boundaryEvent.getParentElement().removeChildElement(boundaryEvent);
                        task.getParentElement().removeChildElement(task);


                    }
                    // blocking event
                    else
                    {
                        List<SequenceFlow> newSequenceFlow = new ArrayList<SequenceFlow>();
                        Collection<SequenceFlow> outgoingFlows = boundaryEvent.getOutgoing();
                        outgoingFlows.addAll(task.getOutgoing());
                        // replace with inclusive split gateways
                        //add an empty inclusive gateway
                        InclusiveGateway inclusiveGateway = modelInstance.newInstance(InclusiveGateway.class);
                        do {
                            newID = Generate7ID.generate();
                        }while(modelInstance.getModelElementById("myInclusiveGateway_"+newID)!=null);
                        inclusiveGateway.setId("myInclusiveGateway_"+newID);
                        process.addChildElement(inclusiveGateway);

                        //add an sequence flow between activity and inclusive gateway(gateway incoming)
                        Collection<SequenceFlow> taskIncomings = task.getIncoming();
                        for (SequenceFlow taskIncoming :taskIncomings )
                        {
                            taskIncoming.setTarget(inclusiveGateway);
                            //set outgoing of the pre element
                            AddIncomingOrOutcoming.AddOutgoingToElement(modelInstance,task.getId(),taskIncoming);
                            //set incoming of the parallel gateway
                            inclusiveGateway.getIncoming().add(taskIncoming);
                        }
                        //add sequence flows between exclusive gateway and activity(gateway outcoming)
                        for (SequenceFlow outgoingFlow : outgoingFlows) {
                            SequenceFlow newOutgoingFlow = modelInstance.newInstance(SequenceFlow.class);
                            do {
                                newID = Generate7ID.generate();
                            }while(modelInstance.getModelElementById("Flow_"+newID)!=null);
                            newOutgoingFlow.setId("Flow_"+newID);
                            //parallelGatewayId++;
                            newOutgoingFlow.setSource(inclusiveGateway);
                            newOutgoingFlow.setTarget(outgoingFlow.getTarget());
                            process.addChildElement(newOutgoingFlow);
                            newSequenceFlow.add(newOutgoingFlow);
                            AddIncomingOrOutcoming.AddIncomingToElement(modelInstance,outgoingFlow.getTarget().getId(),newOutgoingFlow);
                            //set outcoming of the parallel gateway
                            inclusiveGateway.getOutgoing().add(newOutgoingFlow);
                        }
                        //remove origin outgoing flow
                        for (SequenceFlow outgoingFlow : outgoingFlows) {
                            if(modelInstance.getModelElementById(outgoingFlow.getId()) != null) {
                                modelInstance.getModelElementById(outgoingFlow.getSource().getId()).removeChildElement(outgoingFlow);
                                modelInstance.getModelElementById(outgoingFlow.getId()).getParentElement().removeChildElement(outgoingFlow);
                            }
                        }

                        //Get the position and bound of activities and gateway
                        //preactivity:key
                        BaseElement preTask = modelInstance.getModelElementById(task.getId());
                        BpmnShape preTaskShape = modelInstance.getModelElementById(preTask.getDiagramElement().getId());
                        Double preTaskX = preTaskShape.getBounds().getX();
                        Double preTaskY = preTaskShape.getBounds().getY();
                        Double preTaskWidth = preTaskShape.getBounds().getWidth();
                        Double preTaskHeight = preTaskShape.getBounds().getHeight();
                        //Add graphic information
                        // Get the BPMN diagram instance
                        BpmnPlane plane = modelInstance.getModelElementsByType(BpmnPlane.class).iterator().next();
                        // Create a new shape for the gateway
                        BpmnShape gatewayShape = CreateBPMNShape.create(modelInstance,inclusiveGateway.getId(),preTaskX,preTaskY,50.0,50.0);
                        // Add the shape to the BPMN diagram
                        plane.addChildElement(gatewayShape);

                        BaseElement inclusiveGatewayElement = modelInstance.getModelElementById(inclusiveGateway.getId());
                        BpmnShape inclusiveGatewayShape = modelInstance.getModelElementById(inclusiveGatewayElement.getDiagramElement().getId());
                        Double gatewayX = inclusiveGatewayShape.getBounds().getX();
                        Double gatewayY = inclusiveGatewayShape.getBounds().getY();
                        Double gatewayWidth = inclusiveGatewayShape.getBounds().getWidth();
                        Double gatewayHeight = inclusiveGatewayShape.getBounds().getHeight();

                        for (SequenceFlow newFlow : newSequenceFlow)
                        {
                            BaseElement nextTask = modelInstance.getModelElementById(newFlow.getTarget().getId());
                            BpmnShape nextTaskShape = modelInstance.getModelElementById(nextTask.getDiagramElement().getId());
                            Double nextTaskX = nextTaskShape.getBounds().getX();
                            Double nextTaskY = nextTaskShape.getBounds().getY();
                            Double nextTaskWidth = nextTaskShape.getBounds().getWidth();
                            Double nextTaskHeight = nextTaskShape.getBounds().getHeight();

                            CreateBPMNEdge.create(modelInstance, newFlow, gatewayX, gatewayY+gatewayHeight/2, nextTaskX, nextTaskY);
                        }
                        //delete element
                        boundaryEvent.getParentElement().removeChildElement(boundaryEvent);
                        task.getParentElement().removeChildElement(task);


                    }
                }
                else
                {
                    newDeleteList.add(taskToDelete);
                }

            }
        }
        return newDeleteList;
    }
}
