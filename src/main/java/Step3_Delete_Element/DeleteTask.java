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
        List<NoAssociationTask> tasksToDelete = new ArrayList<>();
        BpmnPlane plane = modelInstance.getModelElementsByType(BpmnPlane.class).iterator().next();

        //get attached boundary event task list
        //these tasks would be remained
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
                    //note: task will never be connected with data object without state, so we dont need to check the state of the data object
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
//        System.out.println();

        for (NoAssociationTask todelete : tasksToDelete){
            if (modelInstance.getModelElementById(todelete.getId()) instanceof SubProcess) continue;
            //create two list to store the source tasks and target tasks
            List<String> sources = new ArrayList<>();
            List<String> targets = new ArrayList<>();
            List<String> toDeleteFlow = new ArrayList<>();
            HashMap<String,SequenceFlow> flows = new HashMap<>();

            //add source task id into sources list
            for (SequenceFlow incoming : todelete.getTask().getIncoming()){
//                    System.out.println("sources id:" + incoming.getSource().getId());
                    sources.add(incoming.getSource().getId());
                    flows.put(incoming.getSource().getId(),incoming);
                    toDeleteFlow.add(incoming.getId());
            }

            //add target task id into targets list
            for (SequenceFlow outgoing : todelete.getTask().getOutgoing()){
//                    System.out.println("targets id:" + outgoing.getTarget().getId());
                    targets.add(outgoing.getTarget().getId());
                    flows.put(outgoing.getTarget().getId(),outgoing);
                    toDeleteFlow.add(outgoing.getId());
            }

            for (String id : toDeleteFlow){
                modelInstance.getModelElementById(id).getParentElement().removeChildElement(modelInstance.getModelElementById(id));
            }

            //connect source and target one to one
            for (String src : sources){
                for (String tgt : targets){
                    //creat a new sequence flow
                    SequenceFlow src2tgt = modelInstance.newInstance(SequenceFlow.class);
                    src2tgt.setId(GenerateID.getID("Flow_",modelInstance));
                    //set source ref and target ref of this flow
                    src2tgt.setTarget(modelInstance.getModelElementById(tgt));
                    src2tgt.setSource(modelInstance.getModelElementById(src));
                    src2tgt.setName(CombineSequenceFlowCondition.combine(flows.get(src),flows.get(tgt)));


                    //change this part is because we could add the new flow the same process of the remove one
                    todelete.getTask().getParentElement().addChildElement(src2tgt);

                    //add incoming and outgoing
                    src2tgt.getTarget().getIncoming().add(src2tgt);
                    src2tgt.getSource().getOutgoing().add(src2tgt);

                    // Get the BPMN diagram instance
                    Bounds bounds1 = GetBounds.get(modelInstance,src2tgt.getSource().getId());
//                    System.out.println(src2tgt.getTarget().getId());
                    Bounds bounds2 = GetBounds.get(modelInstance,src2tgt.getTarget().getId());

                    //create new edge for new flow
                    BpmnEdge edge = CreateBPMNEdge.create(modelInstance,src2tgt,
                            bounds1.getX() + bounds1.getWidth()/2, bounds1.getY() + bounds1.getHeight()/2,
                            bounds2.getX() + bounds2.getWidth()/2, bounds2.getY() + bounds2.getHeight()/2);

                    //add new edge to diagram
                    plane.addChildElement(edge);
                }
            }
        }

        //delete task
        for (NoAssociationTask task : tasksToDelete){
            modelInstance.getModelElementById(task.getId()).getParentElement().removeChildElement(modelInstance.getModelElementById(task.getId()));
        }

        //delete useless association, shape, edge
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
