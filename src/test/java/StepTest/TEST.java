package StepTest;

import Step1_Delete_Pool.AddExclusiveGatewayForEndEvent;
import Step3_Delete_Element.*;
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
            BpmnModelInstance bpmnModelInstance = Bpmn.readModelFromFile(new File("models/Steps/bookdiagram.bpmn"));

            AddExclusiveGatewayForEndEvent.add(bpmnModelInstance);

            File outputFile = new File("models/result.bpmn");
            Bpmn.writeModelToFile(outputFile, bpmnModelInstance);

        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
