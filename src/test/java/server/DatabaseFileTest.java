package server;

import client.Request;
import client.RequestDeserializer;
import com.google.gson.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.*;


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
      String inputJson = "{}";
      try {
         fileAccess.writeToFile(path, inputJson);
      } catch (IOException e) {
         fail("Exception thrown when not expected");
      }
      database = new DatabaseFile(path);

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
   public void test_get_value() {
      String inputJson = "{\"test1\": \"correct1\"}";
      String key = "test1";
      String value = "correct1";
      JsonArray keyArray = new JsonArray();
      keyArray.add(key);

      try {
         fileAccess.writeToFile(path, inputJson);
      } catch (IOException e) {
         fail("Exception thrown when not expected");
      }

      database = new DatabaseFile(path);

      JsonElement result = null;
      try {
         result = database.get(keyArray);
      } catch (WrongArgumentException | IOException e) {
         fail("Exception thrown when not expected");
      }

      assertThat(result != null).isTrue();
      if (result.isJsonPrimitive()) {
         assertThat(result.getAsJsonPrimitive().getAsString()).isEqualTo(value);
      } else {
         fail("result is not a json primitive");
      }
   }

   @Test
   public void test_get_value_key_doesnt_exist() {
      String inputJson = "{\"test1\": \"correct1\"}";
      String key = "test1";
      JsonArray keyArray = new JsonArray();
      keyArray.add(key);

      String secondKey = "test2";
      JsonArray secondKeyArray = new JsonArray();
      secondKeyArray.add(secondKey);

      try {
         fileAccess.writeToFile(path, inputJson);
      } catch (IOException e) {
         fail("Exception thrown when not expected");
      }

      database = new DatabaseFile(path);

      assertThatThrownBy(() -> database.get(secondKeyArray)).isInstanceOf(WrongArgumentException.class);
   }

   @Test
   public void test_delete_value() {
      String inputJson = "{\"test4\": \"correct4\"}";
      String key = "test4";
      String value = "correct4";
      JsonArray keyArray = new JsonArray();
      keyArray.add(key);

      try {
         fileAccess.writeToFile(path, inputJson);
      } catch (IOException e) {
         fail("Exception thrown when not expected");
      }

      database = new DatabaseFile(path);

      JsonElement result = null;
      try {
         result = database.get(keyArray);
      } catch (WrongArgumentException | IOException e) {
         fail("Exception thrown when not expected");
      }
      assertThat(result != null).isTrue();
      if (result.isJsonPrimitive()) {
         assertThat(result.getAsJsonPrimitive().getAsString()).isEqualTo(value);
      } else {
         fail("result is not a json primitive");
      }

      try {
         database.delete(keyArray);
      } catch (WrongArgumentException | IOException e) {
         fail("Exception thrown when not expected");
      }

      assertThatThrownBy(() -> database.get(keyArray)).isInstanceOf(WrongArgumentException.class);
   }



   @Test
   public void test_retrieve_json_primitive_value() {
      String inputJson = "{}";
      try {
         fileAccess.writeToFile(path, inputJson);
      } catch (IOException e) {
         fail("Exception thrown when not expected");
      }

      JsonArray keyArray = new JsonArray();
      keyArray.add("key1");
      keyArray.add("key2");

      JsonObject innerObject = new JsonObject();
      innerObject.addProperty("key2", "value");

      DatabaseFile databaseFile = new DatabaseFile(path);
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

   @Test
   public void set_deeply_nested_json() {
      String inputJson = "{}";
      try {
         fileAccess.writeToFile(path, inputJson);
      } catch (IOException e) {
         fail("Exception thrown when not expected");
      }
      DatabaseFile database = new DatabaseFile(path);

      JsonArray keyArray = new JsonArray();
      keyArray.add("Pokemon");
      keyArray.add("Kanto");
      keyArray.add("Bulbasaur");
      keyArray.add("type");
      String value = "grass";

      try {
         database.set(keyArray, new JsonPrimitive(value));
      } catch (WrongArgumentException | IOException e) {
         fail("Exception thrown when not expected");
      }

      JsonElement result = null;
      try {
         result = database.get(keyArray);
      } catch (WrongArgumentException | IOException e) {
         fail("Exception thrown when not expected");
      }

      assertThat(result != null).isTrue();
      assertThat(result.isJsonPrimitive()).isTrue();
      assertThat(result.getAsJsonPrimitive().isString()).isTrue();
      assertThat(result.getAsJsonPrimitive().getAsString()).isEqualTo(value);
   }

   @Test
   public void test_set_and_get_json_object() {
      String inputJson = "{}";
      try {
         fileAccess.writeToFile(path, inputJson);
      } catch (IOException e) {
         fail("Exception thrown when not expected");
      }
      database = new DatabaseFile(path);

      String key = "Bulbasaur";
      JsonArray keyArray = new JsonArray();
      keyArray.add(key);
      JsonObject value = new JsonObject();
      value.add("type", new JsonPrimitive("grass"));
      value.add("size", new JsonPrimitive("small"));

      JsonElement result = null;
      try {
         database.set(keyArray, value);
         result = database.get(keyArray);
      } catch (WrongArgumentException | IOException e) {
         fail("Exception thrown when not expected");
      }

      assertThat(result != null).isTrue();
      assertThat(result.isJsonObject()).isTrue();
      JsonObject innerObject = result.getAsJsonObject();

      assertThat(innerObject.has("type")).isTrue();
      assertThat(innerObject.get("type").isJsonPrimitive()).isTrue();
      assertThat(innerObject.get("type").getAsJsonPrimitive().isString()).isTrue();
      assertThat(innerObject.get("type").getAsJsonPrimitive().getAsString().equals("grass")).isTrue();

      assertThat(innerObject.has("size")).isTrue();
      assertThat(innerObject.get("size").isJsonPrimitive()).isTrue();
      assertThat(innerObject.get("size").getAsJsonPrimitive().isString()).isTrue();
      assertThat(innerObject.get("size").getAsJsonPrimitive().getAsString().equals("small")).isTrue();
   }

   @Test
   public void test_set_json_object_and_get_nested_key() {
      String inputJson = "{}";
      try {
         fileAccess.writeToFile(path, inputJson);
      } catch (IOException e) {
         fail("Exception thrown when not expected");
      }
      database = new DatabaseFile(path);

      String key = "Bulbasaur";
      JsonArray keyArray = new JsonArray();
      keyArray.add(key);
      JsonObject value = new JsonObject();
      value.add("type", new JsonPrimitive("grass"));
      value.add("size", new JsonPrimitive("small"));

      JsonArray getKeyArray = new JsonArray();
      getKeyArray.add(key);
      getKeyArray.add("type");

      JsonElement result = null;
      try {
         database.set(keyArray, value);
         result = database.get(getKeyArray);
      } catch (WrongArgumentException | IOException e) {
         fail("Exception thrown when not expected");
      }

      assertThat(result != null).isTrue();
      assertThat(result.isJsonPrimitive()).isTrue();
      assertThat(result.getAsJsonPrimitive().isString()).isTrue();
      assertThat(result.getAsJsonPrimitive().getAsString().equals("grass")).isTrue();
   }

   @Test
   public void test_set_and_get_json_array() {
      String inputJson = "{}";
      try {
         fileAccess.writeToFile(path, inputJson);
      } catch (IOException e) {
         fail("Exception thrown when not expected");
      }
      database = new DatabaseFile(path);

      String key = "Venusaur";
      JsonArray keyArray = new JsonArray();
      keyArray.add(key);
      JsonObject value = new JsonObject();
      JsonArray typeArray = new JsonArray();
      typeArray.add("grass");
      typeArray.add("poison");
      value.add("type", typeArray);
      value.add("size", new JsonPrimitive("small"));

      JsonArray getKeyArray = new JsonArray();
      getKeyArray.add(key);
      getKeyArray.add("type");

      JsonElement result = null;
      try {
         database.set(keyArray, value);
         result = database.get(getKeyArray);
      } catch (WrongArgumentException | IOException e) {
         fail("Exception thrown when not expected");
      }

      assertThat(result != null).isTrue();
      assertThat(result.isJsonArray()).isTrue();
      assertThat(result.getAsJsonArray().size() == 2).isTrue();

      assertThat(result.getAsJsonArray().get(0).isJsonPrimitive()).isTrue();
      assertThat(result.getAsJsonArray().get(0).getAsJsonPrimitive().isString()).isTrue();
      assertThat(result.getAsJsonArray().get(0).getAsJsonPrimitive().getAsString().equals("grass")).isTrue();

      assertThat(result.getAsJsonArray().get(1).isJsonPrimitive()).isTrue();
      assertThat(result.getAsJsonArray().get(1).getAsJsonPrimitive().isString()).isTrue();
      assertThat(result.getAsJsonArray().get(1).getAsJsonPrimitive().getAsString().equals("poison")).isTrue();
   }

   @Test
   public void test_set_and_delete_nested_json() {
      String inputJson = "{}";
      try {
         fileAccess.writeToFile(path, inputJson);
      } catch (IOException e) {
         fail("Exception thrown when not expected");
      }
      database = new DatabaseFile(path);

      String key = "Bulbasaur";
      JsonArray keyArray = new JsonArray();
      keyArray.add(key);
      JsonObject value = new JsonObject();
      value.add("type", new JsonPrimitive("grass"));
      value.add("size", new JsonPrimitive("small"));

      JsonArray getKeyArray = new JsonArray();
      getKeyArray.add(key);
      getKeyArray.add("type");

      JsonElement result = null;
      try {
         database.set(keyArray, value);
         result = database.get(keyArray);
      } catch (WrongArgumentException | IOException e) {
         fail("Exception thrown when not expected");
      }

      assertThat(result != null).isTrue();
      assertThat(result.isJsonObject()).isTrue();
      JsonObject innerObject = result.getAsJsonObject();

      assertThat(innerObject.has("type")).isTrue();
      assertThat(innerObject.get("type").isJsonPrimitive()).isTrue();
      assertThat(innerObject.get("type").getAsJsonPrimitive().isString()).isTrue();
      assertThat(innerObject.get("type").getAsJsonPrimitive().getAsString().equals("grass")).isTrue();

      assertThat(innerObject.has("size")).isTrue();
      assertThat(innerObject.get("size").isJsonPrimitive()).isTrue();
      assertThat(innerObject.get("size").getAsJsonPrimitive().isString()).isTrue();
      assertThat(innerObject.get("size").getAsJsonPrimitive().getAsString().equals("small")).isTrue();

      try {
         database.delete(getKeyArray);
      } catch (WrongArgumentException | IOException e) {
         fail("Exception thrown when not expected");
      }

      assertThatThrownBy(() -> database.get(getKeyArray)).isInstanceOf(WrongArgumentException.class);
   }

   @Test
   public void delete_non_existent_value() {
      String inputJson = "{}";
      try {
         fileAccess.writeToFile(path, inputJson);
      } catch (IOException e) {
         fail("Exception thrown when not expected");
      }
      database = new DatabaseFile(path);

      JsonArray keyArray = new JsonArray();
      keyArray.add("Bulbasaur");
      keyArray.add("type");

      assertThatThrownBy(() -> database.delete(keyArray)).isInstanceOf(WrongArgumentException.class);
   }

   @Test
   public void get_non_existent_value() {
      String inputJson = "{\"I love\": \"my life <3\"}";
      try {
         fileAccess.writeToFile(path, inputJson);
      } catch (IOException e) {
         fail("Exception thrown when not expected");
      }
      database = new DatabaseFile(path);

      JsonArray keyArray = new JsonArray();
      keyArray.add("Bulbasaur");
      keyArray.add("type");

      assertThatThrownBy(() -> database.get(keyArray)).isInstanceOf(WrongArgumentException.class);
   }

   @Test
   public void set_existent_key_new_value() {
      String inputJson = "{\"test\": {\"language\": {\"scala\": \"spark\"}}}";
      try {
         fileAccess.writeToFile(path, inputJson);
      } catch (IOException e) {
         fail("Exception thrown when not expected");
      }
      database = new DatabaseFile(path);

      JsonArray keyArray = new JsonArray();
      keyArray.add("test");
      keyArray.add("language");
      keyArray.add("java");

      JsonElement result = null;
      try {
         database.set(keyArray, new JsonPrimitive("spring"));
         result = database.get(keyArray);
      } catch (WrongArgumentException | IOException e) {
         fail("Exception thrown when not expected");
      }

      assertThat(result != null).isTrue();
      assertThat(result.isJsonPrimitive()).isTrue();
      assertThat(result.getAsJsonPrimitive().isString()).isTrue();
      assertThat(result.getAsJsonPrimitive().getAsString().equals("spring")).isTrue();
   }
}
