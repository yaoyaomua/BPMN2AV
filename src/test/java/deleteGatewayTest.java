import Step3_Delete_Element.Delete121Gateway;
import Step3_Delete_Element.DeleteElement;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.Gateway;
import org.junit.Test;

import java.io.File;

public class deleteGatewayTest {

    @Test
    public void gatewaytest1(){
        BpmnModelInstance bpmnModelInstance = Bpmn.readModelFromFile(new File("models/diagram5.bpmn"));
        for (Gateway gateway: bpmnModelInstance.getModelElementsByType(Gateway.class)){
            System.out.println(gateway.getId());
        }
    }

    @Test
    public void gatewayTest2(){
        try {
            BpmnModelInstance bpmnModelInstance = Bpmn.readModelFromFile(new File("models/diagram (7).bpmn"));
            Delete121Gateway.delete(bpmnModelInstance);
            File outputFile = new File("models/output1.bpmn");
            Bpmn.writeModelToFile(outputFile, bpmnModelInstance);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
