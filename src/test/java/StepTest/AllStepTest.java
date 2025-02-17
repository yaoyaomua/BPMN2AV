package StepTest;

import AllSteps.ArtifactView;
import Step1_Delete_Pool.AddExclusiveGatewayForEndEvent;
import Step1_Delete_Pool.DeletePool;
import Step1_Delete_Pool.MergeProcess;
import Step2_Flow_Transform.AddAndGateway;
import Step2_Flow_Transform.AddSequenceFlow;
import Step3_Delete_Element.*;
import Step4_Well_Structure.*;
import Step4_Well_Structure.BPStruct.BPStruct;
import de.hpi.bpt.process.Process;
import ee.ut.bpstruct2.Restructurer;
import ee.ut.bpstruct2.util.BPMN2Reader;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.Artifact;
import org.camunda.bpm.model.bpmn.instance.MessageFlow;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.json.JSONObject;
import org.junit.Test;

import java.io.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;

public class AllStepTest {

    @Test
    public void DemoTest1(){
        try {
            String filePath = "models/demo/book.bpmn";

            BpmnModelInstance modelInstance;

            try (InputStream inputStream = new FileInputStream(new File(filePath))) {
                modelInstance = Bpmn.readModelFromStream(inputStream);
            }

            String artifact = "Container";
            ArtifactView.extract(modelInstance,artifact);

            File step3output= new File("models/demo/book_Container.bpmn");

            Bpmn.writeModelToFile(step3output, modelInstance);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void DemoTest2(){
        try {
            String filePath = "models/demo/book.bpmn";

            BpmnModelInstance modelInstance;

            try (InputStream inputStream = new FileInputStream(new File(filePath))) {
                modelInstance = Bpmn.readModelFromStream(inputStream);
            }

            String artifact = "Truck";
            ArtifactView.extract(modelInstance,artifact);

            File step3output= new File("models/demo/book_Truck.bpmn");

            Bpmn.writeModelToFile(step3output, modelInstance);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void DemoTest3(){
        try {
            String filePath = "models/demo/diagram19.bpmn";

            BpmnModelInstance modelInstance;

            try (InputStream inputStream = new FileInputStream(new File(filePath))) {
                modelInstance = Bpmn.readModelFromStream(inputStream);
            }

            String artifact = "Purchase order";
            ArtifactView.extract(modelInstance,artifact);

            File step3output= new File("models/demo/diagram19_PurchaseOrder.bpmn");

            Bpmn.writeModelToFile(step3output, modelInstance);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
