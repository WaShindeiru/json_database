package client.request;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.Test;

public class RequestDeserializeTest {

    private RequestDeserializer deserializer = new RequestDeserializer();
    private Gson gson = new GsonBuilder().registerTypeAdapter(Request.class, new RequestDeserializer()).create();

    @Test
    public void testDeserialize() {
        String testJson2 = """
              {
              "type":"set",
              "key":"person",
              "value":{
                  "name":"Elon Musk",
                  "car":{
                      "model":"Tesla Roadster",
                      "year":"2018"
                  },
                  "rocket":{
                      "name":"Falcon 9",
                      "launches":"87"
                  }
                  }
              }""";


        String testJson = """
                {"type": "set","key": ["test", "test33"],"value": {"oho": "3"}}
                """;

        Request request = gson.fromJson(testJson2, Request.class);

        System.out.println(request);
        System.out.println(request.key.getClass());
        System.out.println(request.key);

        System.out.println(request.value);
    }
}