package Step1_Delete_Pool;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.LoopCharacteristics;
import org.camunda.bpm.model.bpmn.instance.MultiInstanceLoopCharacteristics;
import org.camunda.bpm.model.bpmn.instance.SubProcess;

import java.util.HashMap;

public class SubProcessSaveLoop {
    public SubProcessSaveLoop() {
    }

    public static HashMap<SubProcess, LoopCharacteristics> save(BpmnModelInstance modelInstance){
        HashMap<SubProcess, LoopCharacteristics> sub= new HashMap<>();
        for (SubProcess subProcess : modelInstance.getModelElementsByType(SubProcess.class)){
            if (subProcess.getLoopCharacteristics() != null){
                System.out.println(subProcess.getName());
                sub.put(subProcess, subProcess.getLoopCharacteristics());
                subProcess.removeChildElement(subProcess.getLoopCharacteristics());
            }
        }

//        for (LoopCharacteristics multiInstanceLoopCharacteristics : modelInstance.getModelElementsByType(LoopCharacteristics.class)){
//            System.out.println(multiInstanceLoopCharacteristics.getParentElement());
//
//            if (multiInstanceLoopCharacteristics.getParentElement() instanceof SubProcess){
//                sub.put(((SubProcess) multiInstanceLoopCharacteristics.getParentElement()), multiInstanceLoopCharacteristics);
//                multiInstanceLoopCharacteristics.getParentElement().removeChildElement(multiInstanceLoopCharacteristics);
//            }
//        }
        return sub;
    }
//
//    public static HashMap<SubProcess, >

    public static void read(BpmnModelInstance modelInstance, HashMap<SubProcess, LoopCharacteristics> sub){
        for (SubProcess subProcess : modelInstance.getModelElementsByType(SubProcess.class)){
            if (sub.containsKey(subProcess)){
                subProcess.setLoopCharacteristics(sub.get(subProcess));
            }
        }
    }

//    public static HashMap<SubProcess, MultiInstanceLoopCharacteristics> save1(BpmnModelInstance modelInstance){
//        HashMap<SubProcess, MultiInstanceLoopCharacteristics> sub= new HashMap<>();
//        for (MultiInstanceLoopCharacteristics multiInstanceLoopCharacteristics : modelInstance.getModelElementsByType(MultiInstanceLoopCharacteristics.class)){
//            if (multiInstanceLoopCharacteristics.getParentElement() instanceof SubProcess){
//                sub.put(((SubProcess) multiInstanceLoopCharacteristics.getParentElement()), multiInstanceLoopCharacteristics);
//                multiInstanceLoopCharacteristics.getParentElement().removeChildElement(multiInstanceLoopCharacteristics);
//            }
//        }
//        return sub;
//    }
//
//    public static void read1(BpmnModelInstance modelInstance, HashMap<SubProcess, MultiInstanceLoopCharacteristics> sub){
//        for (SubProcess subProcess : modelInstance.getModelElementsByType(SubProcess.class)){
//            if (sub.containsKey(subProcess)){
//                subProcess.setLoopCharacteristics(sub.get(subProcess));
//            }
//        }
//    }
}
