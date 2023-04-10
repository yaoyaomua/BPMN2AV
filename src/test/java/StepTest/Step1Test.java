package StepTest;

import Step1_Delete_Pool.DeletePool;
import Step1_Delete_Pool.MergeProcess;
import Step2_Flow_Transform.AddAndGateway;
import Step2_Flow_Transform.AddSequenceFlow;
import Step3_Delete_Element.DeleteRepeatFlow;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.MessageFlow;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collection;

public class Step1Test {

    @Test
    public void DeletePoolMergeProcessTest(){
        try {
            //Read bpmn file
            String filePath = "Data/diagram.xml";
            BpmnModelInstance modelInstance;
            try (InputStream inputStream = new FileInputStream(new File(filePath))) {
                modelInstance = Bpmn.readModelFromStream(inputStream);
            }
            Collection<MessageFlow> messageflows;
            messageflows = DeletePool.delete(modelInstance);
            // Delete all process tags and keep only the first one
            MergeProcess.merge(modelInstance);
            // Covert Message Flow to Sequence Flow
//            AddSequenceFlow.add(modelInstance,messageflows);
//            // Add And-GateWay
//            AddAndGateway.add(modelInstance);
//            // Delete Repeat Flow
//            DeleteRepeatFlow.delete(modelInstance);

            // Store bpmn file
            File outputFile = new File("Data/mergedModelInstance.bpmn");
            Bpmn.writeModelToFile(outputFile, modelInstance);
            // Delete empty lines
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
