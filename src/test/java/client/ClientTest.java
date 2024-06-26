package client;

import client.request.Request;
import client.request.RequestDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import org.junit.jupiter.api.Test;
import server.storage.FileAccess;
import server.response.Response;
import server.Server;

import java.io.DataInputStream;
import java.io.IOException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.Fail.fail;

public class ClientTest {

   private static String jsonInputPath = "./src/test/resources/";
   private static String databasePath = "./src/test/resources/db.json";
   private static Gson gson = new GsonBuilder().registerTypeAdapter(Request.class, new RequestDeserializer()).create();
   private static FileAccess fileAccess = new FileAccess();

   @Test
   public void test_successful_connection() throws IOException {
      fileAccess.writeToFile(databasePath, "{\"Bulbasaur\": \"grass\"}");

      Client client = new Client(jsonInputPath);
      String[] args = {"-in", "testGet.json"};
      DataInputStream input;

      Server server = new Server(databasePath);
      Thread secondThread = new Thread(() -> server.serve());
      secondThread.start();

      try {
         Thread.sleep(100);
      } catch (InterruptedException e) {
         fail("Thread interrupted");
      }

      client.start();

      input = client.getInput();
      client.makeRequest(args);

      try {
         Thread.sleep(100);
      } catch (InterruptedException e) {
         fail("Thread interrupted");
      }

      String resultString = input.readUTF();
      Response response = gson.fromJson(resultString, Response.class);

      client.cleanUp();

      assertThat(response.value).isEqualTo(new JsonPrimitive("grass"));
      assertThat(response.response).isEqualTo("OK");
   }
}
