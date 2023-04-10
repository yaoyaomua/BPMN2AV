import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.FlowElement;
import org.camunda.bpm.model.bpmn.instance.Gateway;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.Task;
import org.junit.Test;

import java.io.File;
import java.util.Collection;

public class outputtest {

    @Test
    public void incomingtest(){
        BpmnModelInstance bpmnModelInstance = Bpmn.readModelFromFile(new File("models/diagram6.bpmn"));
        for (Gateway gateway : bpmnModelInstance.getModelElementsByType(Gateway.class)){
            System.out.println(gateway.getElementType().getTypeName());
            System.out.println(gateway instanceof Gateway);
        }

//        System.out.println(bpmnModelInstance.getModelElementById();
    }
}
