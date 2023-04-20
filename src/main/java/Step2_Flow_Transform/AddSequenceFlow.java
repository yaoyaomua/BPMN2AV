package Step2_Flow_Transform;

import java.util.Collection;
import java.util.Iterator;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnEdge;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnShape;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;

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
            // whether the message flow starts from or ends event inside a subprocess
            if ((messageflow.getTarget().getElementType().getTypeName().equals("intermediateThrowEvent")) || (messageflow.getTarget().getElementType().getTypeName().equals("intermediateThrowEvent")))
            {

            }
            //whether include start event or end event
            //set the width and height of the new activity
            if((messageflow.getTarget().getElementType().getTypeName().equals("startEvent") && !(messageflow.getTarget().getParentElement().getElementType().getTypeName().equals("subProcess"))))
            {
                StartEvent startevent = modelInstance.getModelElementById(messageflow.getTarget().getId());
                Task task = modelInstance.newInstance(Task.class);
                task.setName("Start"+i);
                task.setId(startevent.getId());
                startevent.replaceWithElement(task);
                messageflow.setTarget(task);
            }
            if(messageflow.getSource().getElementType().getTypeName().equals("endEvent") && !(messageflow.getSource().getParentElement().getElementType().getTypeName().equals("subProcess")))
            {
                System.out.println("This is end event!");
                EndEvent endEvent = modelInstance.getModelElementById(messageflow.getSource().getId());
                Task task = modelInstance.newInstance(Task.class);
                task.setName("End"+i);
                task.setId(endEvent.getId());
                endEvent.replaceWithElement(task);
                messageflow.setSource(task);
            }
            // Create a new sequence flow element
            SequenceFlow sequenceFlow = modelInstance.newInstance(SequenceFlow.class);
            sequenceFlow.setId(messageflow.getId());
            if (!messageflow.getSource().getElementType().getTypeName().equals("endEvent")) {
                sequenceFlow.setSource(modelInstance.getModelElementById(messageflow.getSource().getId()));
            }
            else
            {
                ModelElementInstance subProcess = messageflow.getSource().getParentElement();
                sequenceFlow.setSource((FlowNode) subProcess);
            }
            if (!messageflow.getTarget().getElementType().getTypeName().equals("startEvent")) {
                sequenceFlow.setTarget(modelInstance.getModelElementById(messageflow.getTarget().getId()));
            }
            else
            {
                ModelElementInstance subProcess = messageflow.getTarget().getParentElement();
                sequenceFlow.setTarget((FlowNode) subProcess);
            }


            // Add the sequence flow element to the model instance
            modelInstance.getModelElementsByType(Process.class).iterator().next().addChildElement(sequenceFlow);

            // Add bpmnelement to bpmnEdge
            BpmnEdge bpmnEdge = modelInstance.getModelElementById(messageflow.getId()+"_di");
            BaseElement bpmnElement = modelInstance.getModelElementById(messageflow.getId());
            bpmnEdge.setBpmnElement(bpmnElement);
            /*
            BaseElement sourceElement = modelInstance.getModelElementById(sequenceFlow.getSource().getId());
            BpmnShape sourceElementShape = modelInstance.getModelElementById(sourceElement.getDiagramElement().getId());
            Double sourceX = sourceElementShape.getBounds().getX();
            Double sourceY = sourceElementShape.getBounds().getY();
            BaseElement targetElement = modelInstance.getModelElementById(sequenceFlow.getTarget().getId());
            BpmnShape targetElementShape = modelInstance.getModelElementById(targetElement.getDiagramElement().getId());
            Double targetX = targetElementShape.getBounds().getX();
            Double targetY = targetElementShape.getBounds().getY();
            CreateBPMNEdge.create(modelInstance,sequenceFlow,sourceX,sourceY,targetX,targetY);
*/

        }


    }
}
