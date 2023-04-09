package Step2_Flow_Transform;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.BaseElement;
import org.camunda.bpm.model.bpmn.instance.ParallelGateway;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnEdge;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnPlane;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnShape;

public class AddAndGateway {
    public static void add(BpmnModelInstance modelInstance)
    {
        Process process = modelInstance.getModelElementById("mergedProcess");
        //Create two hash map
        Map<String, Collection<SequenceFlow>> Target_map = new HashMap<>();
        Map<String, Collection<SequenceFlow>> Source_map = new HashMap<>();
        int parallelGatewayId = 0;
        
        
        //Traversal All Sequence Flow
        for (SequenceFlow flow : process.getChildElementsByType(SequenceFlow.class)) {
            Collection<SequenceFlow> target_collection = new ArrayList<>();
            Collection<SequenceFlow> source_collection = new ArrayList<>();
            //Target Map
            //if key exists
            if(Target_map.containsKey(flow.getAttributeValue("targetRef")))
            {
               target_collection = Target_map.get(flow.getAttributeValue("targetRef"));
               target_collection.add(flow);
               Target_map.put(flow.getAttributeValue("targetRef"), target_collection);
            }
            else
            {
                target_collection.add(flow);
                Target_map.put(flow.getAttributeValue("targetRef"), target_collection);
            }
            
            //Source_Map
            if(Source_map.containsKey(flow.getAttributeValue("sourceRef")))
            {
               source_collection = Source_map.get(flow.getAttributeValue("sourceRef"));
               source_collection.add(flow);
               Source_map.put(flow.getAttributeValue("sourceRef"), source_collection);
            }
            else
            {
                source_collection.add(flow);
                Source_map.put(flow.getAttributeValue("sourceRef"), source_collection);
            }
    
        }
        //iterate Target Map and Source Map
        //Target Map: activity income sequence flow
        //Source Map: activity outcome sequence flow
        System.out.println("Target Map");
        for (Map.Entry<String, Collection<SequenceFlow>> entry : Target_map.entrySet()) {
            String key = entry.getKey();
            Collection<SequenceFlow> value = entry.getValue();
            System.out.println(key + " : " + value.size());
            }
        System.out.println("Source Map");
        for (Map.Entry<String, Collection<SequenceFlow>> entry : Source_map.entrySet()) {
            String key = entry.getKey();
            Collection<SequenceFlow> value = entry.getValue();
            System.out.println(key + " : " + value.size());
            }
        
        
         //Target_map:Add gateway and sequenceflow
        for (Map.Entry<String, Collection<SequenceFlow>> entry : Target_map.entrySet()) {
            String key = entry.getKey();
            Collection<SequenceFlow> value = entry.getValue();
            //Check if the activiy have more than one outcoming sequence flow
            if(value.size()>1)
            {
                List<SequenceFlow> newSequenceFlow = new ArrayList<SequenceFlow>();
                //add an empty parallel gateway
                ParallelGateway parallelGateway = modelInstance.newInstance(ParallelGateway.class);
                parallelGateway.setId("myParallelGateway_"+parallelGatewayId);
                process.addChildElement(parallelGateway);
                
                //add an sequence flow between activity and paralle gateway(gateway incoming)
                SequenceFlow outGoing = modelInstance.newInstance(SequenceFlow.class);
                outGoing.setId("SequenceFlow_"+parallelGatewayId);
                outGoing.setTarget(modelInstance.getModelElementById(key));
                outGoing.setSource(parallelGateway);
                modelInstance.getModelElementsByType(Process.class).iterator().next().addChildElement(outGoing);
                //Add to list
                newSequenceFlow.add(outGoing);
                //set incoming of the parallel gateway
                parallelGateway.getOutgoing().add(outGoing);
                
                //add sequence flows between parallel gateway and activity(gateway outcoming)
                for (SequenceFlow incomingFlow : entry.getValue()) {
                    SequenceFlow newIncmoingFlow = modelInstance.newInstance(SequenceFlow.class);
                    newIncmoingFlow.setId("new_" + incomingFlow.getId());
                    newIncmoingFlow.setTarget(parallelGateway);
                    newIncmoingFlow.setSource(incomingFlow.getSource());
                    process.addChildElement(newIncmoingFlow);
                    
                    modelInstance.getModelElementsByType(Process.class).iterator().next().addChildElement(incomingFlow);
                    
                    //Add to List
                    newSequenceFlow.add(newIncmoingFlow); 
                    //set outcoming of the parallel gateway
                    parallelGateway.getOutgoing().add(newIncmoingFlow);
                }
                //remove origin outgoing flow
                for (SequenceFlow incomingFlow : entry.getValue()) {
                    modelInstance.getModelElementById(key).removeChildElement(incomingFlow);
                    modelInstance.getModelElementById(incomingFlow.getId()).getParentElement().removeChildElement(incomingFlow);
                }
                
                //Get the position and bound of activities and gateway
                //preactivity:key
                BaseElement nextElement = modelInstance.getModelElementById(key);
                BpmnShape nextElementShape = modelInstance.getModelElementById(nextElement.getDiagramElement().getId());
                Double nextElementX = nextElementShape.getBounds().getX();
                Double nextElementY = nextElementShape.getBounds().getY();
                Double nextElementWidth = nextElementShape.getBounds().getWidth();
                Double nextElementHeight = nextElementShape.getBounds().getHeight();
                //Add graphic information
                // Get the BPMN diagram instance
                BpmnPlane plane = modelInstance.getModelElementsByType(BpmnPlane.class).iterator().next();
                // Create a new shape for the gateway
                BpmnShape gatewayShape = CreateBPMNShape.create(modelInstance,parallelGateway.getId(),nextElementX,nextElementY-100,50.0,50.0);
                // Add the shape to the BPMN diagram
                plane.addChildElement(gatewayShape);
                //gateway
                BaseElement gatewayElement = modelInstance.getModelElementById(parallelGateway.getId());
                BpmnShape parallelGatewayShape = modelInstance.getModelElementById(gatewayElement.getDiagramElement().getId());
                Double gatewayX = parallelGatewayShape.getBounds().getX();
                Double gatewayY = parallelGatewayShape.getBounds().getY();
                Double gatewayWidth = parallelGatewayShape.getBounds().getWidth();
                Double gatewayHeight = parallelGatewayShape.getBounds().getHeight();
                int flag = 0;
                for (SequenceFlow newFlow : newSequenceFlow)
                {
                    BaseElement preElement = modelInstance.getModelElementById(newFlow.getSource().getId());
                    BpmnShape preElementShape = modelInstance.getModelElementById(preElement.getDiagramElement().getId());
                    Double preElementX = preElementShape.getBounds().getX();
                    Double preElementY = preElementShape.getBounds().getY();
                    Double preElementWidth = preElementShape.getBounds().getWidth();
                    Double preElementHeight = preElementShape.getBounds().getHeight();

                    if(flag == 0)
                    {
                        //createBpmnEdge(modelInstance, newFlow, preElementX, preElementY,gatewayX, gatewayY+gatewayHeight/2);
                        CreateBPMNEdge.create(modelInstance, newFlow, gatewayX+20, gatewayY+gatewayHeight/2, nextElementX, nextElementY);
                    }
                    else
                    {
                        CreateBPMNEdge.create(modelInstance, newFlow, preElementX, preElementY,gatewayX+10, gatewayY);
                    }
           
                    flag ++;


                }
                
         
            }
            parallelGatewayId++;
        }
        
        //Source_map:Add gateway and sequenceflow
        for (Map.Entry<String, Collection<SequenceFlow>> entry : Source_map.entrySet()) {
            String key = entry.getKey();
            Collection<SequenceFlow> value = entry.getValue();
            //Check if the activiy have more than one outcoming sequence flow
            if(value.size()>1)
            {
                List<SequenceFlow> newSequenceFlow2 = new ArrayList<SequenceFlow>();
                //add an empty parallel gateway
                ParallelGateway parallelGateway = modelInstance.newInstance(ParallelGateway.class);
                parallelGateway.setId("myParallelGateway_"+parallelGatewayId);
                process.addChildElement(parallelGateway);
                
                //add an sequence flow between activity and paralle gateway(gateway incoming)
                SequenceFlow incomingFlow = modelInstance.newInstance(SequenceFlow.class);
                incomingFlow.setId("New_SequenceFlow_"+parallelGatewayId);
                parallelGatewayId++;
                incomingFlow.setSource(modelInstance.getModelElementById(key));
                incomingFlow.setTarget(parallelGateway);
                //process.addChildElement(incomingFlow);
               
                modelInstance.getModelElementsByType(Process.class).iterator().next().addChildElement(incomingFlow);
                
                //Add to list
                newSequenceFlow2.add(incomingFlow);
                
                //set incoming of the parallel gateway
                parallelGateway.getIncoming().add(incomingFlow);
                
                //add sequence flows between parallel gateway and activity(gateway outcoming)
                for (SequenceFlow outgoingFlow : entry.getValue()) {
                    SequenceFlow newOutgoingFlow = modelInstance.newInstance(SequenceFlow.class);
                    newOutgoingFlow.setId("New_SequenceFlow_"+parallelGatewayId);
                    parallelGatewayId++;
                    newOutgoingFlow.setSource(parallelGateway);
                    newOutgoingFlow.setTarget(outgoingFlow.getTarget());
                    process.addChildElement(newOutgoingFlow);
                    
                    modelInstance.getModelElementsByType(Process.class).iterator().next().addChildElement(incomingFlow);
                    
                    //Add to List
                    newSequenceFlow2.add(newOutgoingFlow); 
                    //set outcoming of the parallel gateway
                    parallelGateway.getOutgoing().add(newOutgoingFlow);
                }
                //remove origin outgoing flow
                for (SequenceFlow outgoingFlow : entry.getValue()) {
                    if(modelInstance.getModelElementById(outgoingFlow.getId()) != null) {
                        modelInstance.getModelElementById(key).removeChildElement(outgoingFlow);
                        modelInstance.getModelElementById(outgoingFlow.getId()).getParentElement().removeChildElement(outgoingFlow);
                    }
                }
                //Get the position and bound of activities and gateway
                //preactivity:key
                BaseElement preTask = modelInstance.getModelElementById(key);
                BpmnShape preTaskShape = modelInstance.getModelElementById(preTask.getDiagramElement().getId());
                Double preTaskX = preTaskShape.getBounds().getX();
                Double preTaskY = preTaskShape.getBounds().getY();
                Double preTaskWidth = preTaskShape.getBounds().getWidth();
                Double preTaskHeight = preTaskShape.getBounds().getHeight();
                //Add graphic information
                // Get the BPMN diagram instance
                BpmnPlane plane = modelInstance.getModelElementsByType(BpmnPlane.class).iterator().next();
                // Create a new shape for the gateway
                BpmnShape gatewayShape = CreateBPMNShape.create(modelInstance,parallelGateway.getId(),preTaskX,preTaskY-100,50.0,50.0);
                // Add the shape to the BPMN diagram
                plane.addChildElement(gatewayShape);
                //gateway
                //BpmnShape parallelGatewayShape = modelInstance.getModelElementsByType(BpmnShape.class).iterator().next();
                //parallelGatewayShape.setBpmnElement(parallelGateway);
                BaseElement parallelGatewayElement = modelInstance.getModelElementById(parallelGateway.getId());
                BpmnShape parallelGatewayShape = modelInstance.getModelElementById(parallelGatewayElement.getDiagramElement().getId());
                Double gatewayX = parallelGatewayShape.getBounds().getX();
                Double gatewayY = parallelGatewayShape.getBounds().getY();
                Double gatewayWidth = parallelGatewayShape.getBounds().getWidth();
                Double gatewayHeight = parallelGatewayShape.getBounds().getHeight();
                int flag = 0;
                for (SequenceFlow newFlow : newSequenceFlow2)
                {
                    BaseElement nextTask = modelInstance.getModelElementById(newFlow.getTarget().getId());
                    BpmnShape nextTaskShape = modelInstance.getModelElementById(nextTask.getDiagramElement().getId());
                    Double nextTaskX = nextTaskShape.getBounds().getX();
                    Double nextTaskY = nextTaskShape.getBounds().getY();
                    Double nextTaskWidth = nextTaskShape.getBounds().getWidth();
                    Double nextTaskHeight = nextTaskShape.getBounds().getHeight();

                    if(flag == 0)
                    {
                        CreateBPMNEdge.create(modelInstance, newFlow, preTaskX, preTaskY+10, gatewayX, gatewayY+gatewayHeight/2);
                    }
                    else
                    {
                        CreateBPMNEdge.create(modelInstance, newFlow, gatewayX, gatewayY+gatewayHeight/2, nextTaskX, nextTaskY);
                    }
           
                    flag ++;


                }
                
         
            }
            parallelGatewayId++;
        }
        
    }
}
