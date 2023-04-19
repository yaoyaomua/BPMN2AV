package Step2_Flow_Transform;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;

import java.util.ArrayList;
import java.util.List;

public class AddSuperStartEndEvent {


    public AddSuperStartEndEvent() {
    }

    public static void add(BpmnModelInstance modelInstance){
        List<String> linkStart = new ArrayList<>();
        List<String> linkEnd = new ArrayList<>();
        ParallelGateway startGateway = modelInstance.newInstance(ParallelGateway.class);
        ParallelGateway endGateway = modelInstance.newInstance(ParallelGateway.class);

        for (StartEvent event : modelInstance.getModelElementsByType(StartEvent.class)){

        }
        for(EndEvent event: modelInstance.getModelElementsByType(EndEvent.class)){

        }
        for(IntermediateThrowEvent event : modelInstance.getModelElementsByType(IntermediateThrowEvent.class)){

        }
        for (Task task : modelInstance.getModelElementsByType(Task.class)){

        }

        StartEvent startEvent = modelInstance.newInstance(StartEvent.class);
        EndEvent endEvent = modelInstance.newInstance(EndEvent.class);
    }
}
