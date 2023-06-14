
package Step3_Delete_Element;

import Step2_Flow_Transform.AddIncomingOrOutcoming;
import Step2_Flow_Transform.CreateBPMNEdge;
import Step2_Flow_Transform.CreateBPMNShape;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnPlane;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnShape;

import java.util.List;
import java.util.*;

public class DeleteBoundaryEvent {
        public static void delete (BpmnModelInstance modelInstance, String artifact){
            HashMap<String,String> element2graph = BPMNElement2Graph.map(modelInstance);
            System.out.println(element2graph.toString());

            List<NoAssociationTask> tasksToDelete = new ArrayList<>();
            //mark irrelevant elements by check the Associations of the task
            for(Task task: modelInstance.getModelElementsByType(Task.class)){
                Collection<DataInputAssociation> inputDataAssociations = task.getDataInputAssociations();
                Collection<DataOutputAssociation> outputDataAssociations = task.getDataOutputAssociations();
                boolean isIrr = true;
                for (DataInputAssociation input : inputDataAssociations){
                    Collection<ItemAwareElement> sources = input.getSources();
                    for (ItemAwareElement itemAwareElement : sources){
                        DataObjectReference dataObjectReference = modelInstance.getModelElementById(itemAwareElement.getId());
                        String dataName = dataObjectReference.getName();
                        //check the data is the artifact or not?
                        if (dataName.equals(artifact)){
                            System.out.println(dataName.equals(artifact));
                            isIrr = false;
                        }
                    }
                }

                for (DataOutputAssociation output : outputDataAssociations){
                    String dataId = output.getTarget().getId();
                    DataObjectReference dataObjectReference = modelInstance.getModelElementById(dataId);
                    String dataName = dataObjectReference.getName();
                    //check the data is the artifact or not?
                    if (dataName.equals(artifact)){
                        System.out.println(dataName.equals(artifact));
                        isIrr = false;
                    }
                }

                if (isIrr){
                    NoAssociationTask nut = new NoAssociationTask(task.getId(),task);
                    tasksToDelete.add(nut);
                }
                System.out.println("activity name: " + task.getName());
                System.out.println("activity need delete? : " + isIrr);

            }

            //
            Process process = modelInstance.getModelElementsByType(Process.class).iterator().next();
            String newID;
            // Get all boundary events in process
            Collection<BoundaryEvent> allBoundaryEvents = modelInstance.getModelElementsByType(BoundaryEvent.class);
            // check if the activity have boundary event
            for (BoundaryEvent boundaryEvent : allBoundaryEvents) {
                Boolean isDeleteEvent = true;
                Collection<DataOutputAssociation> boundaryEventOutputdatas = boundaryEvent.getDataOutputAssociations();
                if (boundaryEventOutputdatas.size()==0)
                {
                    System.out.println("this boudary event do not have output data assosiation");
                    isDeleteEvent = true;
                }
                else
                {
                    for (DataOutputAssociation boundaryEventOutputdata : boundaryEventOutputdatas)
                    {
                        DataObjectReference dataObjectReference = modelInstance.getModelElementById(boundaryEventOutputdata.getTarget().getId());
                        //Collection<Association> associations = modelInstance.getModelElementsByType(Association.class);
                        //Collection<String> textAnnotations = new ArrayList<>();
                        String dataState = "";
                        /*for (Association association:associations)
                        {
                            // the text annotation of this dataobject reference
                            if (association.getSource().getId().equals(dataObjectReference.getId()))
                            {
                                TextAnnotation textAnnotation = modelInstance.getModelElementById(association.getTarget().getId());
                                if(textAnnotation.getText() != null) {
                                    textAnnotations.add(textAnnotation.getText().toString());
                                }
                            }
                        }*/
                        if (dataObjectReference.getDataState() != null)
                        {
                            dataState = dataObjectReference.getDataState().getName().toString();
                        }
                        // same artifact
                        if (dataObjectReference.getName().equals(artifact)){
                            System.out.println(dataObjectReference.getName());
                            // input data object with state
                                /*for (String textAnnotation : textAnnotations) {
                                    if (!textAnnotation.equals("")) {
                                        //modelInstance.getModelElementById(task.getDiagramElement().getId()).getParentElement().removeChildElement(modelInstance.getModelElementById(task.getDiagramElement().getId()));
                                        //task.getParentElement().removeChildElement(task);
                                        isDeleteEvent = false;
                                    }
                                }*/
                            if (!dataState.equals(""))
                            {
                                isDeleteEvent = false;
                            }
                        }
                        //different artifact
                        else
                        {
                            // input data object without state
                            /*if (textAnnotations.size() == 0)
                            {
                                isDeleteEvent = false;
                            }
                            else {
                                for (String textAnnotation : textAnnotations) {
                                    if (textAnnotation.equals("")) {
                                        isDeleteEvent = false;
                                    }
                                }
                            }*/
                            if (dataState.equals(""))
                            {
                                isDeleteEvent = false;
                            }
                            /*else {
                                    if (dataState.equals("")) {
                                        isDeleteEvent = false;
                                    }
                            }*/
                        }
                        if (!isDeleteEvent)
                        {
                            break;
                        }
                    }
                }

                // replace event
                if (isDeleteEvent) {
                    for (NoAssociationTask taskToDelete : tasksToDelete) {
                        System.out.println(boundaryEvent.getAttachedTo());
                        if (boundaryEvent.getAttachedTo() == modelInstance.getModelElementById(taskToDelete.getId())) {
                            Task task = modelInstance.getModelElementById(taskToDelete.getId());
                            //  blocking event
                            if (boundaryEvent.cancelActivity()) {
                                List<SequenceFlow> newSequenceFlow = new ArrayList<SequenceFlow>();
                                Collection<SequenceFlow> outgoingFlows = boundaryEvent.getOutgoing();
                                outgoingFlows.addAll(task.getOutgoing());
                                // replace with exclusive split gateways
                                //add an empty exclusive gateway
                                ExclusiveGateway exclusiveGateway = modelInstance.newInstance(ExclusiveGateway.class);
                                do {
                                    newID = Generate7ID.generate();
                                } while (modelInstance.getModelElementById("myExclusiveGateway_" + newID) != null);
                                exclusiveGateway.setId("myExclusiveGateway_" + newID);
                                process.addChildElement(exclusiveGateway);

                                //add an sequence flow between activity and exclusive gateway(gateway incoming)
                                Collection<SequenceFlow> taskIncomings = task.getIncoming();
                                for (SequenceFlow taskIncoming : taskIncomings) {
                                    taskIncoming.setTarget(exclusiveGateway);
                                    //set outgoing of the pre element
                                    AddIncomingOrOutcoming.AddOutgoingToElement(modelInstance, taskIncoming.getSource().getId(), taskIncoming);
                                    //set incoming of the parallel gateway
                                    exclusiveGateway.getIncoming().add(taskIncoming);
                                }
                                //add sequence flows between exclusive gateway and activity(gateway outcoming)
                                for (SequenceFlow outgoingFlow : outgoingFlows) {
                                    SequenceFlow newOutgoingFlow = modelInstance.newInstance(SequenceFlow.class);
                                    do {
                                        newID = Generate7ID.generate();
                                    } while (modelInstance.getModelElementById("Flow_" + newID) != null);
                                    newOutgoingFlow.setId("Flow_" + newID);
                                    //parallelGatewayId++;
                                    newOutgoingFlow.setSource(exclusiveGateway);
                                    newOutgoingFlow.setTarget(outgoingFlow.getTarget());
                                    process.addChildElement(newOutgoingFlow);
                                    newSequenceFlow.add(newOutgoingFlow);
                                    AddIncomingOrOutcoming.AddIncomingToElement(modelInstance, outgoingFlow.getTarget().getId(), newOutgoingFlow);
                                    //set outcoming of the parallel gateway
                                    exclusiveGateway.getOutgoing().add(newOutgoingFlow);
                                }
                                //remove origin outgoing flow
                                for (SequenceFlow outgoingFlow : outgoingFlows) {
                                    if (modelInstance.getModelElementById(outgoingFlow.getId()) != null) {
                                        modelInstance.getModelElementById(outgoingFlow.getDiagramElement().getId()).getParentElement().removeChildElement(modelInstance.getModelElementById(outgoingFlow.getDiagramElement().getId()));
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
                                BpmnShape gatewayShape = CreateBPMNShape.create(modelInstance, exclusiveGateway.getId(), preTaskX, preTaskY, 50.0, 50.0);
                                // Add the shape to the BPMN diagram
                                plane.addChildElement(gatewayShape);

                                BaseElement exclusiveGatewayElement = modelInstance.getModelElementById(exclusiveGateway.getId());
                                BpmnShape exclusiveGatewayShape = modelInstance.getModelElementById(exclusiveGatewayElement.getDiagramElement().getId());
                                Double gatewayX = exclusiveGatewayShape.getBounds().getX();
                                Double gatewayY = exclusiveGatewayShape.getBounds().getY();
                                Double gatewayWidth = exclusiveGatewayShape.getBounds().getWidth();
                                Double gatewayHeight = exclusiveGatewayShape.getBounds().getHeight();

                                for (SequenceFlow newFlow : newSequenceFlow) {
                                    BaseElement nextTask = modelInstance.getModelElementById(newFlow.getTarget().getId());
                                    BpmnShape nextTaskShape = modelInstance.getModelElementById(nextTask.getDiagramElement().getId());
                                    Double nextTaskX = nextTaskShape.getBounds().getX();
                                    Double nextTaskY = nextTaskShape.getBounds().getY();
                                    Double nextTaskWidth = nextTaskShape.getBounds().getWidth();
                                    Double nextTaskHeight = nextTaskShape.getBounds().getHeight();

                                    CreateBPMNEdge.create(modelInstance, newFlow, gatewayX, gatewayY + gatewayHeight / 2, nextTaskX, nextTaskY);
                                }
                                //delete element: task, boundary event , association ,data reference ,data association
                                Collection<DataOutputAssociation> boundaryEventOutputs = boundaryEvent.getDataOutputAssociations();
                                if (boundaryEventOutputs.size()>0) {
                                    System.out.println("size > 0");
                                    for (DataOutputAssociation boundaryEventOutput : boundaryEventOutputs) {
                                        DataObjectReference dataObjectReference = modelInstance.getModelElementById(boundaryEventOutput.getTarget().getId());
                                        Collection<Association> associations = modelInstance.getModelElementsByType(Association.class);
                                        for (Association association : associations) {
                                            // the text annotation of this dataobject reference
                                            if (association.getSource().getId().equals(dataObjectReference.getId())) {
                                                modelInstance.getModelElementById(association.getDiagramElement().getId()).getParentElement().removeChildElement(modelInstance.getModelElementById(association.getDiagramElement().getId()));
                                                association.getParentElement().removeChildElement(association);
                                                modelInstance.getModelElementById(association.getTarget().getDiagramElement().getId()).getParentElement().removeChildElement(modelInstance.getModelElementById(association.getTarget().getDiagramElement().getId()));
                                                association.getTarget().getParentElement().removeChildElement(association.getTarget());
                                            }
                                        }
                                        modelInstance.getModelElementById(dataObjectReference.getDiagramElement().getId()).getParentElement().removeChildElement(modelInstance.getModelElementById(dataObjectReference.getDiagramElement().getId()));
                                        dataObjectReference.getParentElement().removeChildElement(dataObjectReference);
                                        modelInstance.getModelElementById(boundaryEventOutput.getDiagramElement().getId()).getParentElement().removeChildElement(modelInstance.getModelElementById(boundaryEventOutput.getDiagramElement().getId()));
                                        boundaryEventOutput.getParentElement().removeChildElement(boundaryEventOutput);
                                    }
                                }
                                modelInstance.getModelElementById(task.getDiagramElement().getId()).getParentElement().removeChildElement(modelInstance.getModelElementById(task.getDiagramElement().getId()));
                                modelInstance.getModelElementById(boundaryEvent.getDiagramElement().getId()).getParentElement().removeChildElement(modelInstance.getModelElementById(boundaryEvent.getDiagramElement().getId()));
                                boundaryEvent.getParentElement().removeChildElement(boundaryEvent);
                                task.getParentElement().removeChildElement(task);



                            }
                            //non blocking event
                            else {
                                List<SequenceFlow> newSequenceFlow = new ArrayList<SequenceFlow>();
                                Collection<SequenceFlow> outgoingFlows = boundaryEvent.getOutgoing();
                                outgoingFlows.addAll(task.getOutgoing());
                                // replace with inclusive split gateways
                                //add an empty inclusive gateway
                                InclusiveGateway inclusiveGateway = modelInstance.newInstance(InclusiveGateway.class);
                                do {
                                    newID = Generate7ID.generate();
                                } while (modelInstance.getModelElementById("myInclusiveGateway_" + newID) != null);
                                inclusiveGateway.setId("myInclusiveGateway_" + newID);
                                process.addChildElement(inclusiveGateway);

                                //add an sequence flow between activity and inclusive gateway(gateway incoming)
                                Collection<SequenceFlow> taskIncomings = task.getIncoming();
                                for (SequenceFlow taskIncoming : taskIncomings) {
                                    taskIncoming.setTarget(inclusiveGateway);
                                    //set outgoing of the pre element
                                    AddIncomingOrOutcoming.AddOutgoingToElement(modelInstance, taskIncoming.getSource().getId(), taskIncoming);
                                    //set incoming of the parallel gateway
                                    inclusiveGateway.getIncoming().add(taskIncoming);
                                }
                                //add sequence flows between exclusive gateway and activity(gateway outcoming)
                                for (SequenceFlow outgoingFlow : outgoingFlows) {
                                    SequenceFlow newOutgoingFlow = modelInstance.newInstance(SequenceFlow.class);
                                    do {
                                        newID = Generate7ID.generate();
                                    } while (modelInstance.getModelElementById("Flow_" + newID) != null);
                                    newOutgoingFlow.setId("Flow_" + newID);
                                    //parallelGatewayId++;
                                    newOutgoingFlow.setSource(inclusiveGateway);
                                    newOutgoingFlow.setTarget(outgoingFlow.getTarget());
                                    process.addChildElement(newOutgoingFlow);
                                    newSequenceFlow.add(newOutgoingFlow);
                                    AddIncomingOrOutcoming.AddIncomingToElement(modelInstance, outgoingFlow.getTarget().getId(), newOutgoingFlow);
                                    //set outcoming of the parallel gateway
                                    inclusiveGateway.getOutgoing().add(newOutgoingFlow);
                                }
                                //remove origin outgoing flow
                                for (SequenceFlow outgoingFlow : outgoingFlows) {
                                    if (modelInstance.getModelElementById(outgoingFlow.getId()) != null) {
                                        modelInstance.getModelElementById(outgoingFlow.getDiagramElement().getId()).getParentElement().removeChildElement(modelInstance.getModelElementById(outgoingFlow.getDiagramElement().getId()));
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
                                BpmnShape gatewayShape = CreateBPMNShape.create(modelInstance, inclusiveGateway.getId(), preTaskX, preTaskY, 50.0, 50.0);
                                // Add the shape to the BPMN diagram
                                plane.addChildElement(gatewayShape);

                                BaseElement inclusiveGatewayElement = modelInstance.getModelElementById(inclusiveGateway.getId());
                                BpmnShape inclusiveGatewayShape = modelInstance.getModelElementById(inclusiveGatewayElement.getDiagramElement().getId());
                                Double gatewayX = inclusiveGatewayShape.getBounds().getX();
                                Double gatewayY = inclusiveGatewayShape.getBounds().getY();
                                Double gatewayWidth = inclusiveGatewayShape.getBounds().getWidth();
                                Double gatewayHeight = inclusiveGatewayShape.getBounds().getHeight();

                                for (SequenceFlow newFlow : newSequenceFlow) {
                                    BaseElement nextTask = modelInstance.getModelElementById(newFlow.getTarget().getId());
                                    BpmnShape nextTaskShape = modelInstance.getModelElementById(nextTask.getDiagramElement().getId());
                                    Double nextTaskX = nextTaskShape.getBounds().getX();
                                    Double nextTaskY = nextTaskShape.getBounds().getY();
                                    Double nextTaskWidth = nextTaskShape.getBounds().getWidth();
                                    Double nextTaskHeight = nextTaskShape.getBounds().getHeight();

                                    CreateBPMNEdge.create(modelInstance, newFlow, gatewayX, gatewayY + gatewayHeight / 2, nextTaskX, nextTaskY);
                                }
                                //delete element: task, boundary event , association ,data reference ,data association
                                Collection<DataOutputAssociation> boundaryEventOutputs = boundaryEvent.getDataOutputAssociations();
                                if (boundaryEventOutputs.size()>0) {
                                    System.out.println("size > 0");
                                    for (DataOutputAssociation boundaryEventOutput : boundaryEventOutputs) {
                                        DataObjectReference dataObjectReference = modelInstance.getModelElementById(boundaryEventOutput.getTarget().getId());
                                        Collection<Association> associations = modelInstance.getModelElementsByType(Association.class);
                                        for (Association association : associations) {
                                            // the text annotation of this dataobject reference
                                            if (association.getSource().getId().equals(dataObjectReference.getId())) {
                                                modelInstance.getModelElementById(association.getDiagramElement().getId()).getParentElement().removeChildElement(modelInstance.getModelElementById(association.getDiagramElement().getId()));
                                                association.getParentElement().removeChildElement(association);
                                                modelInstance.getModelElementById(association.getTarget().getDiagramElement().getId()).getParentElement().removeChildElement(modelInstance.getModelElementById(association.getTarget().getDiagramElement().getId()));
                                                association.getTarget().getParentElement().removeChildElement(association.getTarget());
                                            }
                                        }
                                        modelInstance.getModelElementById(dataObjectReference.getDiagramElement().getId()).getParentElement().removeChildElement(modelInstance.getModelElementById(dataObjectReference.getDiagramElement().getId()));
                                        dataObjectReference.getParentElement().removeChildElement(dataObjectReference);
                                        modelInstance.getModelElementById(boundaryEventOutput.getDiagramElement().getId()).getParentElement().removeChildElement(modelInstance.getModelElementById(boundaryEventOutput.getDiagramElement().getId()));
                                        boundaryEventOutput.getParentElement().removeChildElement(boundaryEventOutput);
                                    }
                                }
                                modelInstance.getModelElementById(task.getDiagramElement().getId()).getParentElement().removeChildElement(modelInstance.getModelElementById(task.getDiagramElement().getId()));
                                modelInstance.getModelElementById(boundaryEvent.getDiagramElement().getId()).getParentElement().removeChildElement(modelInstance.getModelElementById(boundaryEvent.getDiagramElement().getId()));
                                task.getParentElement().removeChildElement(task);
                                boundaryEvent.getParentElement().removeChildElement(boundaryEvent);


                            }
                        }

                    }
                }
            }

        }
}
