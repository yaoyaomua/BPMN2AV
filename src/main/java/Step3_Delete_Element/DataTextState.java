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
        HashMap<String,String> textData = getText(modelInstance);
        HashSet<String> datas = new HashSet<>();
        for (Task task : modelInstance.getModelElementsByType(Task.class)){
            for (DataInputAssociation dataInputAssociation : task.getDataInputAssociations()){
                for (ItemAwareElement itemAwareElement: dataInputAssociation.getSources()){
//                    System.out.println(itemAwareElement.getId());
                    if (!datas.contains(itemAwareElement.getId())){
                        datas.add(itemAwareElement.getId());
                    }
                }
            }
            for (DataOutputAssociation dataOutputAssociation : task.getDataOutputAssociations()){
                if (!datas.contains(dataOutputAssociation.getTarget().getId())){
                    datas.add(dataOutputAssociation.getTarget().getId());
                }
            }
        }
//        for ( Map.Entry<String, Tab> entry : hash.entrySet()) {
//            String key = entry.getKey();
//            Tab tab = entry.getValue();
//            // do something with key and/or tab
//        }
        System.out.println("******delete no state data object*****");
        for (HashMap.Entry<String,String> entry :  textData.entrySet()){
            if (entry.getValue() == null){
//                System.out.println(entry.getKey());
                DataObjectReference dataObjectReference = modelInstance.getModelElementById(entry.getKey());
                if (!dataObjectReference.getName().equals(artifact)){
//                    System.out.println("add this one");
                    datas.add(entry.getKey());
                }
            }
        }
//        System.out.println(datas.toString());
        return datas;
    }
}
