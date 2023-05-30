package StepTest;

import Step1_Delete_Pool.AddExclusiveGatewayForEndEvent;
import Step3_Delete_Element.*;
import Step4_Well_Structure.BPStruct.BoundaryEventForBPStruct;
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
//            BpmnModelInstance bpmnModelInstance = Bpmn.readModelFromFile(new File("models/diagram (82).bpmn"));
            BpmnModelInstance bpmnModelInstance = Bpmn.readModelFromFile(new File("models/diagram (86).bpmn"));




        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
