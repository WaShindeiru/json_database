package client.request;

import com.google.gson.*;

import java.lang.reflect.Type;

public class RequestDeserializer implements JsonDeserializer<Request> {


    @Override
    public Request deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        JsonElement requestType = jsonObject.get("type");
        JsonElement key = jsonObject.get("key");
        JsonElement value = jsonObject.get("value");

        return new Request(requestType.getAsString(), key, value);
    }
}
