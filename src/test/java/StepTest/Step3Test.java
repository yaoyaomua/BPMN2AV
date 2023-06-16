package StepTest;

import Step1_Delete_Pool.AddExclusiveGatewayForEndEvent;
import Step1_Delete_Pool.DeletePool;
import Step1_Delete_Pool.MergeLane;
import Step1_Delete_Pool.MergeProcess;
import Step2_Flow_Transform.AddAndGateway;
import Step2_Flow_Transform.AddSequenceFlow;
import Step3_Delete_Element.*;
import Step3_Delete_Element.DeleteEmptySubprocess;
import Step4_Well_Structure.Delete121Gateway;
import Step4_Well_Structure.DeleteRepeatFlow;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;
import org.junit.Test;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class Step3Test {

    @Test
    public void deleteElementTest(){
        try {
            BpmnModelInstance bpmnModelInstance = Bpmn.readModelFromFile(new File("models/Steps/step3_delete_task.bpmn"));
            for (DataObjectReference dataObjectReference : bpmnModelInstance.getModelElementsByType(DataObjectReference.class)){
                System.out.println(dataObjectReference.getId());
            }
            for (DataObject dataObject: bpmnModelInstance.getModelElementsByType(DataObject.class)){
                System.out.println(dataObject.getId());
            }
            for (Task task :bpmnModelInstance.getModelElementsByType(Task.class)){
                for (DataInputAssociation dataInputAssociation : task.getDataInputAssociations()){
                    for (ItemAwareElement itemAwareElement : dataInputAssociation.getSources()){
                        System.out.println(itemAwareElement.getId());
                    }
                }
            }


            DeleteTask.delete(bpmnModelInstance, "DATA1");
            File outputFile = new File("models/Steps/step3_delete_Task_result.bpmn");
            Bpmn.writeModelToFile(outputFile, bpmnModelInstance);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void deleteEventTest(){
        BpmnModelInstance bpmnModelInstance = Bpmn.readModelFromFile(new File("models/diagram (46).bpmn"));
//        DeleteEvent.delete(bpmnModelInstance, "DATA1");
        try {
            File outputFile = new File("models/Steps/step3_delete_Event_result.bpmn");
            Bpmn.writeModelToFile(outputFile, bpmnModelInstance);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void deleteDataObjectTest(){
        BpmnModelInstance bpmnModelInstance = Bpmn.readModelFromFile(new File("models/Steps/step3_delete_data_object.bpmn"));
        HashSet<String> set = DataTextState.getAssociatedDataObject(bpmnModelInstance,"DATA1");
        System.out.println(set.toString());
        DeleteDataObject.delete(bpmnModelInstance,"DATA1");
        try {
            File outputFile = new File("models/step3_delete_data_object_result.bpmn");
            Bpmn.writeModelToFile(outputFile, bpmnModelInstance);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    public void delete121GatewayTest(){
        try {
            BpmnModelInstance bpmnModelInstance = Bpmn.readModelFromFile(new File("models/Steps/step3_delete_121_gateway.bpmn"));
            Delete121Gateway.delete(bpmnModelInstance);
            File outputFile = new File("models/Steps/step3_delete_121_gateway_result.bpmn");
            Bpmn.writeModelToFile(outputFile, bpmnModelInstance);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void deleteRepeatedFlows(){
        try {
            BpmnModelInstance bpmnModelInstance = Bpmn.readModelFromFile(new File("models/Steps/step3_repeated_flows.bpmn"));
            DeleteRepeatFlow.delete(bpmnModelInstance);
            File outputFile = new File("models/Steps/step3_repeated_result.bpmn");
            Bpmn.writeModelToFile(outputFile, bpmnModelInstance);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }



    @Test
    public void AddSubEndStart(){
        BpmnModelInstance bpmnModelInstance = Bpmn.readModelFromFile(new File("models/Steps/step3_add_sub_start_end.bpmn"));
        AddSubProcessStartEndEvent.add(bpmnModelInstance);
        try {
            File outputFile = new File("models/Steps/step3_add_sub_start_end_result.bpmn");
            Bpmn.writeModelToFile(outputFile, bpmnModelInstance);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void AddSuperStart(){
        BpmnModelInstance bpmnModelInstance = Bpmn.readModelFromFile(new File("models/Steps/step3_add_super_start.bpmn"));
        AddSuperStartEndEvent.addStart(bpmnModelInstance);
        try {
            File outputFile = new File("models/Steps/step3_add_super_start_result.bpmn");
            Bpmn.writeModelToFile(outputFile, bpmnModelInstance);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void AddSuperEnd(){
        BpmnModelInstance bpmnModelInstance = Bpmn.readModelFromFile(new File("models/Steps/step3_add_super_end.bpmn"));
        AddSuperStartEndEvent.addEnd(bpmnModelInstance);
        try {
            File outputFile = new File("models/Steps/step3_add_super_end_result.bpmn");
            Bpmn.writeModelToFile(outputFile, bpmnModelInstance);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    public void deleteBoundaryEvent(){
        try {
            String artifact = "DATA1";
            BpmnModelInstance bpmnModelInstance = Bpmn.readModelFromFile(new File("models/Steps/step3_delete_boundary_event.bpmn"));
            Collection<MessageFlow> messageflows;

            //step 1 delete pool and store message flows
            System.out.println("************************************");
            System.out.println("delete pool start:");
            MergeLane.merge(bpmnModelInstance);
            //Handle multiple end event situation, prepare for add super start and super end event
            AddExclusiveGatewayForEndEvent.add(bpmnModelInstance);
            //delete pool function return a collection, contains all the message flows
            messageflows = DeletePool.delete(bpmnModelInstance);
            //Merge the process
            //Delete all process tags and keep only the first one
            System.out.println("************************************");
            System.out.println("merge process start:");
            MergeProcess.merge(bpmnModelInstance);

            //step 2 transform message flow to sequence flow
            System.out.println("*************************************");
            System.out.println("transform message flow start: ");
            // Covert Message Flow to Sequence Flow
            AddSequenceFlow.add(bpmnModelInstance,messageflows);
            // Add And-GateWay
            System.out.println("*************************************");
            System.out.println("add and gateways start: ");
            //after this step, each event or activity only has one incoming flow and one outgoing flow
            AddAndGateway.add(bpmnModelInstance);

            HashSet<String> addedEvent = RecordEvent.record(bpmnModelInstance);

            DeleteBoundaryEvent.delete(bpmnModelInstance,"DATA1",addedEvent);

            File outputFile = new File("models/Steps/step3_delete_boundary_event_result.bpmn");
            Bpmn.writeModelToFile(outputFile, bpmnModelInstance);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
