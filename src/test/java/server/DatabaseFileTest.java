package server;

import client.Request;
import client.RequestDeserializer;
import com.google.gson.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;


public class DatabaseFileTest {

   private static DatabaseFile database;
   private static FileAccess fileAccess;
   private static Gson gson = new GsonBuilder().registerTypeAdapter(Request.class, new RequestDeserializer()).create();
   private static final String path = "./src/test/resources/db.json";

   @BeforeAll
   public static void setUp() {
      database = new DatabaseFile(path);
      fileAccess = new FileAccess();
   }

   @Test
   public void test_set_value() {
      String key = "test";
      String value = "correct";
      JsonArray keyArray = new JsonArray();
      keyArray.add(key);

      try {
         database.set(keyArray, new JsonPrimitive(value));
      } catch (WrongArgumentException | IOException e) {
         fail("Exception thrown when not expected");
      }

      String result = "";
      try {
         result = fileAccess.readFromFile(path);
      } catch (IOException e) {
         fail("Exception thrown when not expected");
      }

      JsonObject jsonFromFile = gson.fromJson(result, JsonObject.class);
      assertThat(jsonFromFile.has(key)).isTrue();
      assertThat(jsonFromFile.get(key).getAsString()).isEqualTo(value);
   }

   @Test
   public void test_retrieve_json_primitive_value() {
      JsonArray keyArray = new JsonArray();
      keyArray.add("key1");
      keyArray.add("key2");

      JsonObject innerObject = new JsonObject();
      innerObject.addProperty("key2", "value");

      JsonObject rootObject = new JsonObject();
      rootObject.add("key1", innerObject);

      DatabaseFile databaseFile = new DatabaseFile();
      try {
         databaseFile.set(keyArray, new JsonPrimitive("value"));
      } catch (WrongArgumentException | IOException e) {
         fail("Exception thrown when not expected");
      }

      try {
         JsonElement result = databaseFile.get(keyArray);
         assertThat(result.isJsonPrimitive()).isTrue();
         assertThat(result.getAsString()).isEqualTo("value");
      } catch (WrongArgumentException | IOException e) {
         fail("Exception thrown when not expected");
      }
   }
}
