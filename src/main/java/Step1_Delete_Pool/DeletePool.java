package Step1_Delete_Pool;

import java.util.ArrayList;
import java.util.Collection;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.Collaboration;
import org.camunda.bpm.model.bpmn.instance.Definitions;
import org.camunda.bpm.model.bpmn.instance.MessageFlow;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;

public class DeletePool {

    public DeletePool() {
    }

    //Delete Pool
    public static Collection<MessageFlow> delete(BpmnModelInstance modelInstance){
        //Definitions
        Definitions definitions = modelInstance.getDefinitions();
        //Collaboration
        Collaboration collaboration = modelInstance.getModelElementsByType(Collaboration.class).iterator().next();
        //Get all messageflow
        Collection<MessageFlow> messageflows = collaboration.getMessageFlows();

        Collaboration collaborationToDelete = null;
        for (ModelElementInstance element : definitions.getRootElements()) {
            if (element instanceof Collaboration) {
                Collaboration collaboration_element = (Collaboration) element;
                collaborationToDelete = collaboration_element;
            }

        }
        // delete pool
        if (collaborationToDelete != null) {
            collaborationToDelete.getParentElement().removeChildElement(collaborationToDelete);
        }

        return messageflows;

    }
}
