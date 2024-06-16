package server.response;

import com.google.gson.JsonElement;

public class Response {

    public String response = null;
    public JsonElement value = null;
    public String reason = null;

    public Response(String response, JsonElement value, String reason) {
        this.response = response;
        this.value = value;
        this.reason = reason;
    }
}
