package StepTest;

import Step4_Well_Structure.*;
import Step4_Well_Structure.BPStruct.BPMNReader;
import Step4_Well_Structure.BPStruct.BoundaryEventForBPStruct;
import Step4_Well_Structure.BPStruct.JSON2BPMN;
import de.hpi.bpt.process.*;
import de.hpi.bpt.process.Process;
import ee.ut.bpstruct2.Restructurer;
import ee.ut.bpstruct2.util.BPMN2Reader;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.BoundaryEvent;
import org.camunda.bpm.model.bpmn.instance.IntermediateThrowEvent;
import org.camunda.bpm.model.bpmn.instance.bpmndi.BpmnShape;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.json.JSONObject;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.*;

public class Step4Test {

    @Test
    public void test1() throws Exception {
//        File file = new File("models/acyclic/model7818.bpmn");
        File file = new File("models/output.bpmn");
        BpmnModelInstance modelInstance = Bpmn.readModelFromFile(file);
        HashMap<BoundaryEvent, BpmnShape> shapes = BoundaryEventForBPStruct.store(modelInstance);
        HashMap<IntermediateThrowEvent,BoundaryEvent> events = BoundaryEventForBPStruct.pre(modelInstance);
        Process process = BPMNReader.parse(modelInstance);
        File stepoutput= new File("models/delete.bpmn");
        Bpmn.writeModelToFile(stepoutput, modelInstance);
        process.setName("process7817");
        Restructurer str = new Restructurer(process);
        if (str.perform()) {
            try {
//                String filename = "models/acyclic/result1.JSON";
//                PrintStream out = new PrintStream(filename);
//                JSONObject json = de.hpi.bpt.process.serialize.Process2JSON.convert(str.proc);
//                out.print(json);
//                out.close();
                System.out.println("*******************start transform process to bpmn**********************************");
                JSON2BPMN.store(modelInstance,str.proc);
                System.out.println("*******************end transform process to bpmn**********************************");
                BoundaryEventForBPStruct.after(modelInstance,events,shapes);
                File step3output= new File("models/bpstruct1.bpmn");
                Bpmn.writeModelToFile(step3output, modelInstance);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Model cannot be restructured");
        }

    }
}
