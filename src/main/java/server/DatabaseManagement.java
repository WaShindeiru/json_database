package server;

import client.Request;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.io.IOException;

public class DatabaseManagement {

    private DatabaseFile database;
    private Gson gson;

    public DatabaseManagement() {
        database = new DatabaseFile();
        gson = new Gson();
    }

    public String takeActionJson(Request request) {

        String command = request.type;
        JsonElement key = request.key;
        JsonElement value = request.value;
        String result = "";
        Response response;

        JsonArray keyArray = new JsonArray();

        // key is a single value, transform it into array
        if (key != null && key.isJsonPrimitive()) {
            String keyString = key.getAsString();
            keyArray = new JsonArray();
            keyArray.add(gson.fromJson(keyString, JsonElement.class));
        } else if (key != null && key.isJsonArray()) {
            keyArray = key.getAsJsonArray();
        }

        try {
            switch (command) {
                case "set":
                    database.set(keyArray, value);
                    response = new Response("OK", null, null);
                    result = gson.toJson(response);
                    break;

                case "delete":
                    database.delete(keyArray);
                    response = new Response("OK", null, null);
                    result = gson.toJson(response);
                    break;

                case "get":
                    JsonElement temp = database.get(keyArray);
                    response = new Response("OK", temp, null);
                    result = gson.toJson(response);
                    break;

                case "exit":
                    response = new Response("OK", null, null);
                    result = gson.toJson(response);
                    break;

                default:
                    response = new Response("ERROR", null, null);
                    result = gson.toJson(response);
            }

        } catch (WrongArgumentException e) {
            response = new Response("ERROR", null, e.getMessage());
            result = gson.toJson(response);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}
