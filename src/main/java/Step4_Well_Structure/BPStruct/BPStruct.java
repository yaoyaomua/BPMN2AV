package Step4_Well_Structure.BPStruct;


import de.hpi.bpt.process.Process;
import ee.ut.bpstruct2.Restructurer;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnShape;
import org.jdom.JDOMException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class BPStruct {
    public BPStruct() {
    }

    public static void run(BpmnModelInstance modelInstance) throws IOException, JDOMException {
        System.out.println("start store boundary event information");
        HashMap<BoundaryEvent, BpmnShape> shapes = BoundaryEventForBPStruct.store(modelInstance);
        HashMap<IntermediateThrowEvent,BoundaryEvent> events = BoundaryEventForBPStruct.pre(modelInstance);
        Process process = BPMNReader.parse(modelInstance);
        process.setName("process7817");
        Restructurer str = new Restructurer(process);
        System.out.println("start structure bpmn");
        if (str.perform()) {
            try {
                System.out.println("start output bpmn from process");
                JSON2BPMN.store(modelInstance, str.proc);
                System.out.println("start add boundary information according to records");
                BoundaryEventForBPStruct.after(modelInstance, events, shapes);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
