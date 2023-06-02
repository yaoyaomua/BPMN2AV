package Step3_Delete_Element;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class DataTextState {

    public DataTextState() {
    }

    public static HashMap<String,String> getText(BpmnModelInstance modelInstance){
        HashMap<String,String> TextState = new HashMap<>();
        for (DataObjectReference dataObjectReference : modelInstance.getModelElementsByType(DataObjectReference.class)){
            String DataId = dataObjectReference.getId();
            TextState.put(DataId,null);
        }
        for (Association association : modelInstance.getModelElementsByType(Association.class)){
            if (TextState.containsKey(association.getSource().getId())){
                TextState.put(association.getSource().getId(),association.getId());
            }
        }
        return TextState;
    }

    public static HashSet<String> getAssociatedDataObject(BpmnModelInstance modelInstance,String artifact){
        HashSet<String> datas = new HashSet<>();
        for (DataInputAssociation dataInputAssociation : modelInstance.getModelElementsByType(DataInputAssociation.class)){
            for (ItemAwareElement itemAwareElement: dataInputAssociation.getSources()){
//                    System.out.println(itemAwareElement.getId());
                if (!datas.contains(itemAwareElement.getId())){
                    datas.add(itemAwareElement.getId());
                }
            }
        }
        for (DataOutputAssociation dataOutputAssociation : modelInstance.getModelElementsByType(DataOutputAssociation.class)){
            if (!datas.contains(dataOutputAssociation.getTarget().getId())){
                datas.add(dataOutputAssociation.getTarget().getId());
            }
        }

        return datas;
    }
}
