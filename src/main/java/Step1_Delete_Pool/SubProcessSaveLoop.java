package Step1_Delete_Pool;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.LoopCharacteristics;
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
}
