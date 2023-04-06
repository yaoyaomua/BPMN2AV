package Step4_Well_Structure;

import de.hpi.bpt.process.serialize.SerializationException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class DataObjectAddToJSON {
    public static void addDataObject(JSONObject json, Map<String,MyDataObject> datamap) throws JSONException, SerializationException {
        try {
            JSONArray data_objects = new JSONArray();
            for (Map.Entry<String, MyDataObject> myDataObjectEntry : datamap.entrySet()) {
                JSONObject jData = new JSONObject();
                jData.put("id", myDataObjectEntry.getValue().getDoID());
                jData.put("label", myDataObjectEntry.getValue().getDoName());
                jData.put("refid", myDataObjectEntry.getValue().getDorefID());
                jData.put("src", myDataObjectEntry.getValue().getSourceRef());
                jData.put("tgt", myDataObjectEntry.getValue().getTargetRef());
                System.out.println(jData);
                data_objects.put(jData);
            }
            json.put("data_objects",data_objects);
        }
        catch(JSONException e) {
            throw new SerializationException(e.getMessage());
        }
    }
}
