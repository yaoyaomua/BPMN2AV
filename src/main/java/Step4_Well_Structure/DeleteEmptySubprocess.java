package Step4_Well_Structure;

import de.hpi.bpt.process.epc.IProcessInterface;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.SubProcess;

import java.util.Collection;
import java.util.stream.Collectors;

public class DeleteEmptySubprocess {
    public static void delete(BpmnModelInstance modelInstance) {
        Collection<SubProcess> emptySubProcesses = modelInstance.getModelElementsByType(SubProcess.class).stream().filter(subProcess -> subProcess.getFlowElements().isEmpty()).collect(Collectors.toList());
        //remove empty subprocess
        if (emptySubProcesses.size()!=0) {
            emptySubProcesses.forEach(emptySubProcess -> {
                SequenceFlow incomingFlow = emptySubProcess.getIncoming().iterator().next();
                SequenceFlow outgoingFlow = emptySubProcess.getOutgoing().iterator().next();
                //reconnect sequence flow
                incomingFlow.getSource().getOutgoing().remove(incomingFlow);
                incomingFlow.getSource().getOutgoing().add(outgoingFlow);
                outgoingFlow.getTarget().getIncoming().remove(outgoingFlow);
                outgoingFlow.getTarget().getIncoming().add(incomingFlow);

                outgoingFlow.setSource(incomingFlow.getSource());
                incomingFlow.getParentElement().removeChildElement(incomingFlow);


                emptySubProcess.getParentElement().removeChildElement(emptySubProcess);
            });
        }
    }
}
