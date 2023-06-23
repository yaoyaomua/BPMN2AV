# Generate Artifact-oriented Process Diagaram From BPMN Collaboration Diagram

This project provide a prototype for automatically generating artifact-oriented process diagram. This project is a part of configuration for BPM process monitoring platform.

## Installation and Transformation

### Environment
1. Programming Language: The deriving part was implemented using Java 17
2. Integrated Development Environment (IDE): The chosen IDE for the development of this project was IntelliJ IDEA.
3. BPMN Engine: The project utilizes the Camunda API for manipulating BPMN files.
4. BPMN File Standard: The input to the project must conform to the BPMN 2.0 standard.

### Input Conditions
1. The BPMN collaboration diagram is enriched with data objects.
2. The artifact that needs to generate the process view must be deterministic.
3. The input BPMN collaboration diagram must be sound. The soundness of the BPMN diagram can be described as having three basic characteristics: (i) a process instance can complete for any situation once the execution starts, (ii) when an instance of the process ends, there must be no activity still running or enabled, and (iii) the process model can not contain a dead activity that can not run for all the traces.

### Installation
1. Download the code from <https://github.com/yaoyaomua/BPMN2ArtifactViewProcess>.
2. Open project in IntelliJ IDEA.
3. Import required libraries for BPStruct.

### Transformation Steps
1. Input enriched BPMN diagram. It is recommended to be placed in /BPMN2ArtifactViewProcess/models
2. Change the filePath( the path of the input BPMN diagram), artifact(the name of the artifact), step3output(the path of the result BPMN diagram) in /BPMN2ArtifactViewProcess/src/test/java/StepTest/AllStepTest.java and run.
3. Get new diagram and check it in Camunda Modeler, Signavio or Bpmn.io.

## Related Link
- Projet Github Link: <https://github.com/yaoyaomua/BPMN2ArtifactViewProcess>
- BPStruct Link: <https://code.google.com/archive/p/bpstruct/>
- SMATifact Link: <https://link.springer.com/book/10.1007/978-3-030-32412-4>

## About
This project is a part of DTU graduation thesis.
