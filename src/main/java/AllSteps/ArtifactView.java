package AllSteps;

import Step1_Delete_Pool.*;
import Step2_Flow_Transform.AddAndGateway;
import Step2_Flow_Transform.AddSequenceFlow;
import Step3_Delete_Element.*;
import Step4_Well_Structure.*;
import Step4_Well_Structure.BPStruct.BPStruct;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.LoopCharacteristics;
import org.camunda.bpm.model.bpmn.instance.MessageFlow;
import org.camunda.bpm.model.bpmn.instance.SubProcess;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnEdge;
import org.jdom.JDOMException;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

public class ArtifactView {


    public ArtifactView() {
    }

    public static void extract(BpmnModelInstance modelInstance, String artifact) throws IOException, JDOMException {
        Collection<MessageFlow> messageflows;
        //HashMap<SubProcess, LoopCharacteristics> sub = SubProcessSaveLoop.save(modelInstance);
        BpmnModelInstance initialModelInstance = modelInstance;
        //step 1 delete pool and store message flows
        System.out.println("************************************");
        System.out.println("delete pool start:");
        MergeLane.merge(modelInstance);
        DeletePool.deleteEmpty(modelInstance);

        //Handle multiple end event situation, prepare for add super start and super end event
        AddExclusiveGatewayForEndEvent.add(modelInstance);
        //delete pool function return a collection, contains all the message flows
        messageflows = DeletePool.delete(modelInstance);
        //Merge the process
        //Delete all process tags and keep only the first one
        System.out.println("************************************");
        System.out.println("merge process start:");
        MergeProcess.merge(modelInstance);

        //step 2 transform message flow to sequence flow
        System.out.println("*************************************");
        System.out.println("transform message flow start: ");
        // Covert Message Flow to Sequence Flow
        AddSequenceFlow.add(modelInstance,messageflows);
        // Add And-GateWay
        System.out.println("*************************************");
        System.out.println("add and gateways start: ");
        //after this step, each event or activity only has one incoming flow and one outgoing flow
        AddAndGateway.add(modelInstance);

        //step 3 handle events
        System.out.println("**************************************");
        System.out.println("add super start and super end event start:");
        //this step need to recode which start and end are we add, so that if the start or end is irrelevant, will be remained
        HashSet<String> addedEvent = RecordEvent.record(modelInstance);

//        step 3 delete elements
        System.out.println("*************************************");
        System.out.println("delete irrelevant boundary events start:");

        DeleteBoundaryEvent.delete(modelInstance,artifact,addedEvent);


        System.out.println("*************************************");
        System.out.println("delete Task start:");
        DeleteTask.delete(modelInstance,artifact);


        System.out.println("*************************************");
        System.out.println("delete event start:");
        DeleteEvent.delete(modelInstance,artifact, addedEvent);


        System.out.println("*************************");
        System.out.println("delete data object start:");
        DeleteDataObject.delete(modelInstance,artifact);


        System.out.println("*************************");
        System.out.println("delete empty sub process start:");
        DeleteEmptySubprocess.delete(modelInstance,addedEvent);

        System.out.println("*************************");
        System.out.println("delete data object start:");
        DeleteDataObject.delete(modelInstance,artifact);

        System.out.println("*************************");
        System.out.println("manage flows start:");
        FlowManage.manage(modelInstance);
//        DeleteRepeatFlow.delete(modelInstance);
//        Delete121Gateway.delete(modelInstance);
//        DeleteParalleGatewaySequenceFlow.delete(modelInstance);
//        DeleteLoop.loop(modelInstance);


//        System.out.println("*************************");
//        System.out.println("Check if the BPStruct is required:");
//        Boolean isRuquired = CheckIfBPStructRequired.check(modelInstance,initialModelInstance);
//
//        System.out.println("*************************");
//        System.out.println("BPStruct:");
//        if (isRuquired) {
//            BPStruct.run(modelInstance);
//        }

        System.out.println("*************************");
        System.out.println("BPStruct:");
        BPStruct.run(modelInstance);


        //SubProcessSaveLoop.read(modelInstance,sub);
    }
}
