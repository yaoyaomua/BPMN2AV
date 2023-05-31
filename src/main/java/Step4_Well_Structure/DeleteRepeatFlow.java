package Step4_Well_Structure;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;

import java.util.*;

public class DeleteRepeatFlow {
    public static void delete(BpmnModelInstance modelInstance) {
        List<String> allSequenceFlow = new ArrayList<>();
        Map<String, Collection<String>> map = new HashMap<String, Collection<String>>();
        Set<Collection<String>> seenValues = new HashSet<Collection<String>>();
        List<String> keysToRemove = new ArrayList<String>();
        for (SequenceFlow sequenceFlow: modelInstance.getModelElementsByType(SequenceFlow.class)){
            Collection<String> ref = new ArrayList<>();
            allSequenceFlow.add(sequenceFlow.getId());
            System.out.println(sequenceFlow.getId());
            ref.add(sequenceFlow.getSource().getId());
            ref.add(sequenceFlow.getTarget().getId());
            map.put(sequenceFlow.getId(), ref);
        }
        for (Map.Entry<String, Collection<String>> entry : map.entrySet()) {
            Collection<String> value = entry.getValue();
            if (seenValues.contains(value)) {
                keysToRemove.add(entry.getKey());
            }
            else {
                seenValues.add(value);
            }
        }
        for (String removeFlow : keysToRemove) {
            SequenceFlow removeSequenceFlow = modelInstance.getModelElementById(removeFlow);
            modelInstance.getModelElementById(removeFlow).getParentElement().removeChildElement(removeSequenceFlow);
        }

    }
}
