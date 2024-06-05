package server;

import client.Request;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.Socket;

class ServerThreadTest {

   private Server server;
   private static String databasePath = "./src/test/resources/db.json";
   private DatabaseFile database = new DatabaseFile(databasePath);
   private FileAccess fileAccess = database.getFileAccess();
   private ServerThread serverThread;
   private Gson gson = new GsonBuilder().setPrettyPrinting().create();

   public ServerThreadTest() {
      server = new Server();
   }

   @Test
   void takeActionJsonAdd() throws IOException {
      fileAccess.writeToFile(databasePath, "{\n}");
      serverThread = new ServerThread(new Socket(), server, database);
      Request request = new Request("set", new JsonPrimitive("test"), new JsonPrimitive("test1"));

      String response = serverThread.takeActionJson(request);

      String temp = fileAccess.readFromFile(databasePath);
      JsonObject object = gson.fromJson(temp, JsonObject.class);

      Assertions.assertTrue(object.has("test"));
      Assertions.assertEquals(object.get("test").getAsString(), "test1");
   }

   @Test
   void takeActionJsonGet() throws IOException {
      fileAccess.writeToFile(databasePath, "{\"test\": \"test1\"}");
      serverThread = new ServerThread(new Socket(), server, database);
      Request request = new Request("get", new JsonPrimitive("test"), new JsonPrimitive("test1"));

      String response = serverThread.takeActionJson(request);

      Response responseTemp = gson.fromJson(response, Response.class);
      Assertions.assertEquals(responseTemp.value.getAsString(), "test1");
   }

   @Test
   void takeActionJsonRemove() throws IOException {
      fileAccess.writeToFile(databasePath, "{\"test\": \"test1\"}");
      serverThread = new ServerThread(new Socket(), server, database);
      Request request = new Request("delete", new JsonPrimitive("test"), null);

      String response = serverThread.takeActionJson(request);

      String temp = fileAccess.readFromFile(databasePath);
      JsonObject object = gson.fromJson(temp, JsonObject.class);

      Assertions.assertFalse(object.has("test"));
   }
}