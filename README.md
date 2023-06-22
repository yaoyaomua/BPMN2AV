# Generate Artifact-oriented Process Diagaram From BPMN Collaboration Diagram

This project provide a prototype for automatically generating artifact-oriented process diagram. This project is a part of configuration for BPM process monitoring platform.

## Installation and Transformation

### Environment
1. Programming Language: The deriving part was implemented using Java 17, the latest Long-Term Support (LTS) version at the time of development, which was chosen for its improved performance, advanced language features, strong encapsulation, and enhanced security.
2. Integrated Development Environment (IDE): The chosen IDE for the development of this project was IntelliJ IDEA. IDEA has advanced capabilities for code editing, navigation, refactoring, and analysis, making it an ideal choice for efficient and productive development.
3. BPMN Engine: The project utilizes the Camunda API for manipulating BPMN files. Camunda is a flexible framework for workflow and decision automation that brings the power of BPMN to the Java environment.
4. BPMN File Standard: The input to the project must conform to the BPMN 2.0 standard. BPMN 2.0 (Business Process Model and Notation) is a standard for business process modeling, which provides a graphical notation for specifying business processes in a business process diagram. It's widely accepted, and the usage of BPMN 2.0 ensures interoperability and standardization.

### Input Conditions
1. The BPMN collaboration diagram is enriched with data objects. Relying on the standard BPMN data objects, the BPMN collaboration diagram can be used to present information about the different artifacts and process interactions. Since the transformation rules are based on the enriched BPMN collaboration diagram, this condition is required for the transformation to work.
2. The artifact that needs to generate the process view must be deterministic. Since in a BPMN collaboration diagram there is at least one artifact with which to interact. Specifying the process diagram for the viewpoint of the required artifact is one of the conditions.
3. The input BPMN collaboration diagram must be sound. The soundness of the BPMN diagram can be described as having three basic characteristics: (i) a process instance can complete for any situation once the execution starts, (ii) when an instance of the process ends, there must be no activity still running or enabled, and (iii) the process model can not contain a dead activity that can not run for all the traces. Soundness is the basic requirement for the original diagram.

### Installation
1. Download the code from <https://github.com/yaoyaomua/BPMN2ArtifactViewProcess>.
2. Open project in IntelliJ IDEA.
3. Import required libraries for BPStruct.

### Transformation
1. Input diagram.
2. Change the location in AllStepTest and run.
3. Get new diagram and check it in Camunda Modeler, Signavio or Bpmn.io.

## Related Link
- Projet Github Link: <https://github.com/yaoyaomua/BPMN2ArtifactViewProcess>
- BPStruct Link: <https://code.google.com/archive/p/bpstruct/>
- SMATifact Link: <https://link.springer.com/book/10.1007/978-3-030-32412-4>

## About
This project is a part of DTU graduation thesis.

