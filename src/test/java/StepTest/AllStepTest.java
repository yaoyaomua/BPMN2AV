package StepTest;

import Step1_Delete_Pool.AddExclusiveGatewayForEndEvent;
import Step1_Delete_Pool.DeletePool;
import Step1_Delete_Pool.MergeProcess;
import Step2_Flow_Transform.AddAndGateway;
import Step2_Flow_Transform.AddSequenceFlow;
import Step3_Delete_Element.*;
import Step4_Well_Structure.*;
import de.hpi.bpt.process.Process;
import ee.ut.bpstruct2.Restructurer;
import ee.ut.bpstruct2.util.BPMN2Reader;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
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
    public void stepsTest(){
        try {
            //Read bpmn file
            String filePath = "models/Steps/bookdiagram.bpmn";
            BpmnModelInstance modelInstance;
            try (InputStream inputStream = new FileInputStream(new File(filePath))) {
                modelInstance = Bpmn.readModelFromStream(inputStream);
            }
//            System.out.println("please choose artifact: ");
//            Scanner scanner = new Scanner(System.in);
            String artifact = "Truck";

            Collection<MessageFlow> messageflows;

            //step 1 delete pool and store message flows
            System.out.println("************************************");
            System.out.println("delete pool start:");
            AddExclusiveGatewayForEndEvent.add(modelInstance);
            messageflows = DeletePool.delete(modelInstance);
            // Delete all process tags and keep only the first one
            System.out.println("************************************");
            System.out.println("merge process start:");
            MergeProcess.merge(modelInstance);

//            File step1output = new File("models/step1output.bpmn");
//            Bpmn.writeModelToFile(step1output, modelInstance);

            //step 2 transform message flow to sequence flow
            System.out.println("********************************");
            System.out.println("transform message flow start: ");
            // Covert Message Flow to Sequence Flow
            AddSequenceFlow.add(modelInstance,messageflows);
            // Add And-GateWay
            System.out.println("********************************");
            System.out.println("add and gateway start: ");
            AddAndGateway.add(modelInstance);
//            File step2output = new File("models/step2output.bpmn");
//            Bpmn.writeModelToFile(step2output, modelInstance);

            //step 3 handle events
            System.out.println("*************************");
            System.out.println("add start and end event start:");
            HashSet<String> addedEvent = RecordEvent.record(modelInstance);

            //step 4 delete elements
            System.out.println("*************************");
            System.out.println("delete boundary event start:");
            DeleteBoundaryEvent.delete(modelInstance,artifact);

//            File step2output = new File("models/step2output.bpmn");
//            Bpmn.writeModelToFile(step2output, modelInstance);

            System.out.println("*************************");
            System.out.println("delete Task start:");
            DeleteTask.delete(modelInstance,artifact);


            System.out.println("*************************");
            System.out.println("delete event start:");
            DeleteEvent.delete(modelInstance,artifact, addedEvent);


            System.out.println("*************************");
            System.out.println("delete data object start:");
            DeleteDataObject.delete(modelInstance,artifact);


            System.out.println("*************************");
            System.out.println("delete empty sub process start:");
            //delete one incoming and one outgoing flow
            DeleteEmptySubprocess.delete(modelInstance);


            System.out.println("*************************");
            System.out.println("delete repeated flow start:");
            DeleteRepeatFlow.delete(modelInstance);

//            System.out.println("*************************");
//            System.out.println("delete 1-1 gateway start:");
//            Delete121Gateway.delete(modelInstance);
//            File midoutput= new File("models/output1.bpmn");
//            Bpmn.writeModelToFile(midoutput, modelInstance);

//            System.out.println("*************************");
//            System.out.println("add subprocess event start:");
//            AddSubProcessStartEndEvent.add(modelInstance);
//            for (SequenceFlow sequenceFlow : modelInstance.getModelElementsByType(SequenceFlow.class)){
//                System.out.println(sequenceFlow.getId());
//                System.out.println(sequenceFlow.getSource().getId());
//                System.out.println(sequenceFlow.getSource().getName());
//                System.out.println(sequenceFlow.getTarget().getId());
//                System.out.println(sequenceFlow.getTarget().getName());
//            }
//            File midoutput= new File("models/output1.bpmn");
//            Bpmn.writeModelToFile(midoutput, modelInstance);





            File step3output= new File("models/output.bpmn");
            Bpmn.writeModelToFile(step3output, modelInstance);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
