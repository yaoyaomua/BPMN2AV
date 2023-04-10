package Step3_Delete_Element;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnEdge;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnPlane;
import org.camunda.bpm.model.bpmn.instance.dc.Bounds;
import org.camunda.bpm.model.bpmn.instance.di.Waypoint;

import java.util.*;

public class DeleteElement {

    public DeleteElement(){

    }

    public static void delete(BpmnModelInstance modelInstance){

        List<NoAssociationTask> tasksToDelete = new ArrayList<>();
//        List<String> starts = new ArrayList<>();
//        List<String> ends = new ArrayList<>();


        //mark start event and end event
//        for (StartEvent startEvent: modelInstance.getModelElementsByType(StartEvent.class)){
//            starts.add(startEvent.getId());
//        }
//        for (EndEvent endEvent: modelInstance.getModelElementsByType(EndEvent.class)){
//            ends.add(endEvent.getId());
//        }

        //mark irrelevant elements by check the Associations of the task
        for(Task task: modelInstance.getModelElementsByType(Task.class)){
            Collection<DataInputAssociation> inputDataAssociations = task.getDataInputAssociations();
//            System.out.println(inputDataAssociations.isEmpty());
            Collection<DataOutputAssociation> outputDataAssociations = task.getDataOutputAssociations();
//            System.out.println(outputDataAssociations.isEmpty());
            //if the task do not have input or out put data association, then we mark it in the list
            if (inputDataAssociations.isEmpty() && outputDataAssociations.isEmpty()){
//                System.out.println("add irrelevant task");
                NoAssociationTask nut = new NoAssociationTask(task.getId(),task);
                tasksToDelete.add(nut);
//                System.out.println("task to delete : " + nut.getId());
            }
        }

        System.out.println("number of taske to delete:" + tasksToDelete.size());
//        System.out.println(modelInstance.getModelElementById("Activity_1mvshxv").getElementType().getTypeName());

        for (NoAssociationTask todelete : tasksToDelete){
            System.out.println("**********************************");
            System.out.println("to delete the activity:" + todelete.getId());
            System.out.println("incong size:" + todelete.getTask().getIncoming().size());
            System.out.println("outgoing size:" + todelete.getTask().getOutgoing().size());

            System.out.println("start delete");
            //create two list to store the source tasks and target tasks
            List<String> sources = new ArrayList<>();
            List<String> targets = new ArrayList<>();
            List<String> toDeleteFlow = new ArrayList<>();


            //add source task id into sources list
            for (SequenceFlow incoming : todelete.getTask().getIncoming()){
//                if (incoming.getTarget().getId() == todelete.getTask().getId()){
                    System.out.println("sources id:" + incoming.getSource().getId());
//                System.out.println("source type" + incoming.getSource());
                    sources.add(incoming.getSource().getId());
//                    sources.add(incoming.getTextContent());
                    toDeleteFlow.add(incoming.getId());
//                }
            }

            //add target task id into targets list
            for (SequenceFlow outgoing : todelete.getTask().getOutgoing()){
//                if (outgoing.getSource().getId() == todelete.getTask().getId()){
                    System.out.println("targets id:" + outgoing.getTarget().getId());
//                    targets.add(outgoing.getTextContent());
                    targets.add(outgoing.getTarget().getId());
                    toDeleteFlow.add(outgoing.getId());
//                }
            }
            System.out.println("before delete sequence flow: "+ modelInstance.getModelElementsByType(SequenceFlow.class).size());

            for (String id : toDeleteFlow){
                //delete flow in bpmn
                modelInstance.getModelElementById(id).getParentElement().removeChildElement(modelInstance.getModelElementById(id));
                //delete flow in graph
                modelInstance.getModelElementById(id+"_di").getParentElement().removeChildElement(modelInstance.getModelElementById(id+"_di"));
            }
            System.out.println("after delete sequence flows:  " + modelInstance.getModelElementsByType(SequenceFlow.class).size());

            System.out.println("sourcr list :" + sources.size());
            System.out.println("target list :" + targets.size());

//            System.out.println(modelInstance.getModelElementById("Activity_1mvshxv").getElementType().getTypeName());

            //connect source and target one to one
            for (String src : sources){
                for (String tgt : targets){
//                    System.out.println(modelInstance.getModelElementById("Activity_1mvshxv").getElementType().getTypeName());
                    SequenceFlow src2tgt = modelInstance.newInstance(SequenceFlow.class);
//                    System.out.println(modelInstance.getModelElementById("Activity_1mvshxv").getElementType().getTypeName());
                    String src2tgtID;
                    do {
                         src2tgtID = Generate7ID.generate();
                    }while ( modelInstance.getModelElementById("Flow_" + src2tgtID) != null);
                    src2tgt.setId("Flow_" + src2tgtID);

                    System.out.println("set new flow id:"+src2tgt.getId());
                    System.out.println("set sourceRef and targetRef");
                    System.out.println(src);
                    System.out.println(tgt);

                    src2tgt.setTarget(modelInstance.getModelElementById(tgt));
//                    System.out.println("setTarget " + modelInstance.getModelElementById(src));
//                    System.out.println("afterset Target:" + src2tgt.getTarget().getId());
                    src2tgt.setSource(modelInstance.getModelElementById(src));
                    modelInstance.getModelElementsByType(Process.class).iterator().next().addChildElement(src2tgt);

                    //add outgoing
                    if(modelInstance.getModelElementById(src) instanceof StartEvent){
                        StartEvent srcAc = modelInstance.getModelElementById(src);
                        srcAc.getOutgoing().add(src2tgt);
                    }else if (modelInstance.getModelElementById(src) instanceof Gateway){
                        Gateway srcAc = modelInstance.getModelElementById(src);
                        srcAc.getOutgoing().add(src2tgt);
                    }else {
                        Task srcAc = modelInstance.getModelElementById(src);
                        srcAc.getOutgoing().add(src2tgt);
                    }

                    //add incoming
                    if ( modelInstance.getModelElementById(tgt) instanceof EndEvent){
                        EndEvent tgtAc = modelInstance.getModelElementById(tgt);
                        tgtAc.getIncoming().add(src2tgt);
                    }else if (modelInstance.getModelElementById(tgt) instanceof  Gateway){
                        Gateway tgtAc = modelInstance.getModelElementById(tgt);
                        tgtAc.getIncoming().add(src2tgt);
                    }else {
                        Task tgtAc = modelInstance.getModelElementById(tgt);
                        tgtAc.getIncoming().add(src2tgt);
                    }


                    System.out.println("after add sequence flow in bpmn: " + modelInstance.getModelElementsByType(SequenceFlow.class).size());;

                    System.out.println("start add sequence flow in graph...");

                    // Get the BPMN diagram instance
                    BpmnPlane plane = modelInstance.getModelElementsByType(BpmnPlane.class).iterator().next();

                    System.out.println(src2tgt.getAttributeValue("targetRef"));

                    BpmnEdge edge = createEdgeForNewSequenceFlow(modelInstance, src2tgt, src, tgt);

                    plane.addChildElement(edge);


                }
            }
        }


        //删除task
        for (NoAssociationTask task : tasksToDelete){
            modelInstance.getModelElementById(task.getId()).getParentElement().removeChildElement(modelInstance.getModelElementById(task.getId()));
            modelInstance.getModelElementById(task.getId()+"_di").getParentElement().removeChildElement(modelInstance.getModelElementById(task.getId()+"_di"));

        }
        System.out.println("end delete");
        for (Task task: modelInstance.getModelElementsByType(Task.class)){
            System.out.println(task.getId());
        }
        for (SequenceFlow sequenceFlow : modelInstance.getModelElementsByType(SequenceFlow.class)){
            System.out.println(sequenceFlow.getId());
            System.out.println(sequenceFlow.getSource().getId());
            System.out.println(sequenceFlow.getTarget().getId());
        }

    }


