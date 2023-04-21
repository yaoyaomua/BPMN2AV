package Add_Data_Object;

import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.Task;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class AddDataObejctFromJSON {


    public AddDataObejctFromJSON() {
    }

    public static void add(BpmnModelInstance modelInstance, File file) throws JSONException {
        for (Task task : modelInstance.getModelElementsByType(Task.class)){

        }

        String jsonString = file.toString();
        JSONObject jsonObject = new JSONObject(jsonString);
        JSONArray jsonArray = jsonObject.getJSONArray("DataObjects");
        for (int i = 0; i < jsonArray.length(); i ++){
//            String Name = arr.getJSONObject(i).getString("post_id");
            String name = jsonArray.getJSONObject(i).getString("Name");
            String state = jsonArray.getJSONObject(i).getString("State");
            String sourceRef = jsonArray.getJSONObject(i).getString("SourceRef");
            String targetRef = jsonArray.getJSONObject(i).getString("TargetRef");


        }
    }
}
