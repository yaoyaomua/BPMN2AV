package Add_Data_Object;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;

import java.util.ArrayList;
import java.util.Collection;


public class AddDataObjectWithoutState {
    public static void add(BpmnModelInstance modelInstance,String artifactName)
    {
        Collection<Process> processes = modelInstance.getModelElementsByType(Process.class);
        Collection<String> bindActivity = new ArrayList<>();
        for (Process process : processes) {
            BaseElement baseElement = null;
            for (BaseElement element : process.getChildElementsByType(BaseElement.class)) {
                Collection<DataInputAssociation> dataInputAssociations = new ArrayList<>();
                Collection<DataOutputAssociation> dataOutputAssociations = new ArrayList<>();
                if (element instanceof Task) {
                    Task task = modelInstance.getModelElementById(element.getId());
                    dataInputAssociations = task.getDataInputAssociations();
                    dataOutputAssociations = task.getDataOutputAssociations();
                }
                else if (element instanceof BoundaryEvent)
                {
                    BoundaryEvent boundaryEvent = modelInstance.getModelElementById(element.getId());
                    dataOutputAssociations = boundaryEvent.getDataOutputAssociations();
                }
                else if (element instanceof StartEvent)
                {
                    StartEvent startEvent = modelInstance.getModelElementById(element.getId());
                    dataOutputAssociations = startEvent.getDataOutputAssociations();
                }
                else if (element instanceof EndEvent)
                {
                    EndEvent endEvent = modelInstance.getModelElementById(element.getId());
                    dataInputAssociations = endEvent.getDataInputAssociations();
                }
                else if (element instanceof IntermediateThrowEvent)
                {
                    IntermediateThrowEvent intermediateThrowEvent = modelInstance.getModelElementById(element.getId());
                    dataInputAssociations = intermediateThrowEvent.getDataInputAssociations();

                }
                else if (element instanceof IntermediateCatchEvent)
                {
                    IntermediateCatchEvent intermediateCatchEvent = modelInstance.getModelElementById(element.getId());
                    dataOutputAssociations = intermediateCatchEvent.getDataOutputAssociations();
                }
                if (dataInputAssociations != null && !dataInputAssociations.isEmpty()) {
                    for (DataInputAssociation dataInputAssociation: dataInputAssociations)
                    {
                        System.out.println(dataInputAssociation.getSources().iterator().next().getId());
                        DataObjectReference dataObjectReference = modelInstance.getModelElementById(dataInputAssociation.getSources().iterator().next().getId());
                        if (dataObjectReference.getName().equals(artifactName))
                        {
                            baseElement = element;
                            break;
                        }
                    }
                    if(baseElement != null)
                    {
                        break;
                    }
                }
                if (dataOutputAssociations != null && !dataOutputAssociations.isEmpty()) {
                    for (DataOutputAssociation dataOutputAssociation: dataOutputAssociations)
                    {
                        System.out.println(dataOutputAssociation.getTarget().getId());
                        DataObjectReference dataObjectReference = modelInstance.getModelElementById(dataOutputAssociation.getTarget().getId());
                        if (dataObjectReference.getName().equals(artifactName))
                        {
                            baseElement = element;
                            break;
                        }
                    }
                    if(baseElement != null)
                    {
                        break;
                    }
                }

            }
            if (baseElement != null) {
                bindActivity.add(baseElement.getId());
                System.out.println("First element with DataObject in Process " + process.getId() + ": " + baseElement.getId() );
            }
        }

    }
}
