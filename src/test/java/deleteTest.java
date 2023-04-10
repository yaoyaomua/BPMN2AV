import Step3_Delete_Element.DeleteElement;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.junit.Test;

import java.io.File;

public class deleteTest {

    @Test
    public void test4(){
        try {
            BpmnModelInstance bpmnModelInstance = Bpmn.readModelFromFile(new File("models/diagram (7).bpmn"));
            DeleteElement.delete(bpmnModelInstance);
            File outputFile = new File("models/output1.bpmn");
            Bpmn.writeModelToFile(outputFile, bpmnModelInstance);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
