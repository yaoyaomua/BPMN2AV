package Step3_Delete_Element;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;

import java.util.List;

public class ElementToDelete {
    public List<NoAssociationTask> taskstodelete;
    public BpmnModelInstance modelInstance;
    public BpmnModelInstance getModelInstance() {
        return modelInstance;
    }

    public void setModelInstance(BpmnModelInstance modelInstance) {
        this.modelInstance = modelInstance;
    }


    public List<NoAssociationTask> getTaskstodelete() {
        return taskstodelete;
    }

    public void setTaskstodelete(List<NoAssociationTask> taskstodelete) {
        this.taskstodelete = taskstodelete;
    }


}
