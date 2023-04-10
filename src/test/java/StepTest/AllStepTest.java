package StepTest;

import Step1_Delete_Pool.DeletePool;
import Step1_Delete_Pool.MergeProcess;
import Step2_Flow_Transform.AddAndGateway;
import Step2_Flow_Transform.AddSequenceFlow;
import Step3_Delete_Element.Delete121Gateway;
import Step3_Delete_Element.DeleteElement;
import Step3_Delete_Element.DeleteRepeatFlow;
import Step4_Well_Structure.DataObjectAddToJSON;
import Step4_Well_Structure.DataObjectStore;
import Step4_Well_Structure.MyDataObject;
import de.hpi.bpt.process.Process;
import ee.ut.bpstruct2.Restructurer;
import ee.ut.bpstruct2.util.BPMN2Reader;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.MessageFlow;
import org.json.JSONObject;
import org.junit.Test;

import java.io.*;
import java.util.Collection;
import java.util.Map;

public class AllStepTest {

    @Test
    public void stepsTest(){
        try {
            //Read bpmn file
            String filePath = "models/diagram (8).bpmn";
            BpmnModelInstance modelInstance;
            try (InputStream inputStream = new FileInputStream(new File(filePath))) {
                modelInstance = Bpmn.readModelFromStream(inputStream);
            }
            Collection<MessageFlow> messageflows;

            //step 1 delete pool and store message flows
            System.out.println("*************************");
            System.out.println("step 1");
            messageflows = DeletePool.delete(modelInstance);
            // Delete all process tags and keep only the first one
            MergeProcess.merge(modelInstance);
            File step1output = new File("models/step1output.bpmn");
            Bpmn.writeModelToFile(step1output, modelInstance);

            //step 2 transform message flow to sequence flow
            System.out.println("*************************");
            System.out.println("step 2");
            // Covert Message Flow to Sequence Flow
            AddSequenceFlow.add(modelInstance,messageflows);
            // Add And-GateWay
            AddAndGateway.add(modelInstance);
            File step2output = new File("models/step2output.bpmn");
            Bpmn.writeModelToFile(step2output, modelInstance);

            //step 3 delete elements
            System.out.println("*************************");
            System.out.println("step 3");
            DeleteElement.delete(modelInstance);
            //delete one incoming and one outgoing flow
            Delete121Gateway.delete(modelInstance);
            // Delete Repeat Flow
            DeleteRepeatFlow.delete(modelInstance);

            //after 3 steps, we could get a process model
            //output process model
            File step3output= new File("models/step3output.bpmn");
            Bpmn.writeModelToFile(step3output, modelInstance);

            //step 4
            System.out.println("*************************");
            System.out.println("step 4");
            File file = new File("models/processModel.bpmn");
            Process process = BPMN2Reader.parse(step3output);
            BpmnModelInstance modelInstance1 = Bpmn.readModelFromFile(step3output);
            Map<String, MyDataObject> datamap = DataObjectStore.storeinname(modelInstance1);
            process.setName("process2json");
            Restructurer str = new Restructurer(process);
            if (str.perform()) {
                try {
                    //get json file
                    String filename = "models/finalResult.json";
                    PrintStream out = new PrintStream(filename);
                    JSONObject json = de.hpi.bpt.process.serialize.Process2JSON.convert(str.proc);
                    DataObjectAddToJSON.addDataObject(json,datamap);
                    out.print(json);
                    out.close();
                } catch (FileNotFoundException var5) {
                    var5.printStackTrace();
                }
            } else {
                System.out.println("Model cannot be restructured");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
