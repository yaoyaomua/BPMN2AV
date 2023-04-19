package Step2_Flow_Transform;

import java.util.Collection;
import java.util.Iterator;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.BaseElement;
import org.camunda.bpm.model.bpmn.instance.IntermediateThrowEvent;
import org.camunda.bpm.model.bpmn.instance.MessageFlow;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.StartEvent;
import org.camunda.bpm.model.bpmn.instance.Task;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnEdge;

public class AddSequenceFlow {


    public AddSequenceFlow() {
    }

    //covert message flow to sequence flow
    public static void add(BpmnModelInstance modelInstance,Collection<MessageFlow> messageflows) {
        // Iteration All MessageFlow
        Iterator<MessageFlow> iterator_messageflows = messageflows.iterator();
        //Check messageflow
        int i = 0;
        while (iterator_messageflows.hasNext()) {
            i++;
            MessageFlow messageflow = iterator_messageflows.next();
            // 对 messageflow对象进行操作
            System.out.println("message flow sourceRef: " + messageflow.getSource().getId());
            System.out.println("message flow targetRef: " + messageflow.getTarget().getId());
            //whether include start event or end event
            //set the width and height of the new activity
            if(messageflow.getTarget().getElementType().getTypeName().equals("startEvent"))
            {
                StartEvent startevent = modelInstance.getModelElementById(messageflow.getTarget().getId());
                Task task = modelInstance.newInstance(Task.class);
                task.setName("Start"+i);
                task.setId(startevent.getId());
                startevent.replaceWithElement(task);
                messageflow.setTarget(task);
            }
            if(messageflow.getSource().getElementType().getTypeName().equals("intermediateThrowEvent"))
            {
                System.out.println("This is end event!");
                IntermediateThrowEvent intermediateThrowEvent = modelInstance.getModelElementById(messageflow.getSource().getId());
                Task task = modelInstance.newInstance(Task.class);
                task.setName("End"+i);
                task.setId(intermediateThrowEvent.getId());
                intermediateThrowEvent.replaceWithElement(task);
                messageflow.setSource(task);
            }
            // Create a new sequence flow element
            SequenceFlow sequenceFlow = modelInstance.newInstance(SequenceFlow.class);
            sequenceFlow.setId(messageflow.getId());
            sequenceFlow.setSource(modelInstance.getModelElementById(messageflow.getSource().getId()));
            sequenceFlow.setTarget(modelInstance.getModelElementById(messageflow.getTarget().getId()));
            // Add the sequence flow element to the model instance
            modelInstance.getModelElementsByType(Process.class).iterator().next().addChildElement(sequenceFlow);
            // Add bpmnelement to bpmnEdge
            BpmnEdge bpmnEdge = modelInstance.getModelElementById(messageflow.getId()+"_di");
            BaseElement bpmnElement = modelInstance.getModelElementById(messageflow.getId());
            bpmnEdge.setBpmnElement(bpmnElement);

        }

    }
}
