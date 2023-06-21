package Step4_Well_Structure;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.impl.instance.Outgoing;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;

import java.util.*;

public class DeleteRepeatFlow {
    public static boolean delete(BpmnModelInstance modelInstance) {
//        System.out.println("delete start");
        List<String> allSequenceFlow = new ArrayList<>();
        Map<String, Collection<String>> map = new HashMap<String, Collection<String>>();
        Set<Collection<String>> seenValues = new HashSet<Collection<String>>();
        List<String> keysToRemove = new ArrayList<String>();

        Map<String, Collection<String>> remainmap = new HashMap<String, Collection<String>>();
        List<String> keysToRemain = new ArrayList<String>();
        for (SequenceFlow sequenceFlow: modelInstance.getModelElementsByType(SequenceFlow.class)){
            Collection<String> ref = new ArrayList<>();
            allSequenceFlow.add(sequenceFlow.getId());
//            System.out.println(sequenceFlow.getId());
            ref.add(sequenceFlow.getSource().getId());
            ref.add(sequenceFlow.getTarget().getId());
            ref.add(sequenceFlow.getName());
            map.put(sequenceFlow.getId(), ref);
        }
        for (Map.Entry<String, Collection<String>> entry : map.entrySet()) {
            Collection<String> value = entry.getValue();
            if (seenValues.contains(value)) {
                keysToRemove.add(entry.getKey());
            }
            else {
                seenValues.add(value);
                keysToRemain.add(entry.getKey());

            }
        }

        List<String> keysToCheck = new ArrayList<String>();
        for (String remainSequenceId: keysToRemain)
        {
            Collection<String> value = map.get(remainSequenceId);
            SequenceFlow sequenceFlow = modelInstance.getModelElementById(remainSequenceId);
            String newCondition = sequenceFlow.getName();
            if (keysToCheck.contains(remainSequenceId))
            {
                continue;
            }
            else
            {
                keysToCheck.add(remainSequenceId);
            }
            for (String checkSequenceId : keysToRemain)
            {
                Collection<String> checkvalue = map.get(checkSequenceId);
                SequenceFlow checkSequenceFlow = modelInstance.getModelElementById(checkSequenceId);
                String checkCondition = checkSequenceFlow.getName();

                if (keysToCheck.contains(checkSequenceId))
                {
                    continue;
                }
                if ((value.toArray()[0] == checkvalue.toArray()[0])&&(value.toArray()[1] == checkvalue.toArray()[1]))
                {
                    // merge condition
                    if (newCondition != null && checkCondition != null) {
                        newCondition = newCondition + " or " + checkCondition;
                    }
                    else
                    {
                        newCondition = null;
                    }
                    keysToCheck.add(checkSequenceId);
                    keysToRemove.add(checkSequenceId);
                }

            }
            sequenceFlow.setName(newCondition);

        }

        if (keysToRemove.size() == 0) return false;
        for (String removeFlow : keysToRemove) {
            SequenceFlow removeSequenceFlow = modelInstance.getModelElementById(removeFlow);
            modelInstance.getModelElementById(removeFlow).getParentElement().removeChildElement(removeSequenceFlow);
        }


        return true;
    }
}
