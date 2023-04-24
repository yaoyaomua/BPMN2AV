package StepTest;

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
            BpmnModelInstance bpmnModelInstance = Bpmn.readModelFromFile(new File("models/step3_add_sub_start_end.bpmn"));
            for (Event event: bpmnModelInstance.getModelElementsByType(Event.class)){
                System.out.println(event.getId());
            }
            System.out.println("**********start add************");
            AddSubProcessStartEndEvent.add(bpmnModelInstance);
            AddSuperStartEndEvent.addStart(bpmnModelInstance);
            AddSuperStartEndEvent.addEnd(bpmnModelInstance);
            File outputFile = new File("models/result.bpmn");
            Bpmn.writeModelToFile(outputFile, bpmnModelInstance);

        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
