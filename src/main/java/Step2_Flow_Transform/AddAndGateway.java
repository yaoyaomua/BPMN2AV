package Step2_Flow_Transform;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.ParallelGateway;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnEdge;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnPlane;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnShape;

public class AddAndGateway {

    public AddAndGateway() {
    }

    public static void add(BpmnModelInstance modelInstance)
    {
        Process process = modelInstance.getModelElementById("mergedProcess");
        //Create two hash map
        Map<String, Collection<SequenceFlow>> Target_map = new HashMap<>();
        Map<String, Collection<SequenceFlow>> Source_map = new HashMap<>();


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

        for (Map.Entry<String, Collection<SequenceFlow>> entry : Source_map.entrySet()) {
            String key = entry.getKey();
            Collection<SequenceFlow> value = entry.getValue();
            //Check if the activiy have more than one outcoming sequence flow
            if(value.size()>1)
            {
                List<SequenceFlow> newSequenceFlow = new ArrayList<SequenceFlow>();
                //add an empty parallel gateway
                ParallelGateway parallelGateway = modelInstance.newInstance(ParallelGateway.class);
                parallelGateway.setId("myParallelGateway");
                process.addChildElement(parallelGateway);

                //add an sequence flow between activity and paralle gateway(gateway incoming)
                SequenceFlow incomingFlow = modelInstance.newInstance(SequenceFlow.class);
                incomingFlow.setId("SequenceFlow_01");
                incomingFlow.setSource(modelInstance.getModelElementById(key));
                incomingFlow.setTarget(parallelGateway);
                //process.addChildElement(incomingFlow);

                modelInstance.getModelElementsByType(Process.class).iterator().next().addChildElement(incomingFlow);

                //Add to list
                newSequenceFlow.add(incomingFlow);

                //BpmnEdge bpmnEdge = modelInstance.getModelElementById(incomingFlow.getId()+"_di");
                //BaseElement bpmnElement = modelInstance.getModelElementById(incomingFlow.getId());
                //bpmnEdge.setBpmnElement(bpmnElement);

                //set incoming of the parallel gateway
                parallelGateway.getIncoming().add(incomingFlow);

                //add sequence flows between parallel gateway and activity(gateway outcoming)
                for (SequenceFlow outgoingFlow : entry.getValue()) {
                    SequenceFlow newOutgoingFlow = modelInstance.newInstance(SequenceFlow.class);
                    newOutgoingFlow.setId("new_" + outgoingFlow.getId());
                    newOutgoingFlow.setSource(parallelGateway);
                    newOutgoingFlow.setTarget(outgoingFlow.getTarget());
                    process.addChildElement(newOutgoingFlow);

                    modelInstance.getModelElementsByType(Process.class).iterator().next().addChildElement(incomingFlow);

                    //Add to List
                    newSequenceFlow.add(newOutgoingFlow);

                    //bpmnEdge = modelInstance.getModelElementById(incomingFlow.getId()+"_di");
                    //bpmnElement = modelInstance.getModelElementById(incomingFlow.getId());
                    //bpmnEdge.setBpmnElement(bpmnElement);

                    //set outcoming of the parallel gateway
                    parallelGateway.getOutgoing().add(newOutgoingFlow);
                }

                //remove origin outgoing flow
                for (SequenceFlow outgoingFlow : entry.getValue()) {
                    modelInstance.getModelElementById(key).removeChildElement(outgoingFlow);
                }

                //Get the position and bound of activities and gateway

                //preactivity:key
                BpmnShape preTaskShape = modelInstance.getModelElementById(key+"_di");
                //System.out.println(key);
                //Task preTask = modelInstance.getModelElementById(key);

                //preTaskShape.setBpmnElement(preTask);
                Double preTaskX = preTaskShape.getBounds().getX();
                Double preTaskY = preTaskShape.getBounds().getY();
                Double preTaskWidth = preTaskShape.getBounds().getWidth();
                Double preTaskHeight = preTaskShape.getBounds().getHeight();

                //gateway
                BpmnShape parallelGatewayShape = modelInstance.getModelElementsByType(BpmnShape.class).iterator().next();
                parallelGatewayShape.setBpmnElement(parallelGateway);
                Double gatewayX = parallelGatewayShape.getBounds().getX();
                Double gatewayY = parallelGatewayShape.getBounds().getY();
                Double gatewayWidth = parallelGatewayShape.getBounds().getWidth();
                Double gatewayHeight = parallelGatewayShape.getBounds().getHeight();



                //Add graphic information
                // Get the BPMN diagram instance
                BpmnPlane plane = modelInstance.getModelElementsByType(BpmnPlane.class).iterator().next();
                // Create a new shape for the gateway
                BpmnShape gatewayShape = createBpmnShape(modelInstance,parallelGateway.getId(),preTaskX+(preTaskWidth/2),preTaskY-100,50.0,50.0);
                // Add the shape to the BPMN diagram
                plane.addChildElement(gatewayShape);

                for (SequenceFlow newFlow : newSequenceFlow)
                {
                    BpmnEdge edge = createBpmnEdge(modelInstance, newFlow, 100.0, 100.0, 200.0, 200.0);
                    // Get the BPMN diagram and add the edge to it
                    plane.addChildElement(edge);
                }

            }
        }
    }
}
