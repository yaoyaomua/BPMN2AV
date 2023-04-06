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



    public static Map<String,MyDataObject> store(File file){
        BpmnModelInstance modelInstance = Bpmn.readModelFromFile(file);
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

//        for (DataObject dataObject : modelInstance.getModelElementsByType(DataObject.class)) {
//            String dataObjectId = dataObject.getId();
////            String dataObjectReference = dataObject.getAttributeValue("camunda:dataObjectRef");
////            String dataObjectName = dataObject.getName();
////            String sourceReference = null;
////            String targetReference = null;
////            Map<String, String> dataObjectInfo = new HashMap<>();
////            dataObjectInfo.put("reference", dataObjectReference);
////            dataObjectInfo.put("name", dataObjectName);
////            dataObjectInfo.put("sourceReference", sourceReference);
////            dataObjectInfo.put("targetReference", targetReference);
////
////            dataObjects.put(dataObjectId, dataObjectInfo);
//
//        }

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



//                System.out.println(dataInputAssociation.getAttributeValue("sourceRef"));
//                if (id2do.containsKey(sourceReference)){
//                    id2do.get(sourceReference).setSourceRef(task.getName());
////                    id2do.get(sourceReference).setSourceRef(task.getId());
//                    n2do.get(id2do.get(sourceReference).getDoName()).setSourceRef(task.getName());
//                }
//                String targetReference = dataInputAssociation.getTarget().getAttributeValue("targetRef");
//                if (dataObjects.containsKey(targetReference)) {
//                    Map<String, String> dataObjectInfo = dataObjects.get(targetReference);
//                    dataObjectInfo.put("sourceReference", sourceReference);
//                    dataObjectInfo.put("targetReference", targetReference);
//                }
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

//                String targetRef = null;
//                DataOutput dataOutput = (DataOutput) dataOutputAssociation.getParentElement().getParentElement().getParentElement();
//                if (dataOutput instanceof InputOutputSpecification) {
//                    InputOutputSpecification inputOutputSpecification = (InputOutputSpecification) dataOutput;
//                    List<DataOutput> dataOutputs = inputOutputSpecification.getDataOutputs();
//                    for (DataOutput output : dataOutputs) {
//                        if (output.getId().equals(dataOutputAssociation.getTarget().getAttributeValue("targetRef"))) {
//                            targetRef = output.getName();
//                            break;
//                        }
//                    }
//                }


//                if (dataObjects.containsKey(sourceReference)) {
//                    Map<String, String> dataObjectInfo = dataObjects.get(sourceReference);
//                    dataObjectInfo.put("sourceReference", sourceReference);
//                    dataObjectInfo.put("targetReference", targetReference);
//                }
            }
        }
//        System.out.println(n2do);
//        System.out.println(id2do);
        return id2do;

//        return dataObjects;

    }
}



