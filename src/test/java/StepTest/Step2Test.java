package StepTest;

import Step1_Delete_Pool.DeletePool;
import Step1_Delete_Pool.MergeProcess;
import Step2_Flow_Transform.AddAndGateway;
import Step2_Flow_Transform.AddSequenceFlow;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.MessageFlow;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Collection;

public class Step2Test {

    @Test
    public void addTest(){
        try {
            //Read bpmn file
            String filePath = "models/Steps/bookdiagram.bpmn";
//            String filePath = "models/subprocesstest.bpmn";
            BpmnModelInstance modelInstance;
            try (InputStream inputStream = new FileInputStream(new File(filePath))) {
                modelInstance = Bpmn.readModelFromStream(inputStream);
            }
            Collection<MessageFlow> messageflows;
            messageflows = DeletePool.delete(modelInstance);
            // Delete all process tags and keep only the first one
            MergeProcess.merge(modelInstance);
            // Covert Message Flow to Sequence Flow
            AddSequenceFlow.add(modelInstance,messageflows);
            // Add And-GateWay
            AddAndGateway.add(modelInstance);
            // Delete empty subprocess
            //DeleteEmptySubprocess.delete(modelInstance);
            // Delete Repeat Flow
            //DeleteRepeatFlow.delete(modelInstance);
            // Store bpmn file
            File outputFile = new File("models/subprocesstestStep2Result.bpmn");
            Bpmn.writeModelToFile(outputFile, modelInstance);
            // Delete empty lines
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    public void replaceSequenceFlow(){
        try {
            //Read bpmn file
            String filePath = "models/Steps/step1_replace_messageflow.bpmn";
//            String filePath = "models/subprocesstest.bpmn";
            BpmnModelInstance modelInstance;
            try (InputStream inputStream = new FileInputStream(new File(filePath))) {
                modelInstance = Bpmn.readModelFromStream(inputStream);
            }
            Collection<MessageFlow> messageflows;
            messageflows = DeletePool.delete(modelInstance);
            // Delete all process tags and keep only the first one
            MergeProcess.merge(modelInstance);
            // Covert Message Flow to Sequence Flow
            AddSequenceFlow.add(modelInstance,messageflows);
            // Add And-GateWay
            // AddAndGateway.add(modelInstance);
            // Delete empty subprocess
            //DeleteEmptySubprocess.delete(modelInstance);
            // Delete Repeat Flow
            //DeleteRepeatFlow.delete(modelInstance);
            // Store bpmn file
            File outputFile = new File("models/Steps/step1_replace_messageflow_result.bpmn");
            Bpmn.writeModelToFile(outputFile, modelInstance);
            // Delete empty lines
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
