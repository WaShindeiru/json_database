package client;

import com.google.gson.*;

import java.lang.reflect.Type;

public class RequestDeserializer implements JsonDeserializer<Request> {


    @Override
    public Request deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        JsonElement requestType = jsonObject.get("type");
        JsonElement key = jsonObject.get("key");
        JsonElement value = jsonObject.get("value");
        Gson gson = new Gson();

        // value is a single value, transform it into an array
        if (value != null && value.isJsonPrimitive()) {
            String valueString = value.getAsString();
            value = gson.fromJson(valueString, JsonElement.class);
        } else if (value != null && value.isJsonObject()) {
            value = value.getAsJsonObject();
        }

        return new Request(requestType.getAsString(), key, value);
    }
}
