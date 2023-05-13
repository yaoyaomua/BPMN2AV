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
        Collection<String> unbindingElements = new ArrayList<>();
        bindingElements = bindingArtifact(modelInstance,artifactName);
        unbindingElements = unbindingArtifact(modelInstance,artifactName);
        String newID;

        // diagram for Data Object without state
        BaseElement initialElement = modelInstance.getModelElementById(bindingElements.iterator().next());
        while (!(initialElement instanceof Process))
        {
            initialElement = (BaseElement) initialElement.getParentElement();
        }
        Process firstProcess = (Process) initialElement;
        //DataObject
        DataObject dataObject = modelInstance.newInstance(DataObject.class);
        do {
            newID = Generate7ID.generate();
        } while (modelInstance.getModelElementById("myDataObject" + newID) != null);
        dataObject.setId("myDataObject" + newID);
        firstProcess.addChildElement(dataObject);
        //DataReference
        DataObjectReference dataObjectReference = modelInstance.newInstance(DataObjectReference.class);
        do {
            newID = Generate7ID.generate();
        } while (modelInstance.getModelElementById("myDataReference" + newID) != null);
        dataObjectReference.setId("myDataReference" + newID);
        dataObjectReference.setDataObject(dataObject);
        dataObjectReference.setName(artifactName);
        firstProcess.addChildElement(dataObjectReference);
        // ######## Diagram: DataReference ########
        Bounds bounds = GetBounds.get(modelInstance,bindingElements.iterator().next());
        CreateBPMNShape.create(modelInstance,dataObjectReference.getId(),bounds.getX(),bounds.getY()-50,36.0,50.0);


        String type = null;
        // diagram for bindingArtifact
        type = "binding";

        for (String bindingElement : bindingElements)
        {
            // current process
            BaseElement currentElement = modelInstance.getModelElementById(bindingElement);
            while (!(currentElement instanceof Process))
            {
                currentElement = (BaseElement) currentElement.getParentElement();
            }
            Process currentProcess = (Process) currentElement;
            // first gateway
            List<BaseElement> currentProcessElementList = displayElementsByDiagramOrder(modelInstance, currentProcess);
            BaseElement firstGateway = null;
            for (BaseElement element: currentProcessElementList)
            {
                if (element instanceof Gateway)
                {
                    firstGateway = element;
                }

            }

            // choose sequenceFlow to set the intermidiateEvent
            SequenceFlow sequenceFlow = null;
            BaseElement baseElement = modelInstance.getModelElementById(bindingElement);
            Boolean isGatewayBeforeElement = false;
            if (firstGateway != null)
            {
                Collection<String> currentIds = new ArrayList<>();
                isGatewayBeforeElement = isElementBefore(modelInstance,baseElement,firstGateway,currentIds);
            }
            if (isGatewayBeforeElement)
            {
                if (firstGateway instanceof Gateway) {
                    Gateway gateway = modelInstance.getModelElementById(firstGateway.getId());
                    sequenceFlow = gateway.getIncoming().iterator().next();
                }
            }
            else {
                if (baseElement instanceof Task) {
                    Task element = modelInstance.getModelElementById(bindingElement);
                    sequenceFlow = element.getIncoming().iterator().next();

                }
                else if (baseElement instanceof Event)
                {
                    Event element = modelInstance.getModelElementById(bindingElement);
                    sequenceFlow = element.getIncoming().iterator().next();
                }
            }
            DrawDiagramForDataObjectWithoutState(modelInstance,baseElement,sequenceFlow,dataObjectReference,type);
        }

        // diagram for unbindingArtifact
        type = "unbinding";
        for (String unbindingElement : unbindingElements) {
            // current process
            BaseElement currentElement = modelInstance.getModelElementById(unbindingElement);
            while (!(currentElement instanceof Process))
            {
                currentElement = (BaseElement) currentElement.getParentElement();
            }
            Process currentProcess = (Process) currentElement;
            // first gateway
            List<BaseElement> currentProcessElementList = displayElementsByDiagramOrder(modelInstance, currentProcess);
            Collections.reverse(currentProcessElementList);

            BaseElement lastGateway = null;
            for (BaseElement element: currentProcessElementList)
            {
                if (element instanceof Gateway)
                {
                    lastGateway = element;
                }

            }


            SequenceFlow sequenceFlow = null;
            BaseElement baseElement = modelInstance.getModelElementById(unbindingElement);
            Boolean isGatewayAfterElement = false;
            if (lastGateway != null)
            {
                Collection<String> currentIds = new ArrayList<>();
                isGatewayAfterElement = isElementBefore(modelInstance,baseElement,lastGateway,currentIds);
            }
            if (isGatewayAfterElement)
            {
                if (lastGateway instanceof Gateway) {
                    Gateway gateway = modelInstance.getModelElementById(lastGateway.getId());
                    sequenceFlow = gateway.getOutgoing().iterator().next();
                }
            }
            else {
                if (baseElement instanceof Task) {
                    Task element = modelInstance.getModelElementById(unbindingElement);
                    sequenceFlow = element.getOutgoing().iterator().next();

                } else if (baseElement instanceof Event) {
                    Event element = modelInstance.getModelElementById(unbindingElement);
                    sequenceFlow = element.getOutgoing().iterator().next();
                }
            }
            DrawDiagramForDataObjectWithoutState(modelInstance,baseElement,sequenceFlow,dataObjectReference,type);

        }

    }
    public static Collection<String> bindingArtifact(BpmnModelInstance modelInstance,String artifactName) {
        Collection<Process> processes = modelInstance.getModelElementsByType(Process.class);
        Collection<String> bindElements = new ArrayList<>();
        // Binding
        for (Process process : processes) {
            BaseElement baseElement = null;
            List<BaseElement> baseElementList = new ArrayList<>();
            baseElementList=displayElementsByDiagramOrder(modelInstance,process);
            bindElements=findElement(modelInstance,process, artifactName, bindElements,baseElement,baseElementList);
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
    public static Collection<String> unbindingArtifact(BpmnModelInstance modelInstance,String artifactName)
    {
        Collection<Process> processes = modelInstance.getModelElementsByType(Process.class);
        Collection<String> unbindElements = new ArrayList<>();


        for (Process process:processes)
        {
            BaseElement baseElement = null;
            List<BaseElement> baseElementList = new ArrayList<>();
            baseElementList=displayElementsByDiagramOrder(modelInstance,process);
            System.out.println("########");
            Collections.reverse(baseElementList);
            unbindElements=findElement(modelInstance,process, artifactName, unbindElements,baseElement,baseElementList);

        }
        // UnBinding
        Collection<MessageFlow> messageFlows = modelInstance.getModelElementsByType(MessageFlow.class);
        Collection<String> deleteUnBindingElements = new ArrayList<>();

        // determine input DataObject without state
        for (MessageFlow messageFlow : messageFlows)
        {
            System.out.println("######################################");
            System.out.println(messageFlow.getId());
            boolean isBefore = false;
            boolean isAfter = false;
            String deleteUnBindingElement = "";
            for (String unbindingElementId:unbindElements)
            {
                BaseElement checkSourceElement = modelInstance.getModelElementById(unbindingElementId);
                BaseElement checkTargetElement = modelInstance.getModelElementById(unbindingElementId);
                //deal with boundary event
                if (modelInstance.getModelElementById(unbindingElementId) instanceof BoundaryEvent)
                {
                    SubProcess subActivity = modelInstance.getModelElementById(((BoundaryEvent) modelInstance.getModelElementById(unbindingElementId)).getAttachedTo().getId());
                    checkTargetElement = (BaseElement) subActivity.getChildElementsByType(StartEvent.class).iterator().next();
                    checkSourceElement = (BaseElement) subActivity.getChildElementsByType(EndEvent.class).iterator().next();
                }
                //deal with subprocess
                if (modelInstance.getModelElementById(unbindingElementId).getParentElement().getParentElement() instanceof SubProcess)
                {}
                if (checkSourceElement.getId().equals(messageFlow.getSource().getId()))
                {
                    isBefore = true;
                    if (!(modelInstance.getModelElementById(unbindingElementId) instanceof BoundaryEvent)) {
                        deleteUnBindingElement = unbindingElementId;
                    }

                }
                else if (checkTargetElement.getId().equals(messageFlow.getTarget().getId()))
                {
                    isAfter = true;
                }
                else
                {
                    BaseElement currentSourceElement = modelInstance.getModelElementById(messageFlow.getSource().getId());
                    BaseElement currentTargetElement = modelInstance.getModelElementById(messageFlow.getTarget().getId());
                    if (!isBefore) {
                        Collection<String> beforeCurrentIds = new ArrayList<>();
                        isBefore = isElementBefore(modelInstance, currentSourceElement, checkSourceElement,beforeCurrentIds);
                        if (!(modelInstance.getModelElementById(unbindingElementId) instanceof BoundaryEvent)) {
                            deleteUnBindingElement = unbindingElementId;
                        }
                    }
                    if (!isAfter) {
                        Collection<String> afterCurrentIds = new ArrayList<>();
                        isAfter = isElementAfter(modelInstance, currentTargetElement, checkTargetElement,afterCurrentIds);
                    }
                }
            }
            System.out.println(""+isBefore+""+isAfter);
            if (isAfter == true && isBefore == true)
            {
                System.out.println(deleteUnBindingElement);
                deleteUnBindingElements.add(deleteUnBindingElement);
            }

        }
        System.out.println("####");

        for (String deleteElement : deleteUnBindingElements)
        {
            unbindElements.remove(deleteElement);
        }
        for (String ids : unbindElements)
        {
            System.out.println(ids);
        }

        return unbindElements;
    }
    public static  Collection<String>  findElement(BpmnModelInstance modelInstance,Process process,String artifactName, Collection<String> checkElements, BaseElement baseElement,List<BaseElement> baseElementList) {
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
            checkElements.add(baseElement.getId());
            System.out.println("Check element with DataObject in Process " + process.getId() + ": " + baseElement.getId());
        }
        return checkElements;
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

    public static List<BaseElement> displayElementsByDiagramOrder(BpmnModelInstance modelInstance,Process currentProcess) {
        Collection<Process> processes = modelInstance.getModelElementsByType(Process.class);
        List<BaseElement> baseElementList = new ArrayList<>();
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
    public static List<BaseElement> setElementsOrderWithSubProcess(BpmnModelInstance modelInstance,FlowNode currentNode,List<BaseElement> baseElementList) {
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
    public static List<BaseElement> setElementsOrderWithGateWay(BpmnModelInstance modelInstance,Gateway gateway,List<BaseElement> baseElementList)
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

    public static void DrawDiagramForDataObjectWithoutState(BpmnModelInstance modelInstance,BaseElement baseElement,SequenceFlow sequenceFlow,DataObjectReference dataObjectReference,String type)
    {
        String newID;
        Bounds dataReferenceBound = GetBounds.get(modelInstance,dataObjectReference.getId());
        while (!(baseElement instanceof Process))
        {
            baseElement = (BaseElement) baseElement.getParentElement();
        }
        Process process = (Process) baseElement;

        IntermediateCatchEvent intermediateCatchEvent = modelInstance.newInstance(IntermediateCatchEvent.class);
        IntermediateThrowEvent intermediateThrowEvent = modelInstance.newInstance(IntermediateThrowEvent.class);
        if(type == "binding") {
            do {
                newID = Generate7ID.generate();
            } while (modelInstance.getModelElementById("myIntermediateCatchEvent_" + newID) != null);
            intermediateCatchEvent.setId("myIntermediateCatchEvent_" + newID);
            process.addChildElement(intermediateCatchEvent);
        }
        else
        {
            do {
                newID = Generate7ID.generate();
            } while (modelInstance.getModelElementById("myIntermediateThrowEvent_" + newID) != null);
            intermediateThrowEvent.setId("myIntermediateThrowEvent_" + newID);
            process.addChildElement(intermediateThrowEvent);
        }
        //add an sequence flow between activity and event
        SequenceFlow incoming = modelInstance.newInstance(SequenceFlow.class);
        do {
            newID = Generate7ID.generate();
        }while(modelInstance.getModelElementById("Flow_"+newID)!=null);
        incoming.setId("Flow"+newID);
        if (type == "binding") {
            incoming.setTarget(intermediateCatchEvent);
        }
        else
        {
            incoming.setTarget(intermediateThrowEvent);
        }
        incoming.setSource(sequenceFlow.getSource());
        process.addChildElement(incoming);
        //set outgoing of the preelement
        AddIncomingOrOutcoming.AddOutgoingToElement(modelInstance, sequenceFlow.getSource().getId(),incoming);
        //set outgoing of the event
        if (type == "binding") {
            intermediateCatchEvent.getIncoming().add(incoming);
        }
        else
        {
            intermediateThrowEvent.getIncoming().add(incoming);
        }

        //
        SequenceFlow outgoing = modelInstance.newInstance(SequenceFlow.class);
        do {
            newID = Generate7ID.generate();
        }while(modelInstance.getModelElementById("Flow_"+newID)!=null);
        outgoing.setId("Flow"+newID);
        outgoing.setTarget(sequenceFlow.getTarget());
        if (type == "binding") {
            outgoing.setSource(intermediateCatchEvent);
        }
        else
        {
            outgoing.setSource(intermediateThrowEvent);
        }
        process.addChildElement(outgoing);
        //set incoming of the preelement
        AddIncomingOrOutcoming.AddIncomingToElement(modelInstance, sequenceFlow.getTarget().getId(),outgoing);
        //set incoming of the event
        if (type == "binding") {
            intermediateCatchEvent.getOutgoing().add(outgoing);
        }
        else
        {
            intermediateThrowEvent.getOutgoing().add(outgoing);
        }

        //Output Association
        //Property
        Property property = modelInstance.newInstance(Property.class);
        do {
            newID = Generate7ID.generate();
        } while (modelInstance.getModelElementById("myProperty" + newID) != null);
        property.setId("myProperty" + newID);
        property.setName("__sourceRef__" + newID);
        if (type == "binding") {
            intermediateCatchEvent.addChildElement(property);
            intermediateCatchEvent.getProperties().add(property);
        }
        else
        {
            intermediateThrowEvent.addChildElement(property);
            intermediateThrowEvent.getProperties().add(property);
        }

        DataOutputAssociation outputAssociation = modelInstance.newInstance(DataOutputAssociation.class);
        DataInputAssociation inputAssociation = modelInstance.newInstance(DataInputAssociation.class);
        if (type == "binding") {
            do {
                newID = Generate7ID.generate();
            } while (modelInstance.getModelElementById("myOutputAssociation" + newID) != null);
            outputAssociation.setId("myOutputAssociation" + newID);
            outputAssociation.setTarget(dataObjectReference);
            outputAssociation.getSources().add(property);
            intermediateCatchEvent.getDataOutputAssociations().add(outputAssociation);
            intermediateCatchEvent.addChildElement(outputAssociation);
        }
        else
        {
            do {
                newID = Generate7ID.generate();
            } while (modelInstance.getModelElementById("myInputAssociation" + newID) != null);
            inputAssociation.setId("myInputAssociation" + newID);
            inputAssociation.getSources().add(dataObjectReference);
            inputAssociation.setTarget(property);
            intermediateThrowEvent.getDataInputAssociations().add(inputAssociation);
            intermediateThrowEvent.addChildElement(inputAssociation);
        }

        // ######## diagram ############
        Bounds sourceBound = GetBounds.get(modelInstance,sequenceFlow.getSource().getId());
        Bounds targetBound = GetBounds.get(modelInstance,sequenceFlow.getTarget().getId());
        // ######## Diagram: interEvent ########
        Bounds eventBound = null;
        if (type == "binding") {
            CreateBPMNShape.create(modelInstance,intermediateCatchEvent.getId(),sourceBound.getX()+50,sourceBound.getY(),36.0,36.0);
            eventBound = GetBounds.get(modelInstance, intermediateCatchEvent.getId());
        }
        else
        {
            CreateBPMNShape.create(modelInstance,intermediateThrowEvent.getId(),sourceBound.getX()+50,sourceBound.getY(),36.0,36.0);
            eventBound = GetBounds.get(modelInstance, intermediateThrowEvent.getId());
        }
        // ######## Diagram: sequenceFlow ########
        CreateBPMNEdge.create(modelInstance, incoming, sourceBound.getX(),sourceBound.getY(),eventBound.getX(),eventBound.getY());
        CreateBPMNEdge.create(modelInstance, outgoing, eventBound.getX(),eventBound.getY(),targetBound.getX(),targetBound.getY());
        // ######## Diagram: Output Association ########
        if (type == "binding") {
            CreateBPMNEdge.create(modelInstance, outputAssociation, eventBound.getX(), eventBound.getY(), dataReferenceBound.getX(), dataReferenceBound.getY());
        }
        else
        {
            CreateBPMNEdge.create(modelInstance, inputAssociation, eventBound.getX(), eventBound.getY(), dataReferenceBound.getX(), dataReferenceBound.getY());
        }
        // delete sequence flow
        modelInstance.getModelElementById(sequenceFlow.getSource().getId()).removeChildElement(sequenceFlow);
        modelInstance.getModelElementById(sequenceFlow.getTarget().getId()).removeChildElement(sequenceFlow);
        modelInstance.getModelElementById(sequenceFlow.getId()).getParentElement().removeChildElement(sequenceFlow);
    }

}
