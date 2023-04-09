package Step4_Well_Structure;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;
import org.camunda.bpm.model.xml.instance.DomElement;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DataObjectStore {

    public DataObjectStore(){

    }
    public static Map<String,MyDataObject> storeinname(BpmnModelInstance modelInstance){
//        BpmnModelInstance modelInstance = Bpmn.readModelFromFile(file);
        Map<String,MyDataObject> n2do = new HashMap<>();
        Map<String,MyDataObject> id2do = new HashMap<>();

        // Extract data objects from DataObject elements


        for (DataObjectReference dataObjectReference: modelInstance.getModelElementsByType(DataObjectReference.class)){
            String dataOjectId = dataObjectReference.getAttributeValue("dataObjectRef");
            String dataObjectRefId = dataObjectReference.getId();
            String dataObjectName = dataObjectReference.getName();

            MyDataObject myDataObject = new MyDataObject(dataOjectId, dataObjectRefId, dataObjectName);
            id2do.put(dataObjectRefId,myDataObject);
            n2do.put(dataObjectName,myDataObject);
        }


//        System.out.println(n2do);
//        System.out.println(id2do);

        // Extract data objects from DataInputAssociation and DataOutputAssociation elements associated with tasks
        for (Task task : modelInstance.getModelElementsByType(Task.class)) {
//            System.out.println( task);
            for (DataInputAssociation dataInputAssociation : task.getDataInputAssociations()) {
//                System.out.println(dataInputAssociation);
//                String sourceReference = dataInputAssociation.getAttributeValue("sourceRef");
//                System.out.println("2222222222222222222222222222" + dataInputAssociation.getDomElement().getChildElements());
                for(DomElement ref : dataInputAssociation.getDomElement().getChildElements()){
                    if (id2do.containsKey(ref.getTextContent())){
                        id2do.get(ref.getTextContent()).setSourceRef(task.getName());
//                    id2do.get(sourceReference).setSourceRef(task.getId());
                        n2do.get(id2do.get(ref.getTextContent()).getDoName()).setSourceRef(task.getName());
                    }
                }


            }
            for (DataOutputAssociation dataOutputAssociation : task.getDataOutputAssociations()) {
                for (DomElement ref : dataOutputAssociation.getDomElement().getChildElements()){
                    if (id2do.containsKey(ref.getTextContent())){
                        id2do.get(ref.getTextContent()).setTargetRef(task.getName());
                        n2do.get(id2do.get(ref.getTextContent()).getDoName()).setTargetRef(task.getName());
//                    id2do.get(sourceReference).setSourceRef(task.getId());
                    }
                }
//                String sourceReference = dataOutputAssociation.getSource().getAttributeValue("sourceRef");
//                System.out.println(dataOutputAssociation);
//                String targetReference = dataOutputAssociation.getAttributeValue("targetRef");
//                System.out.println("1111111111111111111" + dataOutputAssociation.getTextContent());

            }
        }
//        System.out.println(n2do);
//        System.out.println(id2do);
        return id2do;

//        return dataObjects;

    }


    public static Map<String,MyDataObject> storeinid(File file){
        BpmnModelInstance modelInstance = Bpmn.readModelFromFile(file);
        //initial a map for store data object
        //key is the ref id, value is the data object
        Map<String,MyDataObject> id2do = new HashMap<>();

        // Extract data objects from DataObject elements
        for (DataObjectReference dataObjectReference: modelInstance.getModelElementsByType(DataObjectReference.class)){
            //for each data object get the id, ref id and name
            String dataOjectId = dataObjectReference.getAttributeValue("dataObjectRef");
            String dataObjectRefId = dataObjectReference.getId();
            String dataObjectName = dataObjectReference.getName();
            //creat a new data object with the attributes get before
            MyDataObject myDataObject = new MyDataObject(dataOjectId, dataObjectRefId, dataObjectName);
            //put data object into the map, set ref id as key
            id2do.put(dataObjectRefId,myDataObject);
//            n2do.put(dataObjectName,myDataObject);
        }
//        System.out.println(n2do);
//        System.out.println(id2do);

        // Extract data objects from DataInputAssociation and DataOutputAssociation elements associated with tasks
        for (Task task : modelInstance.getModelElementsByType(Task.class)) {
//            System.out.println( task);
            for (DataInputAssociation dataInputAssociation : task.getDataInputAssociations()) {
//               String sourceReference = dataInputAssociation.getAttributeValue("sourceRef");
//                System.out.println("2222222222222222222222222222" + dataInputAssociation.getDomElement().getChildElements());
                for(DomElement ref : dataInputAssociation.getDomElement().getChildElements()){
                    if (id2do.containsKey(ref.getTextContent())){
                        id2do.get(ref.getTextContent()).setSourceRef(task.getId());
//                    id2do.get(sourceReference).setSourceRef(task.getId());
//                        .get(id2do.get(ref.getTextContent()).getDoName()).setSourceRef(task.getName());
                    }
                }


            }
            for (DataOutputAssociation dataOutputAssociation : task.getDataOutputAssociations()) {
                for (DomElement ref : dataOutputAssociation.getDomElement().getChildElements()){
                    if (id2do.containsKey(ref.getTextContent())){
                        id2do.get(ref.getTextContent()).setTargetRef(task.getId());
//                        n2do.get(id2do.get(ref.getTextContent()).getDoName()).setTargetRef(task.getName());
//                    id2do.get(sourceReference).setSourceRef(task.getId());
                    }
                }

            }
        }

        return id2do;
    }

}


