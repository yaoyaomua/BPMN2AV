package Step3_Delete_Element;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;

public class GenerateID {
    public GenerateID() {
    }
    public static String getID(String pre, BpmnModelInstance modelInstance){
        String backID;
        do {
            backID = Generate7ID.generate();
        }while ( modelInstance.getModelElementById(pre + backID) != null);
        return pre + backID;
    }
}
