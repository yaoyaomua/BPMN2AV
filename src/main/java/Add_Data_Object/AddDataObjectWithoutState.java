package Add_Data_Object;

import Step2_Flow_Transform.AddIncomingOrOutcoming;
import Step2_Flow_Transform.CreateBPMNEdge;
import Step2_Flow_Transform.CreateBPMNShape;
import Step2_Flow_Transform.GetBounds;
import Step3_Delete_Element.Generate7ID;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.dc.Bounds;

import java.util.*;


public class AddDataObjectWithoutState {
    public static void add(BpmnModelInstance modelInstance,String artifactName) {
        Collection<String> bindingElements = new ArrayList<>();
        bindingElements = bindingArtifact(modelInstance,artifactName);
        //unbindingArtifact(modelInstance,artifactName);

        // diagram for bindingArtifact
        for (String bindingElement : bindingElements)
        {
            String newID;
            SequenceFlow sequenceFlow = null;
            BaseElement baseElement = modelInstance.getModelElementById(bindingElement);
            if (baseElement instanceof Task)
            {
               Task element = modelInstance.getModelElementById(bindingElement);
               sequenceFlow = element.getIncoming().iterator().next();

            }

           // BaseElement flowElement =modelInstance.getModelElementById(bindingElement);
            while (!(baseElement instanceof Process))
            {
                baseElement = (BaseElement) baseElement.getParentElement();
            }
            Process process = (Process) baseElement;
            System.out.println(process.getId());
            IntermediateCatchEvent intermediateCatchEvent = modelInstance.newInstance(IntermediateCatchEvent.class);
            do {
                newID = Generate7ID.generate();
            }while(modelInstance.getModelElementById("myIntermediateCatchEvent_"+newID)!=null);
            intermediateCatchEvent.setId("myIntermediateCatchEvent_"+newID);
            process.addChildElement(intermediateCatchEvent);

            //add an sequence flow between activity and event
            SequenceFlow incoming = modelInstance.newInstance(SequenceFlow.class);
            do {
                newID = Generate7ID.generate();
            }while(modelInstance.getModelElementById("Flow_"+newID)!=null);
            incoming.setId("Flow"+newID);
            incoming.setTarget(intermediateCatchEvent);
            incoming.setSource(sequenceFlow.getSource());
            process.addChildElement(incoming);
            //set outgoing of the preelement
            AddIncomingOrOutcoming.AddOutgoingToElement(modelInstance, sequenceFlow.getSource().getId(),incoming);
            //set outgoing of the event
            intermediateCatchEvent.getIncoming().add(incoming);

            //
            SequenceFlow outgoing = modelInstance.newInstance(SequenceFlow.class);
            do {
                newID = Generate7ID.generate();
            }while(modelInstance.getModelElementById("Flow_"+newID)!=null);
            outgoing.setId("Flow"+newID);
            outgoing.setTarget(sequenceFlow.getTarget());
            outgoing.setSource(intermediateCatchEvent);
            process.addChildElement(outgoing);
            //set incoming of the preelement
            AddIncomingOrOutcoming.AddIncomingToElement(modelInstance, sequenceFlow.getTarget().getId(),outgoing);
            //set incoming of the event
            intermediateCatchEvent.getOutgoing().add(outgoing);


            //DataObject
            DataObject dataObject = modelInstance.newInstance(DataObject.class);
            do {
                newID = Generate7ID.generate();
            } while (modelInstance.getModelElementById("myDataObject" + newID) != null);
            dataObject.setId("myDataObject" + newID);
            process.addChildElement(dataObject);
            //DataReference
            DataObjectReference dataObjectReference = modelInstance.newInstance(DataObjectReference.class);
            do {
                newID = Generate7ID.generate();
            } while (modelInstance.getModelElementById("myDataReference" + newID) != null);
            dataObjectReference.setId("myDataReference" + newID);
            dataObjectReference.setDataObject(dataObject);
            dataObjectReference.setName(artifactName);
            process.addChildElement(dataObjectReference);
            //Output Association
            //Property
            Property property = modelInstance.newInstance(Property.class);
            do {
                newID = Generate7ID.generate();
            } while (modelInstance.getModelElementById("myProperty" + newID) != null);
            property.setId("myProperty" + newID);
            property.setName("__sourceRef__"+newID);
            intermediateCatchEvent.addChildElement(property);
            intermediateCatchEvent.getProperties().add(property);

            DataOutputAssociation outputAssociation = modelInstance.newInstance(DataOutputAssociation.class);
            do {
                newID = Generate7ID.generate();
            } while (modelInstance.getModelElementById("myOutputAssociation" + newID) != null);
            outputAssociation.setId("myOutputAssociation" + newID);
            outputAssociation.setTarget(dataObjectReference);
            outputAssociation.getSources().add(property);
            intermediateCatchEvent.getDataOutputAssociations().add(outputAssociation);
            intermediateCatchEvent.addChildElement(outputAssociation);


            // ######## diagram ############
            Bounds sourceBound = GetBounds.get(modelInstance,sequenceFlow.getSource().getId());
            Bounds targetBound = GetBounds.get(modelInstance,sequenceFlow.getTarget().getId());
            // ######## Diagram: interEvent ########
            CreateBPMNShape.create(modelInstance,intermediateCatchEvent.getId(),sourceBound.getX()+50,sourceBound.getY(),36.0,36.0);
            Bounds eventBound = GetBounds.get(modelInstance,intermediateCatchEvent.getId());
            // ######## Diagram: sequenceFlow ########
            CreateBPMNEdge.create(modelInstance, incoming, sourceBound.getX(),sourceBound.getY(),eventBound.getX(),eventBound.getY());
            CreateBPMNEdge.create(modelInstance, outgoing, eventBound.getX(),eventBound.getY(),targetBound.getX(),targetBound.getY());
            // ######## Diagram: DataReference ########
            CreateBPMNShape.create(modelInstance,dataObjectReference.getId(),eventBound.getX(),eventBound.getY()-50,36.0,50.0);
            Bounds dataReferenceBound = GetBounds.get(modelInstance,dataObjectReference.getId());
            // ######## Diagram: Output Association ########
            CreateBPMNEdge.create(modelInstance,outputAssociation, eventBound.getX(),eventBound.getY(),dataReferenceBound.getX(),dataReferenceBound.getY());
            // delete sequence flow
            modelInstance.getModelElementById(sequenceFlow.getSource().getId()).removeChildElement(sequenceFlow);
            modelInstance.getModelElementById(sequenceFlow.getTarget().getId()).removeChildElement(sequenceFlow);
            modelInstance.getModelElementById(sequenceFlow.getId()).getParentElement().removeChildElement(sequenceFlow);
        }
        // diagram for unbindingArtifact
    }
    public static Collection<String> bindingArtifact(BpmnModelInstance modelInstance,String artifactName) {
        Collection<Process> processes = modelInstance.getModelElementsByType(Process.class);
        Collection<String> bindElements = new ArrayList<>();
        // Binding
        for (Process process : processes) {
            BaseElement baseElement = null;
            Collection<BaseElement> baseElementList = new ArrayList<>();
            baseElementList=displayElementsByDiagramOrder(modelInstance,process);
            for (BaseElement element : baseElementList) {
                Collection<DataInputAssociation> dataInputAssociations = new ArrayList<>();
                Collection<DataOutputAssociation> dataOutputAssociations = new ArrayList<>();
                if (element instanceof Task) {
                    Task task = modelInstance.getModelElementById(element.getId());
                    dataInputAssociations = task.getDataInputAssociations();
                    dataOutputAssociations = task.getDataOutputAssociations();
                } else if (element instanceof BoundaryEvent) {
                    BoundaryEvent boundaryEvent = modelInstance.getModelElementById(element.getId());
                    dataOutputAssociations = boundaryEvent.getDataOutputAssociations();
                } else if (element instanceof StartEvent) {
                    StartEvent startEvent = modelInstance.getModelElementById(element.getId());
                    dataOutputAssociations = startEvent.getDataOutputAssociations();
                } else if (element instanceof EndEvent) {
                    EndEvent endEvent = modelInstance.getModelElementById(element.getId());
                    dataInputAssociations = endEvent.getDataInputAssociations();
                } else if (element instanceof IntermediateThrowEvent) {
                    IntermediateThrowEvent intermediateThrowEvent = modelInstance.getModelElementById(element.getId());
                    dataInputAssociations = intermediateThrowEvent.getDataInputAssociations();

                } else if (element instanceof IntermediateCatchEvent) {
                    IntermediateCatchEvent intermediateCatchEvent = modelInstance.getModelElementById(element.getId());
                    dataOutputAssociations = intermediateCatchEvent.getDataOutputAssociations();
                }
                if (dataInputAssociations != null && !dataInputAssociations.isEmpty()) {
                    for (DataInputAssociation dataInputAssociation : dataInputAssociations) {
                        DataObjectReference dataObjectReference = modelInstance.getModelElementById(dataInputAssociation.getSources().iterator().next().getId());
                        if (dataObjectReference.getName().equals(artifactName)) {
                            baseElement = element;
                            break;
                        }
                    }
                    if (baseElement != null) {
                        break;
                    }
                }
                if (dataOutputAssociations != null && !dataOutputAssociations.isEmpty()) {
                    for (DataOutputAssociation dataOutputAssociation : dataOutputAssociations) {
                        DataObjectReference dataObjectReference = modelInstance.getModelElementById(dataOutputAssociation.getTarget().getId());
                        if (dataObjectReference.getName().equals(artifactName)) {
                            baseElement = element;
                            break;
                        }
                    }
                    if (baseElement != null) {
                        break;
                    }
                }

            }
            if (baseElement != null) {
                bindElements.add(baseElement.getId());
                System.out.println("First element with DataObject in Process " + process.getId() + ": " + baseElement.getId());
            }
        }

        //
        Collection<MessageFlow> messageFlows = modelInstance.getModelElementsByType(MessageFlow.class);
        Collection<String> deleteBindingElements = new ArrayList<>();

        // determine input DataObject without state
        for (MessageFlow messageFlow : messageFlows)
        {
            System.out.println("######################################");
            System.out.println(messageFlow.getId());
            boolean isBefore = false;
            boolean isAfter = false;
            String deleteBindingElement = "";
            for (String bindingElementId:bindElements)
            {
                BaseElement checkSourceElement = modelInstance.getModelElementById(bindingElementId);
                BaseElement checkTargetElement = modelInstance.getModelElementById(bindingElementId);
                //deal with boundary event
                if (modelInstance.getModelElementById(bindingElementId) instanceof BoundaryEvent)
                {
                    SubProcess subActivity = modelInstance.getModelElementById(((BoundaryEvent) modelInstance.getModelElementById(bindingElementId)).getAttachedTo().getId());
                    checkTargetElement = (BaseElement) subActivity.getChildElementsByType(StartEvent.class).iterator().next();
                    checkSourceElement = (BaseElement) subActivity.getChildElementsByType(EndEvent.class).iterator().next();
                }
                //deal with subprocess
                if (modelInstance.getModelElementById(bindingElementId).getParentElement().getParentElement() instanceof SubProcess)
                {}
                if (checkSourceElement.getId().equals(messageFlow.getSource().getId()))
                {
                    isBefore = true;

                }
                else if (checkTargetElement.getId().equals(messageFlow.getTarget().getId()))
                {
                    isAfter = true;
                    deleteBindingElement = bindingElementId;
                }
                else
                {
                    BaseElement currentSourceElement = modelInstance.getModelElementById(messageFlow.getSource().getId());
                    BaseElement currentTargetElement = modelInstance.getModelElementById(messageFlow.getTarget().getId());
                    if (!isBefore) {
                        Collection<String> beforeCurrentIds = new ArrayList<>();
                        isBefore = isElementBefore(modelInstance, currentSourceElement, checkSourceElement,beforeCurrentIds);
                    }
                    if (!isAfter) {
                        Collection<String> afterCurrentIds = new ArrayList<>();
                        isAfter = isElementAfter(modelInstance, currentTargetElement, checkTargetElement,afterCurrentIds);
                        deleteBindingElement = bindingElementId;
                    }
                }
            }
            System.out.println(""+isBefore+""+isAfter);
            if (isAfter == true && isBefore == true)
            {
                System.out.println(deleteBindingElement);
                deleteBindingElements.add(deleteBindingElement);
            }

        }
        System.out.println("####");

        for (String deleteElement : deleteBindingElements)
        {
            bindElements.remove(deleteElement);
        }
        for (String ids : bindElements)
        {
            System.out.println(ids);
        }
        return bindElements;
    }
    public static void unbindingArtifact(BpmnModelInstance modelInstance,String artifactName)
    {
        Collection<Process> processes = modelInstance.getModelElementsByType(Process.class);
        Collection<String> unbindElements = new ArrayList<>();


        for (Process process:processes)
        {
            Collection<BaseElement> baseElementList = new ArrayList<>();
            baseElementList=displayElementsByDiagramOrder(modelInstance,process);
            System.out.println("########");
            for (BaseElement baseElement:baseElementList)
            {
                System.out.println("Element ID: " + baseElement.getId() + ", Type: " + baseElement.getElementType().getTypeName());
            }
        }
        /*
        // UnBinding
        for (Process process : processes) {
            Collection<FlowElement> elements = process.getFlowElements();
            Collections.sort(elements, Comparator.comparingInt(process::));
            Collection<String> processElements = new ArrayList<>();
            for (FlowElement element : elements)
            {
                System.out.println(element.getId());
            }

            System.out.println("#########################");

        }*/

    }
    public static boolean isElementBefore(BpmnModelInstance modelInstance,BaseElement currentElement,BaseElement targetElement,Collection<String> currentId)
    {
        Collection<SequenceFlow> sequenceFlows = new ArrayList<>();
        // check loop
        if (currentId.contains(currentElement.getId()))
        {
            return false;
        }
        currentId.add(currentElement.getId());

        if (currentElement instanceof Task)
        {
            Task task  = modelInstance.getModelElementById(currentElement.getId());
            sequenceFlows = task.getIncoming();
        }
        else if (currentElement instanceof BoundaryEvent)
        {
            BoundaryEvent boundaryEvent  = modelInstance.getModelElementById(currentElement.getId());
            sequenceFlows = boundaryEvent.getIncoming();
        }
        else if (currentElement instanceof StartEvent)
        {
            StartEvent startEvent  = modelInstance.getModelElementById(currentElement.getId());
            sequenceFlows = startEvent.getIncoming();
        }
        else if(currentElement instanceof EndEvent)
        {
            EndEvent endEvent  = modelInstance.getModelElementById(currentElement.getId());
            sequenceFlows = endEvent.getIncoming();
        }
        else if(currentElement instanceof IntermediateThrowEvent)
        {
            IntermediateThrowEvent intermediateThrowEvent  = modelInstance.getModelElementById(currentElement.getId());
            sequenceFlows = intermediateThrowEvent.getIncoming();
        }
        else if(currentElement instanceof IntermediateCatchEvent)
        {
            IntermediateCatchEvent intermediateCatchEvent  = modelInstance.getModelElementById(currentElement.getId());
            sequenceFlows = intermediateCatchEvent.getIncoming();
        }
        else if(currentElement instanceof Gateway)
        {
            Gateway gateway  = modelInstance.getModelElementById(currentElement.getId());
            sequenceFlows = gateway.getIncoming();
        }
        for (SequenceFlow sequenceFlow : sequenceFlows)
        {
            BaseElement sourceElement = modelInstance.getModelElementById(sequenceFlow.getSource().getId());
            if (sourceElement.getId().equals(targetElement.getId()))
            {
                return true;
            }
            else
            {
                boolean isBefore = isElementBefore(modelInstance,sourceElement,targetElement,currentId);
                if (isBefore)
                {
                    return true;
                }
            }

        }
        return false;
    }
    public static boolean isElementAfter(BpmnModelInstance modelInstance,BaseElement currentElement,BaseElement targetElement, Collection<String> currentId)
    {
        Collection<SequenceFlow> sequenceFlows = new ArrayList<>();
        // check loop
        if (currentId.contains(currentElement.getId()))
        {
            return false;
        }
        currentId.add(currentElement.getId());

        if (currentElement instanceof Task)
        {
            Task task  = modelInstance.getModelElementById(currentElement.getId());
            sequenceFlows = task.getOutgoing();
        }
        else if (currentElement instanceof BoundaryEvent)
        {
            BoundaryEvent boundaryEvent  = modelInstance.getModelElementById(currentElement.getId());
            sequenceFlows = boundaryEvent.getOutgoing();
        }
        else if (currentElement instanceof StartEvent)
        {
            StartEvent startEvent  = modelInstance.getModelElementById(currentElement.getId());
            sequenceFlows = startEvent.getOutgoing();
        }
        else if(currentElement instanceof EndEvent)
        {
            EndEvent endEvent  = modelInstance.getModelElementById(currentElement.getId());
            sequenceFlows = endEvent.getOutgoing();
        }
        else if(currentElement instanceof IntermediateThrowEvent)
        {
            IntermediateThrowEvent intermediateThrowEvent  = modelInstance.getModelElementById(currentElement.getId());
            sequenceFlows = intermediateThrowEvent.getOutgoing();
        }
        else if(currentElement instanceof IntermediateCatchEvent)
        {
            IntermediateCatchEvent intermediateCatchEvent  = modelInstance.getModelElementById(currentElement.getId());
            sequenceFlows = intermediateCatchEvent.getOutgoing();
        }
        else if(currentElement instanceof Gateway)
        {
            Gateway gateway  = modelInstance.getModelElementById(currentElement.getId());
            sequenceFlows = gateway.getOutgoing();
        }
        for (SequenceFlow sequenceFlow : sequenceFlows)
        {
            BaseElement sourceElement = modelInstance.getModelElementById(sequenceFlow.getTarget().getId());
            if (sourceElement.getId().equals(targetElement.getId()))
            {
                return true;
            }
            else
            {
                boolean isAfter = isElementAfter(modelInstance,sourceElement,targetElement,currentId);
                if (isAfter)
                {
                    return true;
                }
            }
        }
        return false;
    }