//import org.camunda.bpm.model.bpmn.Bpmn;
//        import org.camunda.bpm.model.bpmn.BpmnModelInstance;
//        import org.camunda.bpm.model.bpmn.instance.DataObject;
//        import org.camunda.bpm.model.xml.instance.ModelElementInstance;
//        import java.io.FileWriter;
//        import java.io.IOException;
//        import java.util.HashMap;
//        import java.util.Map;
//
//public class BPMNDataObjectExtractor {
//
//    public static void main(String[] args) {
//        String bpmnFilePath = "path/to/your/bpmn/file.bpmn";
//        Map<String, Map<String, String>> dataObjects = extractDataObjects(bpmnFilePath);
//        writeDataObjectsToJson(dataObjects, "data_objects.json");
//    }
//
//    public static Map<String, Map<String, String>> extractDataObjects(String bpmnFilePath) {
//        BpmnModelInstance modelInstance = Bpmn.readModelFromFile(bpmnFilePath);
//        Map<String, Map<String, String>> dataObjects = new HashMap<>();
//
//        for (DataObject dataObject : modelInstance.getModelElementsByType(DataObject.class)) {
//            String dataObjectId = dataObject.getId();
//            String dataObjectReference = dataObject.getAttributeValue("camunda:dataObjectReference");
//            String dataObjectName = dataObject.getName();
//            String sourceReference = null;
//            String targetReference = null;
//
//            ModelElementInstance sourceRef = dataObject.getUniqueChildElementByType(DataObject.class);
//            if (sourceRef != null) {
//                sourceReference = sourceRef.getAttributeValue("sourceRef");
//                targetReference = sourceRef.getAttributeValue("targetRef");
//            }
//
//            Map<String, String> dataObjectInfo = new HashMap<>();
//            dataObjectInfo.put("reference", dataObjectReference);
//            dataObjectInfo.put("name", dataObjectName);
//            dataObjectInfo.put("sourceReference", sourceReference);
//            dataObjectInfo.put("targetReference", targetReference);
//
//            dataObjects.put(dataObjectId, dataObjectInfo);
//        }
//
//        return dataObjects;
//    }
//
//    public static void writeDataObjectsToJson(Map<String, Map<String, String>> dataObjects, String jsonFilePath) {
//        try (FileWriter fileWriter = new FileWriter(jsonFilePath)) {
//            Gson gson = new Gson();
//            gson.toJson(dataObjects, fileWriter);
//            System.out.println("Data objects information has been written to " + jsonFilePath);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}
//
//import org.camunda.bpm.model.bpmn.Bpmn;
//        import org.camunda.bpm.model.bpmn.BpmnModelInstance;
//        import org.camunda.bpm.model.bpmn.instance.*;
//
//        import java.util.HashMap;
//        import java.util.Map;
//
//public class BPMNDataObjectExtractor {
//
//    public static void main(String[] args) {
//        String bpmnFilePath = "path/to/your/bpmn/file.bpmn";
//        Map<String, Map<String, String>> dataObjects = extractDataObjects(bpmnFilePath);
//        // Process the extracted data objects as needed
//        System.out.println(dataObjects);
//    }
//
//    public static Map<String, Map<String, String>> extractDataObjects(String bpmnFilePath) {
//        BpmnModelInstance modelInstance = Bpmn.readModelFromFile(bpmnFilePath);
//        Map<String, Map<String, String>> dataObjects = new HashMap<>();
//
//        // Extract data objects from DataObject elements
//        for (DataObject dataObject : modelInstance.getModelElementsByType(DataObject.class)) {
//            String dataObjectId = dataObject.getId();
//            String dataObjectReference = dataObject.getAttributeValue("camunda:dataObjectRef");
//            String dataObjectName = dataObject.getName();
//            String sourceReference = null;
//            String targetReference = null;
//
//            Map<String, String> dataObjectInfo = new HashMap<>();
//            dataObjectInfo.put("reference", dataObjectReference);
//            dataObjectInfo.put("name", dataObjectName);
//            dataObjectInfo.put("sourceReference", sourceReference);
//            dataObjectInfo.put("targetReference", targetReference);
//
//            dataObjects.put(dataObjectId, dataObjectInfo);
//        }
//
//        // Extract data objects from DataInputAssociation and DataOutputAssociation elements associated with tasks
//        for (Task task : modelInstance.getModelElementsByType(Task.class)) {
//            for (DataInputAssociation dataInputAssociation : task.getDataInputAssociations()) {
//                String sourceReference = dataInputAssociation.getSource().getAttributeValue("sourceRef");
//                String targetReference = dataInputAssociation.getTarget().getAttributeValue("targetRef");
//                if (dataObjects.containsKey(targetReference)) {
//                    Map<String, String> dataObjectInfo = dataObjects.get(targetReference);
//                    dataObjectInfo.put("sourceReference", sourceReference);
//                    dataObjectInfo.put("targetReference", targetReference);
//                }
//            }
//
//            for (DataOutputAssociation dataOutputAssociation : task.getDataOutputAssociations()) {
//                String sourceReference = dataOutputAssociation.getSource().getAttributeValue("sourceRef");
//                String targetReference = dataOutputAssociation.getTarget().getAttributeValue("targetRef");
//                if (dataObjects.containsKey(sourceReference)) {
//                    Map<String, String> dataObjectInfo = dataObjects.get(sourceReference);
//                    dataObjectInfo.put("sourceReference", sourceReference);
//                    dataObjectInfo.put("targetReference", targetReference);
//                }
//            }
//        }
//
//        return dataObjects;
//    }
//}

// Assuming you have a Task object called "task"

//    // Get the outgoing data output associations of the task
//    List<DataOutputAssociation> dataOutputAssociations = task.getDataOutputAssociations();
//
//// Iterate through the data output associations
//for (DataOutputAssociation dataOutputAssociation : dataOutputAssociations) {
//        // Get the target reference value
//        String targetRef = null;
//        DataOutput dataOutput = dataOutputAssociation.getParentElement().getParentElement().getParentElement();
//        if (dataOutput instanceof InputOutputSpecification) {
//        InputOutputSpecification inputOutputSpecification = (InputOutputSpecification) dataOutput;
//        List<DataOutput> dataOutputs = inputOutputSpecification.getDataOutputs();
//        for (DataOutput output : dataOutputs) {
//        if (output.getId().equals(dataOutputAssociation.getTarget().getAttributeValue("targetRef"))) {
//        targetRef = output.getName();
//        break;
//        }
//        }
//        }
//        // Print the target reference value
//        System.out.println("TargetRef value: " + targetRef);
//        }
