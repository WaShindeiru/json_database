package client.request;

import com.google.gson.JsonElement;

public class Request {

    public String type = null;
    public JsonElement key = null;
    public JsonElement value = null;

    public Request(String type, JsonElement key, JsonElement value) {
        this.type = type;
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return "Request{" +
                "type='" + type + '\'' +
                ", key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