    public static BpmnEdge createEdgeForNewSequenceFlow(BpmnModelInstance modelInstance, SequenceFlow sequenceflow, String src, String tgt){
        BpmnEdge edge = modelInstance.newInstance(BpmnEdge.class);
//        BpmnEdge edge = modelInstance.getModelElementsByType(BpmnEdge.class).iterator().next();
        edge.setId(sequenceflow.getId() + "_di");
        edge.setBpmnElement(modelInstance.getModelElementById(sequenceflow.getId()));

        Waypoint waypoint1 = modelInstance.newInstance(Waypoint.class);
        Bounds bound1 = modelInstance.getModelElementById(src+"_di").getChildElementsByType(Bounds.class).iterator().next();
        waypoint1.setX(bound1.getX() + bound1.getWidth());
        waypoint1.setY(bound1.getY() + bound1.getHeight()/2);

        Waypoint waypoint2 = modelInstance.newInstance(Waypoint.class);
        Bounds bound2 = modelInstance.getModelElementById(tgt + "_di").getChildElementsByType(Bounds.class).iterator().next();
        waypoint2.setX(bound2.getX()  );
        waypoint2.setY(bound2.getY() + bound2.getHeight()/2);

        edge.getWaypoints().add(waypoint1);
        edge.getWaypoints().add(waypoint2);

        edge.setId(sequenceflow.getId() + "_di");
        edge.setBpmnElement(modelInstance.getModelElementById(sequenceflow.getId()));
//        System.out.println(modelInstance.getModelElementById(sequenceflow.getAttributeValue("sourceRef")));
//        edge.setTargetElement(modelInstance.getModelElementById(sequenceflow.getAttributeValue("sourceRef")));
//        edge.setSourceElement(modelInstance.getModelElementById(sequenceflow.getAttributeValue("targetRef")));

        return edge;
    }
}
