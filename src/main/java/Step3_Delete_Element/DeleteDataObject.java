package Step3_Delete_Element;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnEdge;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnShape;
import org.camunda.bpm.model.bpmn.instance.di.Diagram;
import org.camunda.bpm.model.bpmn.instance.di.DiagramElement;

import java.util.*;

public class DeleteDataObject {


    public DeleteDataObject() {
    }

    public static void delete(BpmnModelInstance modelInstance, String artifact){

        HashSet<String> datas = DataTextState.getAssociatedDataObject(modelInstance,artifact);

        HashMap<String,String> textData = DataTextState.getText(modelInstance);

        List<String> toDelete = new ArrayList<>();
//        List<String> tgtToDelete = new ArrayList<>();
//        List<String> srcToDelete = new ArrayList<>();

        for (DataObjectReference dataObjectReference : modelInstance.getModelElementsByType(DataObjectReference.class)){
            if (!datas.contains(dataObjectReference.getId())){
                toDelete.add(dataObjectReference.getId());
            }
        }
        //update association in activity and event
        for(DataOutputAssociation dataOutputAssociation : modelInstance.getModelElementsByType(DataOutputAssociation.class)){
            if (toDelete.contains(dataOutputAssociation.getTarget().getId())){
                DiagramElement outputDiagram = dataOutputAssociation.getDiagramElement();
                outputDiagram.getParentElement().removeChildElement(outputDiagram);
                dataOutputAssociation.getParentElement().removeChildElement(dataOutputAssociation);
            }
        }
        for (DataInputAssociation dataInputAssociation : modelInstance.getModelElementsByType(DataInputAssociation.class)){
            Collection<ItemAwareElement> sources = dataInputAssociation.getSources();
            for (ItemAwareElement itemAwareElement : sources){
                if (toDelete.contains(itemAwareElement.getId())){
                    sources.remove(itemAwareElement);
                }
            }
            if (sources.isEmpty()){
                DiagramElement inputDiagram = dataInputAssociation.getDiagramElement();
                inputDiagram.getParentElement().removeChildElement(inputDiagram);
                dataInputAssociation.getParentElement().removeChildElement(dataInputAssociation);
            }
        }

        for (String dataObjectRefID: toDelete){
            DataObjectReference dataObjectReference = modelInstance.getModelElementById(dataObjectRefID);
            DataObject dataObject = dataObjectReference.getDataObject();
            DiagramElement dataShape = dataObjectReference.getDiagramElement();
            dataObjectReference.getParentElement().removeChildElement(dataObjectReference);
            dataObject.getParentElement().removeChildElement(dataObject);
            dataShape.getParentElement().removeChildElement(dataShape);
            if (textData.get(dataObjectRefID) != null){
                Association association = modelInstance.getModelElementById(textData.get(dataObjectRefID));
                TextAnnotation textAnnotation = modelInstance.getModelElementById(association.getTarget().getId());
                DiagramElement textDiagram = textAnnotation.getDiagramElement();
                DiagramElement assoDiagram = association.getDiagramElement();
                assoDiagram.getParentElement().removeChildElement(assoDiagram);
                association.getParentElement().removeChildElement(association);
                textAnnotation.getParentElement().removeChildElement(textAnnotation);
                textDiagram.getParentElement().removeChildElement(textDiagram);
            }
        }

        for (BpmnEdge edge : modelInstance.getModelElementsByType(BpmnEdge.class)){
            if (edge.getBpmnElement() == null){
                edge.getParentElement().removeChildElement(edge);
            }
        }
    }
}
