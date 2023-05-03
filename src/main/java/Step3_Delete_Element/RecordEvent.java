package Step3_Delete_Element;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;

import java.util.HashSet;

public class RecordEvent {


    public RecordEvent() {
    }

    public static HashSet<String> record(BpmnModelInstance modelInstance){
        String superStart = AddSuperStartEndEvent.addStart(modelInstance);
        String superEnd = AddSuperStartEndEvent.addEnd(modelInstance);
        HashSet<String> sub = AddSubProcessStartEndEvent.add(modelInstance);

        HashSet<String> res = new HashSet<>();
        res.add(superStart);
        res.add(superEnd);
        for(String s : sub){
            res.add(s);
        }
        return res;
    }
}
