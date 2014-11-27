package tmpkg;

import java.io.StringWriter;
import javax.json.Json;
import javax.json.JsonObject;

public class TwitterJson {
	private JsonObject json;
	
	public TwitterJson(JsonObject json) {
		this.json = json;
	}
	
	public JsonObject getJson() {
		return json;
	}
	
	public void setJson(JsonObject json) {
        this.json = json;
    }
	
	@Override
    public String toString(){
        StringWriter writer = new StringWriter();
 
        Json.createWriter(writer).write(json);
 
        return writer.toString();
    }
}
