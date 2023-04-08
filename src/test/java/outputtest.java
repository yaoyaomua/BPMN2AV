import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.FlowElement;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.Task;
import org.junit.Test;

import java.io.File;
import java.util.Collection;

public class outputtest {

    @Test
    public void incomingtest(){
        BpmnModelInstance bpmnModelInstance = Bpmn.readModelFromFile(new File("models/diagram.bpmn"));
        for (Task task: bpmnModelInstance.getModelElementsByType(Task.class)){
            Collection<SequenceFlow> incoming = task.getIncoming();
            for (SequenceFlow sequenceFlow : incoming){
                System.out.println("111111" + sequenceFlow.getId());
            }
        }

//        System.out.println(bpmnModelInstance.getModelElementById();
    }
}
