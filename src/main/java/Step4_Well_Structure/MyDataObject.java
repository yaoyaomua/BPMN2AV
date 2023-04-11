package Step4_Well_Structure;

public class MyDataObject{
    private String doID;
    private String dorefID;
    private String doName;
    private String sourceRef;
    private String targetRef;
    private String associationID;
    private String textID;

    public String getTextID() {
        return textID;
    }

    public void setTextID(String textID) {
        this.textID = textID;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    private String text;

    public String getAssociationID() {
        return associationID;
    }

    public void setAssociationID(String associationID) {
        this.associationID = associationID;
    }



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
