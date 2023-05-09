package Step3_Delete_Element;

import Step2_Flow_Transform.CreateBPMNEdge;
import Step2_Flow_Transform.GetBounds;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnEdge;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnPlane;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnShape;
import org.camunda.bpm.model.bpmn.instance.dc.Bounds;
import org.camunda.bpm.model.bpmn.instance.di.Waypoint;

import java.util.*;

public class DeleteTask {

    public DeleteTask(){

    }

    public static void delete(BpmnModelInstance modelInstance, String artifact){
//        HashMap<String,String> element2graph = BPMNElement2Graph.map(modelInstance);
//        System.out.println(element2graph.toString());

        List<NoAssociationTask> tasksToDelete = new ArrayList<>();
        BpmnPlane plane = modelInstance.getModelElementsByType(BpmnPlane.class).iterator().next();

        //get attached boundary event task list
        List<String> boundedTask = new ArrayList<>();
        for (BoundaryEvent event : modelInstance.getModelElementsByType(BoundaryEvent.class)){
            boundedTask.add(event.getAttachedTo().getId());
        }

        //mark irrelevant elements by check the Associations of the task
        for(Task task: modelInstance.getModelElementsByType(Task.class)){
            Collection<DataInputAssociation> inputDataAssociations = task.getDataInputAssociations();
            Collection<DataOutputAssociation> outputDataAssociations = task.getDataOutputAssociations();
            boolean isIrr = true;
            for (DataInputAssociation input : inputDataAssociations){
                Collection<ItemAwareElement> sources = input.getSources();
                for (ItemAwareElement itemAwareElement : sources){
                    DataObjectReference dataObjectReference = modelInstance.getModelElementById(itemAwareElement.getId());
                    String dataName = dataObjectReference.getName();
                    //check the data is the artifact or not?
                    if (dataName.equals(artifact)){
//                        System.out.println(dataName.equals(artifact));
                        //this means the task is relevant to the artifact, so that we need to mark it as relevant
                        isIrr = false;
                    }
                }
            }

            for (DataOutputAssociation output : outputDataAssociations){
                String dataId = output.getTarget().getId();
//                Collection<ItemAwareElement> targets = output.getTarget();
                DataObjectReference dataObjectReference = modelInstance.getModelElementById(dataId);
                String dataName = dataObjectReference.getName();
                //check the data is the artifact or not?
                if (dataName.equals(artifact)){
//                    System.out.println(dataName.equals(artifact));
                    isIrr = false;
                }
            }

            //check weather the task has a boundary event or not
            if (boundedTask.contains(task.getId())){
                isIrr = false;
            }

            if (isIrr){
                NoAssociationTask nut = new NoAssociationTask(task.getId(),task);
                tasksToDelete.add(nut);
            }
            System.out.println("activity name ' " + task.getName() + " ' need delete? : " + isIrr);

        }

        System.out.println("number of taske to delete:" + tasksToDelete.size());
        System.out.println();

        for (NoAssociationTask todelete : tasksToDelete){
            System.out.println("**********************************");
            System.out.println("start delete the activity:" + todelete.getId() + " name is: " + todelete.getTask().getName());
//            System.out.println("incong size:" + todelete.getTask().getIncoming().size());
//            System.out.println("outgoing size:" + todelete.getTask().getOutgoing().size());

            //create two list to store the source tasks and target tasks
            List<String> sources = new ArrayList<>();
            List<String> targets = new ArrayList<>();
            List<String> toDeleteFlow = new ArrayList<>();

            //add source task id into sources list
            for (SequenceFlow incoming : todelete.getTask().getIncoming()){
//                    System.out.println("sources id:" + incoming.getSource().getId());
                    sources.add(incoming.getSource().getId());
                    toDeleteFlow.add(incoming.getId());
            }

            //add target task id into targets list
            for (SequenceFlow outgoing : todelete.getTask().getOutgoing()){
//                    System.out.println("targets id:" + outgoing.getTarget().getId());
                    targets.add(outgoing.getTarget().getId());
                    toDeleteFlow.add(outgoing.getId());
            }
            System.out.println("before delete sequence flow number: "+ modelInstance.getModelElementsByType(SequenceFlow.class).size());

            for (String id : toDeleteFlow){
                //delete flow in bpmn
                modelInstance.getModelElementById(id).getParentElement().removeChildElement(modelInstance.getModelElementById(id));
                //delete flow in graph
                //update: we delete flow and shape in graph together in the end part
//                modelInstance.getModelElementById(id+"_di").getParentElement().removeChildElement(modelInstance.getModelElementById(id+"_di"));
                //update hashmap for element to graph
                //we don't need the hashmap anymore
//                element2graph.remove(id);
            }
            System.out.println("after delete sequence flows:  " + modelInstance.getModelElementsByType(SequenceFlow.class).size());
//            System.out.println("sourcr list :" + sources.size());
//            System.out.println("target list :" + targets.size());

            //connect source and target one to one
            for (String src : sources){
                for (String tgt : targets){
                    SequenceFlow src2tgt = modelInstance.newInstance(SequenceFlow.class);
                    src2tgt.setId(GenerateID.getID("Flow_",modelInstance));
                    System.out.println("creat new flow: id: " + src2tgt.getId());

                    src2tgt.setTarget(modelInstance.getModelElementById(tgt));
                    src2tgt.setSource(modelInstance.getModelElementById(src));
                    System.out.println("set sourceRef and targetRef: " + src + " " + tgt);

                    //change this part is because we could add the new flow the the same process of the remove one
                    todelete.getTask().getParentElement().addChildElement(src2tgt);
//                    modelInstance.getModelElementsByType(Process.class).iterator().next().addChildElement(src2tgt);

                    //add incoming and outgoing
                    src2tgt.getTarget().getIncoming().add(src2tgt);
                    src2tgt.getSource().getOutgoing().add(src2tgt);

//                    System.out.println("after add sequence flow in bpmn: " + modelInstance.getModelElementsByType(SequenceFlow.class).size());;
//                    System.out.println("start add sequence flow in graph...");
                    // Get the BPMN diagram instance
                    Bounds bounds1 = GetBounds.get(modelInstance,src2tgt.getSource().getId());
                    Bounds bounds2 = GetBounds.get(modelInstance,src2tgt.getTarget().getId());

                    System.out.println(src2tgt.getAttributeValue("targetRef"));
                    BpmnEdge edge = CreateBPMNEdge.create(modelInstance,src2tgt,
                            bounds1.getX() + bounds1.getWidth()/2, bounds1.getY() + bounds1.getHeight()/2,
                            bounds2.getX() + bounds2.getWidth()/2, bounds2.getY() + bounds2.getHeight()/2);

                    plane.addChildElement(edge);

                }
            }
        }

        //delete task
        for (NoAssociationTask task : tasksToDelete){
            modelInstance.getModelElementById(task.getId()).getParentElement().removeChildElement(modelInstance.getModelElementById(task.getId()));
        }


        System.out.println("**************end delete******************");
        System.out.println("tASK LIST: ");
        for (Task task: modelInstance.getModelElementsByType(Task.class)){
            System.out.println(task.getName());
        }

        for (Association association : modelInstance.getModelElementsByType(Association.class)){
            if (association.getSource() == null){
                association.getParentElement().removeChildElement(association);
            }
        }

        for (BpmnShape bpmnShape : modelInstance.getModelElementsByType(BpmnShape.class)){
            if (bpmnShape.getBpmnElement() == null){
                bpmnShape.getParentElement().removeChildElement(bpmnShape);
            }
        }

        for (BpmnEdge bpmnEdge : modelInstance.getModelElementsByType(BpmnEdge.class)){
            if (bpmnEdge.getBpmnElement() == null){
                bpmnEdge.getParentElement().removeChildElement(bpmnEdge);
            }
        }

    }

}
