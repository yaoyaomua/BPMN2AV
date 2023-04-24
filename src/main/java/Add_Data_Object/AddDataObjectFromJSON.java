package Add_Data_Object;

import Step2_Flow_Transform.CreateBPMNEdge;
import Step2_Flow_Transform.CreateBPMNShape;
import Step3_Delete_Element.Generate7ID;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnPlane;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnShape;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class AddDataObjectFromJSON {
    public static void add(BpmnModelInstance modelInstance, File file) throws JSONException {
        String jsonContent = ReadJson.readJsonFile(file.toString());
        System.out.println(jsonContent);
        JSONObject jsonObject = new JSONObject(jsonContent);
        JSONArray jsonArray = jsonObject.getJSONArray("DataObjects");
        for (int i = 0; i < jsonArray.length(); i ++){
            Collection<String> sourceList = new ArrayList<>();
            Collection<String> targetList = new ArrayList<>();
            String artifact = jsonArray.getJSONObject(i).getString("artifact");
            String state = jsonArray.getJSONObject(i).getString("state");
            System.out.println("##################");
            System.out.println(artifact);
            System.out.println(state);
            if (jsonArray.getJSONObject(i).getJSONArray("sourceRef") != null) {
                JSONArray sourceRef = jsonArray.getJSONObject(i).getJSONArray("sourceRef");
                for (int m = 0; m < sourceRef.length(); m ++) {
                    String sourceRef_name = sourceRef.getJSONObject(m).getString("name");
                    sourceList.add(sourceRef_name);
                    System.out.println(sourceRef_name);
                }
            }
            if (jsonArray.getJSONObject(i).getJSONArray("targetRef") != null) {
                JSONArray targetRef = jsonArray.getJSONObject(i).getJSONArray("targetRef");
                for (int n = 0; n < targetRef.length(); n ++) {
                    String targetRef_name = targetRef.getJSONObject(n).getString("name");
                    targetList.add(targetRef_name);
                    System.out.println(targetRef_name);
                }
            }
            addDataObject(modelInstance,artifact,state,sourceList,targetList);
        }
    }
    public static void addDataObject(BpmnModelInstance modelInstance, String artifact, String state, Collection<String> sourceList, Collection<String> targetList) {
        Process process = modelInstance.getModelElementsByType(Process.class).iterator().next();
        String newID;
        //DataObject
        DataObject dataObject = modelInstance.newInstance(DataObject.class);
        do {
            newID = Generate7ID.generate();
        } while (modelInstance.getModelElementById("myDataObject" + newID) != null);
        dataObject.setId("myDataObject" + newID);
        process.addChildElement(dataObject);
        //TextAnnotation
        TextAnnotation textAnnotation = modelInstance.newInstance(TextAnnotation.class);
        do {
            newID = Generate7ID.generate();
        } while (modelInstance.getModelElementById("myTextAnnotation" + newID) != null);
        textAnnotation.setId("myTextAnnotation" + newID);
        Text text = modelInstance.newInstance(Text.class);
        text.setTextContent(state);
        textAnnotation.setText(text);
        process.addChildElement(textAnnotation);
        //DataReference
        DataObjectReference dataObjectReference = modelInstance.newInstance(DataObjectReference.class);
        do {
            newID = Generate7ID.generate();
        } while (modelInstance.getModelElementById("myDataReference" + newID) != null);
        dataObjectReference.setId("myDataReference" + newID);
        dataObjectReference.setDataObject(dataObject);
        dataObjectReference.setName(artifact);
        process.addChildElement(dataObjectReference);
        //Association
        Association association = modelInstance.newInstance(Association.class);
        do {
            newID = Generate7ID.generate();
        } while (modelInstance.getModelElementById("myAssociation" + newID) != null);
        association.setId("myAssociation" + newID);
        association.setSource(dataObjectReference);
        association.setTarget(textAnnotation);
        process.addChildElement(association);
        //Output Association
        for (String sourceName : sourceList) {
            Optional<Task> sourceTask = modelInstance.getModelElementsByType(Task.class).stream().filter(task -> task.getName().equals(sourceName)).findFirst();
            //Property
            Property property = modelInstance.newInstance(Property.class);
            do {
                newID = Generate7ID.generate();
            } while (modelInstance.getModelElementById("myProperty" + newID) != null);
            property.setId("myProperty" + newID);
            property.setName("__sourceRef__"+newID);
            sourceTask.get().addChildElement(property);
            sourceTask.get().getProperties().add(property);

            DataOutputAssociation outputAssociation = modelInstance.newInstance(DataOutputAssociation.class);
            do {
                newID = Generate7ID.generate();
            } while (modelInstance.getModelElementById("myOutputAssociation" + newID) != null);
            outputAssociation.setId("myOutputAssociation" + newID);
            outputAssociation.setTarget(dataObjectReference);
            outputAssociation.getSources().add(property);
            sourceTask.get().getDataOutputAssociations().add(outputAssociation);
            sourceTask.get().addChildElement(outputAssociation);
        }
        //  Input Association
        for (String targetName : targetList) {
            Optional<Task> targetTask = modelInstance.getModelElementsByType(Task.class).stream().filter(task -> task.getName().equals(targetName)).findFirst();
            //Property
            Property property =modelInstance.newInstance(Property.class);
            do {
                newID = Generate7ID.generate();
            } while (modelInstance.getModelElementById("myProperty" + newID) != null);
            property.setId("myProperty" + newID);
            property.setName("__targetRef__"+newID);
            targetTask.get().addChildElement(property);
            targetTask.get().getProperties().add(property);
            //Input Association
            DataInputAssociation inputAssociation = modelInstance.newInstance(DataInputAssociation.class);
            do {
                newID = Generate7ID.generate();
            } while (modelInstance.getModelElementById("myInputAssociation" + newID) != null);
            inputAssociation.setId("myInputAssociation" + newID);
            inputAssociation.getSources().add(dataObjectReference);
            inputAssociation.setTarget(property);
            targetTask.get().getDataInputAssociations().add(inputAssociation);
            targetTask.get().addChildElement(inputAssociation);
        }

        //Add diagramElement to BPMN

        // Get the BPMN diagram instance
        // ######## Diagram: DataReference ########
        Collection<DataObjectReference> count_dataObjectReference = modelInstance.getModelElementsByType(DataObjectReference.class);
        CreateBPMNShape.create(modelInstance,dataObjectReference.getId(),100.0+count_dataObjectReference.size()*200,40.0,36.0,50.0);
        // ######## Diagram: TextAnnotation ########
        CreateBPMNShape.create(modelInstance,textAnnotation.getId(),100.0+count_dataObjectReference.size()*200,0.0,100.0,30.0);
        // ######## Diagram: Association ########
        CreateBPMNEdge.create(modelInstance, association, 100.0+count_dataObjectReference.size()*200, 40.0, 100.0+count_dataObjectReference.size()*200, 0.0);
        // ######## Diagram: Output&&Input Association ########
        // output
        for (String sourceName : sourceList) {
            Optional<Task> sourceTask = modelInstance.getModelElementsByType(Task.class).stream().filter(task -> task.getName().equals(sourceName)).findFirst();
            BaseElement refTask = modelInstance.getModelElementById(sourceTask.get().getId());
            BpmnShape refTaskShape = modelInstance.getModelElementById(refTask.getDiagramElement().getId());
            Double refTaskX = refTaskShape.getBounds().getX();
            Double refTaskY = refTaskShape.getBounds().getY();
            Collection<DataOutputAssociation> outputAssociations = sourceTask.get().getDataOutputAssociations();
            for (DataOutputAssociation outputAssociation:outputAssociations)
            {
                CreateBPMNEdge.create(modelInstance, outputAssociation, refTaskX, refTaskY, 100.0+count_dataObjectReference.size()*200, 40.0);
            }
        }
        // input
        for (String targetName : targetList) {
            Optional<Task> targetTask = modelInstance.getModelElementsByType(Task.class).stream().filter(task -> task.getName().equals(targetName)).findFirst();
            BaseElement refTask = modelInstance.getModelElementById(targetTask.get().getId());
            BpmnShape refTaskShape = modelInstance.getModelElementById(refTask.getDiagramElement().getId());
            Double refTaskX = refTaskShape.getBounds().getX();
            Double refTaskY = refTaskShape.getBounds().getY();
            Collection<DataInputAssociation> inputAssociations = targetTask.get().getDataInputAssociations();
            for (DataInputAssociation inputAssociation:inputAssociations)
            {
                CreateBPMNEdge.create(modelInstance, inputAssociation, 100.0+count_dataObjectReference.size()*200, 40.0, refTaskX, refTaskY);
            }
        }


    }
}