    public static Collection<BaseElement> displayElementsByDiagramOrder(BpmnModelInstance modelInstance,Process currentProcess) {
        Collection<Process> processes = modelInstance.getModelElementsByType(Process.class);
        Collection<BaseElement> baseElementList = new ArrayList<>();
        for (Process process : processes) {
            if (process.getId() == currentProcess.getId()) {
                System.out.println("Process ID: " + process.getId());
                FlowNode currentNode = process.getChildElementsByType(StartEvent.class).iterator().next();
                baseElementList=setElementsOrderWithSubProcess(modelInstance,currentNode,baseElementList);
                System.out.println("Process " + process.getId() + " has been traversed.");
            }
        }
        return baseElementList;

    }
    public static Collection<BaseElement> setElementsOrderWithSubProcess(BpmnModelInstance modelInstance,FlowNode currentNode,Collection<BaseElement> baseElementList) {
        Boolean haveLoop = false;
        while (!(currentNode instanceof EndEvent)) {
            //deal with subprocess
            if (currentNode instanceof SubProcess)
            {
                FlowNode startSubProcess = currentNode.getChildElementsByType(StartEvent.class).iterator().next();
                setElementsOrderWithSubProcess(modelInstance,startSubProcess,baseElementList);

            }
            //deal with boundary event
            for (BoundaryEvent boundaryEvent: modelInstance.getModelElementsByType(BoundaryEvent.class))
            {
                if(boundaryEvent.getAttachedTo().getId().equals(currentNode.getId()))
                {
                    setElementsOrderWithSubProcess(modelInstance,boundaryEvent,baseElementList);
                }
            }

            Collection<SequenceFlow> outgoingFlows = currentNode.getOutgoing();
            if (outgoingFlows.size() == 1 && !(currentNode instanceof Gateway)) {
                System.out.println("Element ID: " + currentNode.getId() + ", Type: " + currentNode.getElementType().getTypeName());
                baseElementList.add(currentNode);
                currentNode = outgoingFlows.iterator().next().getTarget();
            } else {
                // Handle gateway
                Gateway gateway = (Gateway) currentNode;
                baseElementList = setElementsOrderWithGateWay(modelInstance, gateway, baseElementList);
                // set new currentNode
                BaseElement currentElement = baseElementList.stream().toList().get(baseElementList.size()-1);
                if (currentElement instanceof Gateway)
                {
                    Gateway currentgateway = modelInstance.getModelElementById(currentElement.getId());
                    currentNode = currentgateway.getOutgoing().iterator().next().getTarget();
                }
                else
                {
                    haveLoop = true;
                    break;
                }
            }
        }
        if (!haveLoop) {
            System.out.println("Element ID: " + currentNode.getId() + ", Type: " + currentNode.getElementType().getTypeName());
            baseElementList.add(currentNode);
        }
        return baseElementList;
    }
    public static Collection<BaseElement> setElementsOrderWithGateWay(BpmnModelInstance modelInstance,Gateway gateway,Collection<BaseElement> baseElementList)
    {
        FlowNode currentNode = gateway;
        FlowNode endcurrentNode = gateway;
        System.out.println("Element ID: " + currentNode.getId() + ", Type: " + currentNode.getElementType().getTypeName());
        baseElementList.add(currentNode);
        Collection<SequenceFlow> outgoingFlows = currentNode.getOutgoing();
        //check if this gateway have loop problem
        Boolean haveLoop = false;

        // normal gateway
        for (SequenceFlow sequenceFlow: outgoingFlows) {
            SequenceFlow currentSequenceFlow = sequenceFlow;

            while (!((currentSequenceFlow.getTarget() instanceof Gateway)&&(currentSequenceFlow.getTarget().getOutgoing().size()==1))){

                // normal activity/event
                if ((currentSequenceFlow.getTarget().getOutgoing().size() == 1)&& !(currentSequenceFlow.getTarget() instanceof Gateway)) {
                    System.out.println("Element ID: " + currentSequenceFlow.getTarget().getId() + ", Type: " + currentSequenceFlow.getTarget().getElementType().getTypeName());
                    baseElementList.add(currentSequenceFlow.getTarget());
                    if (currentSequenceFlow.getTarget() instanceof EndEvent)
                    {
                        haveLoop=true;
                        break;
                    }
                    currentSequenceFlow = currentSequenceFlow.getTarget().getOutgoing().iterator().next();

                }
                // gateway
                else if ((currentSequenceFlow.getTarget().getOutgoing().size() > 1)&& (currentSequenceFlow.getTarget() instanceof Gateway))
                {
                    Gateway otherGateway = (Gateway) currentSequenceFlow.getTarget();
                    if (baseElementList.contains(currentSequenceFlow.getTarget()))
                    {
                        haveLoop = true;
                        break;
                    }
                    setElementsOrderWithGateWay(modelInstance,otherGateway,baseElementList);
                    BaseElement currentElement = baseElementList.stream().toList().get(baseElementList.size()-1);
                    if (currentElement instanceof Gateway)
                    {
                        Gateway currentgateway = modelInstance.getModelElementById(currentElement.getId());
                        currentSequenceFlow = currentgateway.getOutgoing().iterator().next();
                    }
                }
                else
                {
                    haveLoop = true;
                    break;
                }

            }

            endcurrentNode = currentSequenceFlow.getTarget();

        }
        // end gateway
        if (!haveLoop)
        {
            System.out.println("Element ID: " + endcurrentNode.getId() + ", Type: " + endcurrentNode.getElementType().getTypeName());
            baseElementList.add(endcurrentNode);
        }

        return baseElementList;
    }

}
