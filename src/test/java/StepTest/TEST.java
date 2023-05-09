package StepTest;

import Step1_Delete_Pool.AddExclusiveGatewayForEndEvent;
import Step3_Delete_Element.*;
import Step4_Well_Structure.DeleteParalleGatewaySequenceFlow;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;
import org.junit.Test;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;

public class TEST {

    @Test
    public void test1(){
        try {
            BpmnModelInstance bpmnModelInstance = Bpmn.readModelFromFile(new File("models/diagram (77).bpmn"));

            DeleteParalleGatewaySequenceFlow.delete(bpmnModelInstance);

            File outputFile = new File("models/result.bpmn");
            Bpmn.writeModelToFile(outputFile, bpmnModelInstance);

        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
