package Step4_Well_Structure.BPStruct;

import de.hpi.bpt.process.ControlFlow;
import de.hpi.bpt.process.Gateway;
import de.hpi.bpt.process.GatewayType;
import de.hpi.bpt.process.Process;
import de.hpi.bpt.process.Node;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import java.io.IOException;
import java.util.*;

public class BPMNReader {

    public BPMNReader(){

    }

    public static Process parse(BpmnModelInstance modelInstance) throws JDOMException, IOException{
        Namespace BPMN2NS = Namespace.getNamespace("http://www.omg.org/spec/BPMN/20100524/MODEL");
        Process proc = new Process();

        initProcess(proc, modelInstance);
        return proc;
    }

    private static void initProcess(Process process, BpmnModelInstance modelInstance){
        Map<String, Node> nodes = new HashMap<>();
        Collection<SequenceFlow> flows = new ArrayList<>();

        //add task(all activities, events, and sub-process)
//        for (org.camunda.bpm.model.bpmn.instance.Task BpmnTask : modelInstance.getModelElementsByType(org.camunda.bpm.model.bpmn.instance.Task.class)){
//            if (BpmnTask.getParentElement() instanceof SubProcess) continue;
//            de.hpi.bpt.process.Task task = new de.hpi.bpt.process.Task(BpmnTask.getName());
//            task.setId(BpmnTask.getId());
//            nodes.put(task.getId(),task);
//        }
//        for (org.camunda.bpm.model.bpmn.instance.Event BpmnEvent : modelInstance.getModelElementsByType(org.camunda.bpm.model.bpmn.instance.Event.class)){
//            if (BpmnEvent.getParentElement() instanceof SubProcess) continue;
//            de.hpi.bpt.process.Task task = new de.hpi.bpt.process.Task(BpmnEvent.getName());
//            task.setId(BpmnEvent.getId());
//            nodes.put(task.getId(),task);
//        }
//        for (org.camunda.bpm.model.bpmn.instance.SubProcess BpmnSub : modelInstance.getModelElementsByType(org.camunda.bpm.model.bpmn.instance.SubProcess.class)){
//            if (BpmnSub.getParentElement() instanceof SubProcess) continue;
//            de.hpi.bpt.process.Task task = new de.hpi.bpt.process.Task(BpmnSub.getName());
//            task.setId(BpmnSub.getId());
//            nodes.put(task.getId(),task);
//        }

        for (org.camunda.bpm.model.bpmn.instance.Task BpmnTask : modelInstance.getModelElementsByType(org.camunda.bpm.model.bpmn.instance.Task.class)){
            if (BpmnTask.getParentElement() instanceof SubProcess) continue;
            de.hpi.bpt.process.Task task = new de.hpi.bpt.process.Task(BpmnTask.getId());
            task.setId(BpmnTask.getId());
            nodes.put(task.getId(),task);
        }
        for (org.camunda.bpm.model.bpmn.instance.Event BpmnEvent : modelInstance.getModelElementsByType(org.camunda.bpm.model.bpmn.instance.Event.class)){
            if (BpmnEvent.getParentElement() instanceof SubProcess) continue;
            de.hpi.bpt.process.Task task = new de.hpi.bpt.process.Task(BpmnEvent.getId());
            task.setId(BpmnEvent.getId());
            nodes.put(task.getId(),task);
        }
        for (org.camunda.bpm.model.bpmn.instance.SubProcess BpmnSub : modelInstance.getModelElementsByType(org.camunda.bpm.model.bpmn.instance.SubProcess.class)){
            if (BpmnSub.getParentElement() instanceof SubProcess) continue;
            de.hpi.bpt.process.Task task = new de.hpi.bpt.process.Task(BpmnSub.getId());
            task.setId(BpmnSub.getId());
            nodes.put(task.getId(),task);
        }

        //add gateway
        int count = 0;
        for (org.camunda.bpm.model.bpmn.instance.Gateway BpmnGateway : modelInstance.getModelElementsByType(org.camunda.bpm.model.bpmn.instance.Gateway.class)){
            if (BpmnGateway.getParentElement() instanceof SubProcess) continue;
            if (BpmnGateway instanceof ExclusiveGateway){
                if (BpmnGateway.getName() == null){
                    BpmnGateway.setName("Exclusive Gateway " + count);
                    count++;
                }
                Gateway gateway = new Gateway(GatewayType.XOR, BpmnGateway.getName());
                gateway.setId(BpmnGateway.getId());
                nodes.put(gateway.getId(), gateway);
            } else if (BpmnGateway instanceof ParallelGateway) {
                Gateway gateway = new Gateway(GatewayType.AND, BpmnGateway.getName());
                gateway.setId(BpmnGateway.getId());
                nodes.put(gateway.getId(), gateway);
            } else if (BpmnGateway instanceof InclusiveGateway) {
                Gateway gateway = new Gateway(GatewayType.OR, BpmnGateway.getName());
                gateway.setId(BpmnGateway.getId());
                nodes.put(gateway.getId(), gateway);
            } else if (BpmnGateway instanceof EventBasedGateway) {
                Gateway gateway = new Gateway(GatewayType.EVENT, BpmnGateway.getName());
                gateway.setId(BpmnGateway.getId());
                nodes.put(gateway.getId(), gateway);
            } else if (BpmnGateway instanceof ComplexGateway) {
                Gateway gateway = new Gateway(GatewayType.CGT, BpmnGateway.getName());
                gateway.setId(BpmnGateway.getId());
                nodes.put(gateway.getId(), gateway);
            } else {
                Gateway gateway = new Gateway(GatewayType.UNDEFINED, BpmnGateway.getName());
                gateway.setId(BpmnGateway.getId());
                nodes.put(gateway.getId(), gateway);
            }
        }

        System.out.println(nodes.toString());

        //add sequence flow
        for (SequenceFlow sequenceFlow : modelInstance.getModelElementsByType(SequenceFlow.class)){
            if (sequenceFlow.getParentElement() instanceof SubProcess) continue;
//            System.out.println(sequenceFlow.getId());
//            System.out.println(sequenceFlow.getSource().getId());
//            System.out.println(sequenceFlow.getTarget().getId());
//            System.out.println(sequenceFlow.getSource().getName());
//            System.out.println(sequenceFlow.getTarget().getName());
            Node src = (Node) nodes.get(sequenceFlow.getSource().getId());
            Node tgt = (Node) nodes.get(sequenceFlow.getTarget().getId());


            if(src == null || tgt == null) {
//                System.out.println(src);
//                System.out.println(tgt);
                throw new RuntimeException("Malformed graph");
            }

            ControlFlow flow = process.addControlFlow(src,tgt);
            String label = "";
            if (sequenceFlow.getName() != null){
                label = sequenceFlow.getName();
            }
            flow.setLabel(label);
//            System.out.println("add flow success!");
//            System.out.println();
        }
    }
}
