package Step3_Delete_Element;

import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.Task;

import java.util.Collection;

public class NoAssociationTask {

    private String id;
    private Task task;



    public NoAssociationTask(String id, Task task) {
        this.id = id;
        this.task = task;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

}
