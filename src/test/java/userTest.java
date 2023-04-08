import Step4_Well_Structure.BPMN2Reader2;
import Step4_Well_Structure.DataObjectAddToJSON;
import Step4_Well_Structure.DataObjectStore;
import Step4_Well_Structure.MyDataObject;
import de.hpi.bpt.process.Task;
import ee.ut.bpstruct2.Restructurer;
import ee.ut.bpstruct2.util.BPMN2Reader;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.json.JSONObject;
import org.junit.Test;
import de.hpi.bpt.process.Process;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;

public class userTest {


    @Test
    public void test1() throws Exception {
        File file = new File("models/diagram.bpmn");
        Process process = BPMN2Reader.parse(file);
//        DataObjectStore.store(new File("models/acyclic/model9214.bpmn"));
        BpmnModelInstance modelInstance = Bpmn.readModelFromFile(file);
        Map<String, MyDataObject> datamap = DataObjectStore.storeinname(modelInstance);
        process.setName("process7817");
        Restructurer str = new Restructurer(process);
        if (str.perform()) {
            try {
                String filename = "models/acyclic/result1.JSON";
                PrintStream out = new PrintStream(filename);

//                System.out.println(str.proc.getTasks());
//                for (Task task:str.proc.getTasks()){
//                    System.out.println(task.getId());
//                    System.out.println(task.getName());
//                }
                JSONObject json = de.hpi.bpt.process.serialize.Process2JSON.convert(str.proc);
                DataObjectAddToJSON.addDataObject(json,datamap);
                out.print(json);
                out.close();
            } catch (FileNotFoundException var5) {
                var5.printStackTrace();
            }
        } else {
            System.out.println("Model cannot be restructured");
        }
    }

    @Test
    public void test2() throws Exception{
        BpmnModelInstance bpmnModelInstance = Bpmn.readModelFromFile(new File("models/diagram.bpmn"));
        DataObjectStore.storeinname(bpmnModelInstance);

    }
}
