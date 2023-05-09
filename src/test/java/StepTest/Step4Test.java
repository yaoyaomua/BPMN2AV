package StepTest;

import Step4_Well_Structure.*;
import Step4_Well_Structure.BPStruct.BPMNReader;
import Step4_Well_Structure.BPStruct.JSON2BPMN;
import de.hpi.bpt.process.*;
import de.hpi.bpt.process.Process;
import ee.ut.bpstruct2.Restructurer;
import ee.ut.bpstruct2.util.BPMN2Reader;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
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
        File file = new File("models/diagram (81).bpmn");
        BpmnModelInstance modelInstance = Bpmn.readModelFromFile(file);
        DeleteParalleGatewaySequenceFlow.delete(modelInstance);
        File stepoutput= new File("models/delete.bpmn");
        Bpmn.writeModelToFile(stepoutput, modelInstance);

        Delete121Gateway.delete(modelInstance);
        Process process = BPMNReader.parse(modelInstance);
//        DataObjectStore.store(new File("models/acyclic/model9214.bpmn"));
//        BpmnModelInstance modelInstance = Bpmn.readModelFromFile(file);
//        Map<String, MyDataObject> datamap = DataObjectStore.storeinname(modelInstance);
        process.setName("process7817");
        Restructurer str = new Restructurer(process);
        if (str.perform()) {
            try {
                String filename = "models/acyclic/result1.JSON";
                PrintStream out = new PrintStream(filename);
                JSONObject json = de.hpi.bpt.process.serialize.Process2JSON.convert(str.proc);
                out.print(json);
                out.close();

                JSON2BPMN.store(modelInstance,str.proc);
                File step3output= new File("models/bpstruct1.bpmn");
                Bpmn.writeModelToFile(step3output, modelInstance);

//                DataObjectAddToJSON.addDataObject(json,datamap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Model cannot be restructured");
        }

    }

//    public static Process parse(File file) throws JDOMException, IOException {
//        Namespace BPMN2NS = Namespace.getNamespace("http://schema.omg.org/spec/BPMN/2.0");
//        Document doc = (new SAXBuilder()).build(file);
//        Process proc = new Process();
//        Element procElem = doc.getRootElement().getChild("process", BPMN2NS);
//        if (procElem == null) {
//            BPMN2NS = Namespace.getNamespace("http://www.omg.org/spec/BPMN/20100524/MODEL");
//            procElem = doc.getRootElement().getChild("process", BPMN2NS);
//        }
//
//        initProcess(proc, procElem, BPMN2NS);
//        return proc;
//    }

//    protected static void initProcess(Process proc, Element procElem, Namespace BPMN2NS) {
//        Map<String, Node> nodes = new HashMap();
//        List<Element> edges = new LinkedList();
//        Iterator var6 = procElem.getChildren().iterator();
//
//        while(true) {
//            while(true) {
//                Object obj;
//                Task task;
//                do {
//                    if (!var6.hasNext()) {
//                        var6 = edges.iterator();
//
//                        while(var6.hasNext()) {
//                            Element edge = (Element)var6.next();
//                            Node src = (Node)nodes.get(edge.getAttributeValue("sourceRef"));
//                            Node tgt = (Node)nodes.get(edge.getAttributeValue("targetRef"));
//                            if (src == null || tgt == null) {
//                                throw new RuntimeException("Malformed graph");
//                            }
//
//                            ControlFlow flow = proc.addControlFlow(src, tgt);
//                            if (flow != null) {
//                                task = null;
//                                Element expr = edge.getChild("conditionExpression", BPMN2NS);
//                                String label;
//                                if (expr != null) {
//                                    label = expr.getText();
//                                } else {
//                                    label = "";
//                                }
//
//                                flow.setLabel(label);
//                            }
//                        }
//
//                        return;
//                    }
//
//                    obj = var6.next();
//                } while(!(obj instanceof Element));
//
//                Element elem = (Element)obj;
//                String id = elem.getAttributeValue("id");
//                if (id == null || id.isEmpty()) {
//                    System.out.println("oops");
//                }
//
//                String name = elem.getAttributeValue("name");
//                if (elem.getName().equals("exclusiveGateway") || elem.getName().equals("parallelGateway") || elem.getName().equals("inclusiveGateway") || elem.getName().equals("sequenceFlow") ) {
//                    Gateway gateway;
//                    if (elem.getName().equals("exclusiveGateway")) {
//                        gateway = new Gateway(GatewayType.XOR, name);
//                        gateway.setId(id);
//                        nodes.put(id, gateway);
//                    } else if (elem.getName().equals("parallelGateway")) {
//                        gateway = new Gateway(GatewayType.AND, name);
//                        gateway.setId(id);
//                        nodes.put(id, gateway);
//                    } else if (elem.getName().equals("inclusiveGateway")) {
//                        gateway = new Gateway(GatewayType.OR, name);
//                        gateway.setId(id);
//                        nodes.put(id, gateway);
//                    } else if (elem.getName().equals("sequenceFlow")) {
//                        edges.add(elem);
//                    }
//                } else {
//                    task = new Task(name);
//                    task.setId(id);
//                    nodes.put(task.getId(), task);
//                }
//            }
//        }
//    }
}
