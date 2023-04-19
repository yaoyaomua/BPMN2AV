package Step3_Delete_Element;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.Association;
import org.camunda.bpm.model.bpmn.instance.DataObjectReference;
import org.camunda.bpm.model.bpmn.instance.TextAnnotation;

import java.util.HashMap;

public class DataTextState {

    public DataTextState() {
    }

    public static HashMap<String,Boolean> getText(BpmnModelInstance modelInstance){
        HashMap<String,Boolean> TextState = new HashMap<>();
        for (DataObjectReference dataObjectReference : modelInstance.getModelElementsByType(DataObjectReference.class)){
            String DataId = dataObjectReference.getId();
            TextState.put(DataId,false);
        }
        for (Association association : modelInstance.getModelElementsByType(Association.class)){
            if (TextState.containsKey(association.getSource().getId())){
                TextState.put(association.getSource().getId(),true);
            }
        }
        return TextState;
    }
}
