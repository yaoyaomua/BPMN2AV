package Step4_Well_Structure;


import de.hpi.bpt.process.ControlFlow;
import de.hpi.bpt.process.Gateway;
import de.hpi.bpt.process.GatewayType;
import de.hpi.bpt.process.Node;
import de.hpi.bpt.process.Process;
import de.hpi.bpt.process.Task;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

public class BPMN2Reader2 {
    public BPMN2Reader2() {
    }

    public static Process parse(File file) throws JDOMException, IOException {
        Namespace BPMN2NS = Namespace.getNamespace("http://schema.omg.org/spec/BPMN/2.0");
        Document doc = (new SAXBuilder()).build(file);
        Process proc = new Process();
        Element procElem = doc.getRootElement().getChild("process", BPMN2NS);
        if (procElem == null) {
            BPMN2NS = Namespace.getNamespace("http://www.omg.org/spec/BPMN/20100524/MODEL");
            procElem = doc.getRootElement().getChild("process", BPMN2NS);
        }

        initProcess(proc, procElem, BPMN2NS);
        return proc;
    }

//    protected static void storeInfo(Process proc, Element procElem, Namespace BPMN2NS){
//
//    }

    protected static void initProcess(Process proc, Element procElem, Namespace BPMN2NS) {
        Map<String, Node> nodes = new HashMap();
        List<Element> edges = new LinkedList();
        Iterator var6 = procElem.getChildren().iterator();

        while(true) {
            while(true) {
                Object obj;
                Task task;
                do {
                    if (!var6.hasNext()) {
                        var6 = edges.iterator();

                        while(var6.hasNext()) {
                            Element edge = (Element)var6.next();
                            Node src = (Node)nodes.get(edge.getAttributeValue("sourceRef"));
                            Node tgt = (Node)nodes.get(edge.getAttributeValue("targetRef"));
                            if (src == null || tgt == null) {
                                throw new RuntimeException("Malformed graph");
                            }

                            ControlFlow flow = proc.addControlFlow(src, tgt);
                            if (flow != null) {
                                task = null;
                                Element expr = edge.getChild("conditionExpression", BPMN2NS);
                                String label;
                                if (expr != null) {
                                    label = expr.getText();
                                } else {
                                    label = "";
                                }

                                flow.setLabel(label);
                            }
                        }

                        return;
                    }

                    obj = var6.next();
                } while(!(obj instanceof Element));

                Element elem = (Element)obj;
                String id = elem.getAttributeValue("id");

                System.out.println(id);

                if (id == null || id.isEmpty()) {
                    System.out.println("oops");
                }

                String name = elem.getAttributeValue("name");
                if (!elem.getName().equals("task") && !elem.getName().equals("startEvent") && !elem.getName().equals("endEvent")) {
                    Gateway gateway;
                    if (elem.getName().equals("exclusiveGateway")) {
                        gateway = new Gateway(GatewayType.XOR, name);
                        gateway.setId(id);
                        nodes.put(id, gateway);
                    } else if (elem.getName().equals("parallelGateway")) {
                        gateway = new Gateway(GatewayType.AND, name);
                        gateway.setId(id);
                        nodes.put(id, gateway);
                    } else if (elem.getName().equals("inclusiveGateway")) {
                        gateway = new Gateway(GatewayType.OR, name);
                        gateway.setId(id);
                        nodes.put(id, gateway);
                    } else if (elem.getName().equals("sequenceFlow")) {
                        edges.add(elem);
                    }
                } else {
                    task = new Task(name);
                    task.setId(id);
                    nodes.put(task.getId(), task);
                }
            }
        }
    }
}

