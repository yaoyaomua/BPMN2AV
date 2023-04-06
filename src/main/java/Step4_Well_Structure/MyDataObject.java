package Step4_Well_Structure;

public class MyDataObject{
    private String doID;
    private String dorefID;
    private String doName;
    private String sourceRef;
    private String targetRef;

    public MyDataObject(String doID, String dorefID, String doName) {
        this.doID = doID;
        this.dorefID = dorefID;
        this.doName = doName;
    }

//    public MyDataObject(String dorefID) {
//        this.dorefID = dorefID;
//    }

//    public MyDataObject(String doID) {
//        this.doID = doID;
//    }

    public String getDoID() {
        return doID;
    }

    public void setDoID(String doID) {
        this.doID = doID;
    }

    public String getDorefID() {
        return dorefID;
    }

    public void setDorefID(String dorefID) {
        this.dorefID = dorefID;
    }

    public String getDoName() {
        return doName;
    }

    public void setDoName(String doName) {
        this.doName = doName;
    }

    public String getSourceRef() {
        return sourceRef;
    }

    public void setSourceRef(String sourceRef) {
        this.sourceRef = sourceRef;
    }

    public String getTargetRef() {
        return targetRef;
    }

    public void setTargetRef(String targetRef) {
        this.targetRef = targetRef;
    }


    @Override
    public String toString() {
        return "MyDataObject{" +
                "doID='" + doID + '\'' +
                ", dorefID='" + dorefID + '\'' +
                ", doName='" + doName + '\'' +
                ", sourceRef='" + sourceRef + '\'' +
                ", targetRef='" + targetRef + '\'' +
                '}';
    }
}
