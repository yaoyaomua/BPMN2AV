package Step4_Well_Structure.BPStruct;

import Step2_Flow_Transform.AddIncomingOrOutcoming;
import Step2_Flow_Transform.CreateBPMNEdge;
import Step2_Flow_Transform.CreateBPMNShape;
import Step2_Flow_Transform.GetBounds;
import Step3_Delete_Element.Generate7ID;
import Step3_Delete_Element.GenerateID;
import de.hpi.bpt.process.ControlFlow;
import de.hpi.bpt.process.Process;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnEdge;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnPlane;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnShape;
import org.camunda.bpm.model.bpmn.instance.dc.Bounds;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;

public class JSON2BPMN {


    public JSON2BPMN() {
    }

    public static void store(BpmnModelInstance modelInstance, Process process) throws JSONException {
//        HashMap<String, String> Name2BpmnTask = new HashMap<>();
        HashSet<String> name = new HashSet<>();
//        HashMap<String, String> Id2Name = new HashMap<>();
        HashMap<String,HashSet<de.hpi.bpt.process.Task>> dup = new HashMap<>();
//        HashMap<String, String> bpmn2Id = new HashMap<>();
//        HashMap<String, String> Id2Name = new HashMap<>();

        HashMap<String, String> gatewayId = new HashMap<>();

        BpmnPlane plane = modelInstance.getModelElementsByType(BpmnPlane.class).iterator().next();
        org.camunda.bpm.model.bpmn.instance.Process BpmnProcess = modelInstance.getModelElementsByType(org.camunda.bpm.model.bpmn.instance.Process.class).iterator().next();
//        for (Task BpmnTask : modelInstance.getModelElementsByType(Task.class)){
//            Name2BpmnTask.put(BpmnTask.getName(),BpmnTask.getId());
//        }
//
//        for (Event event : modelInstance.getModelElementsByType(Event.class)){
//            Name2BpmnTask.put(event.getName(), event.getId());
//        }
//
//        for (SubProcess subProcess : modelInstance.getModelElementsByType(SubProcess.class)){
//            Name2BpmnTask.put(subProcess.getName(), subProcess.getId());
//        }
//
//        for (de.hpi.bpt.process.Task task : process.getTasks()){
//            String BpmnId = task.getName();
//            String BpstructId = task.getId();
//            if (dup.containsKey(BpmnId)){
//                //multiple same name elements
//                dup.get(BpmnId).add(task);
//                //copy a bpmn task
//                if (modelInstance.getModelElementById(BpmnId) instanceof Task){
//                    Task ac = modelInstance.newInstance(Task.class);
//                    Task preac = modelInstance.getModelElementById(BpmnId);
//                    ac.setId(GenerateID.getID("CopyTask_",modelInstance));
//                    ac.setName(preac.getName());
//                    task.setName(ac.getId());
//                    if ()
//                }
//            }
//
//        }

        for (SequenceFlow sequenceFlow : modelInstance.getModelElementsByType(SequenceFlow.class)){
            if (sequenceFlow.getParentElement() instanceof SubProcess) continue;

            sequenceFlow.getDiagramElement().getParentElement().removeChildElement(sequenceFlow.getDiagramElement());
            sequenceFlow.getParentElement().removeChildElement(sequenceFlow);
        }

        for (Gateway gateway : modelInstance.getModelElementsByType(Gateway.class)){
            if(gateway.getParentElement() instanceof  SubProcess) continue;

            gateway.getDiagramElement().getParentElement().removeChildElement(gateway.getDiagramElement());
            gateway.getParentElement().removeChildElement(gateway);
        }


        int count = 0;
        for (de.hpi.bpt.process.Gateway gateway : process.getGateways()){
//            System.out.println("gateway id: " + gateway.getId());
            Gateway g;
            String type;
            if (gateway.isAND()){
                g = modelInstance.newInstance(ParallelGateway.class);
            } else if (gateway.isOR()) {
                g = modelInstance.newInstance(InclusiveGateway.class);
            } else if (gateway.isXOR()) {
                g = modelInstance.newInstance(ExclusiveGateway.class);
            } else if (gateway.isEventBased()){
                g = modelInstance.newInstance(EventBasedGateway.class);
            } else if (gateway.isCGT()) {
                g = modelInstance.newInstance(ComplexGateway.class);
            }else {
                g = modelInstance.newInstance(Gateway.class);
            }
            g.setId(GenerateID.getID("Gateway_",modelInstance));
            System.out.println(g.getId());
            if (gateway.getName() != null){
                g.setName(gateway.getName());
            }
            BpmnProcess.addChildElement(g);
            BpmnShape gShape = CreateBPMNShape.create(modelInstance,g.getId(),100.0 + count * 100, 10.0, 50.0,50.0);
            plane.addChildElement(gShape);
            gatewayId.put(gateway.getId(),g.getId());
            count ++;
        }

//        for (Gateway gateway : modelInstance.getModelElementsByType(Gateway.class)){
//            System.out.println(gateway.getId());
//        }
//
//        System.out.println(Name2BpmnTask.toString());
        for (ControlFlow flow : process.getControlFlow()){

            String BpmnSrcId = flow.getSource().getName();
            String BpmnTgtId = flow.getTarget().getName();
            String srcId = flow.getSource().getId();
            String tgtId = flow.getTarget().getId();
            if (gatewayId.containsKey(srcId)){
                BpmnSrcId = gatewayId.get(srcId);
            }
            if (gatewayId.containsKey(tgtId)){
                BpmnTgtId = gatewayId.get(tgtId);
            }

            String context = flow.getLabel();
            String Id = flow.getId();
//
//            System.out.println("flow id:" + Id);
//            System.out.println("bpmn id:" + BpmnSrcId);
//            System.out.println("bpstruct id:" + flow.getSource().getId());
//            System.out.println("bpmn id:" + BpmnTgtId);
//            System.out.println("bpstruct id:" + flow.getTarget().getId());
//            System.out.println();



            SequenceFlow sequenceFlow = modelInstance.newInstance(SequenceFlow.class);
            sequenceFlow.setId(GenerateID.getID("Flow_",modelInstance));
            if (context != null){
                sequenceFlow.setName(context);
            }
            BpmnProcess.addChildElement(sequenceFlow);


            //scrId and tgtId are the id of the tasks in bpstruct's process
//            String srcId = flow.getSource().getId();
//            String tgtId = flow.getTarget().getId();
//            String srcId = srcName;
//            String tgtId = tgtName;


//            if ( Name2BpmnTask.containsKey(srcName)) {
//                srcId = Name2BpmnTask.get(srcName);
//            }else if (gatewayId.containsKey(srcId)){
//                srcId = gatewayId.get(srcId);
//            }
//            if (Name2BpmnTask.containsKey(tgtName)){
//                tgtId = Name2BpmnTask.get(tgtName);
//            } else if (gatewayId.containsKey(tgtId)) {
//                tgtId = gatewayId.get(tgtId);
//            }

//            System.out.println(srcId);
//            System.out.println(tgtId);
//            System.out.println();

            sequenceFlow.setSource(modelInstance.getModelElementById(BpmnSrcId));
            sequenceFlow.setTarget(modelInstance.getModelElementById(BpmnTgtId));

            //update incoming and outgoing
            AddIncomingOrOutcoming.AddOutgoingToElement(modelInstance,BpmnSrcId,sequenceFlow);
            AddIncomingOrOutcoming.AddIncomingToElement(modelInstance,BpmnTgtId,sequenceFlow);

            Bounds bounds1 = GetBounds.get(modelInstance,BpmnSrcId);
            Bounds bounds2 = GetBounds.get(modelInstance,BpmnTgtId);
            BpmnEdge edge = CreateBPMNEdge.create(modelInstance,sequenceFlow,
                    bounds1.getX() + bounds1.getWidth()/2, bounds1.getY() + bounds1.getHeight()/2,
                    bounds2.getX() + bounds2.getWidth()/2, bounds2.getY() + bounds2.getHeight()/2);
            plane.addChildElement(edge);
        }
    }

}
