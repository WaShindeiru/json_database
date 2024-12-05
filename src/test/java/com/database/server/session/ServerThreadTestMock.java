package com.database.server.session;

import com.database.client.request.Request;
import com.database.client.request.RequestDeserializer;
import com.google.gson.*;
import com.database.server.storage.NoSuchKeyException;
import com.database.server.storage.NoSuchNestedKeyException;
import com.database.server.storage.WrongNestedKeyTypeException;
import com.database.server.storage.WrongValueTypeException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import com.database.server.Server;
import com.database.server.storage.DatabaseFile;

import java.io.*;
import java.net.Socket;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ServerThreadTestMock {

   @Mock
   private Socket socket;
   @Mock
   private Server server;
   @Mock
   private DatabaseFile database;
   @InjectMocks
   private ServerThread session;

   @BeforeEach
   public void beforeEach() {
      session = new ServerThread(socket, server, database);
   }

   private static String databasePath = "./src/test/resources/db.json";
   private Gson gson = new GsonBuilder().registerTypeAdapter(Request.class, new RequestDeserializer()).create();

   @Test
   public void test_successful_get() throws WrongValueTypeException, NoSuchKeyException, WrongNestedKeyTypeException, NoSuchNestedKeyException {
      JsonElement key = gson.toJsonTree("keykey");
      JsonArray keyArray = new JsonArray();
      keyArray.add(key);
      JsonElement value = gson.toJsonTree("valuevalue");
      Request request = new Request("get", key, null);
      when(database.get(keyArray)).thenReturn(value);

      Response response = null;
      try {
         response = session.getCommand(request);
      } catch (Exception e) {
         fail("Exception not expected");
      }

      assertNotNull(response);
      assertThat(response.response.equals("OK")).isTrue();
      assertThat(response.value).isEqualTo(value);
      assertNull(response.reason);
   }

   @Test
   public void test_successful_set() throws IOException {
      JsonElement key = new Gson().toJsonTree("testKey");
      JsonArray keyArray = new JsonArray();
      keyArray.add(key);
      JsonElement value = new Gson().toJsonTree("testValue");
      Request request = new Request("set", key, value);

      Response response = null;
      try {
         response = session.setCommand(request);
      } catch (Exception e) {
         fail("Exception not expected here");
      }

      verify(database).set(eq(keyArray), eq(value));
      assertNotNull(response);
      assertEquals("OK", response.response);
      assertNull(response.value);
      assertNull(response.reason);
   }

   @Test
   public void test_successful_delete() throws IOException, NoSuchKeyException, WrongNestedKeyTypeException, NoSuchNestedKeyException {
      JsonElement key = new JsonPrimitive("existingKey");
      JsonArray keyArray = new JsonArray();
      keyArray.add(key);
      Request request = new Request("delete", key, null);

      Response response = null;
      try {
         response = session.deleteCommand(request);
      } catch (Exception e) {
         fail("Exception not expected here");
      }

      Mockito.verify(database).delete(eq(keyArray));
      assertNotNull(response);
      assertEquals("OK", response.response);
      assertNull(response.value);
      assertNull(response.reason);
   }

   @Test
   public void test_transform_json_element_into_json_array() {
      JsonElement jsonElement = gson.fromJson("\"simple\"", JsonElement.class);

      JsonArray result = session.transformJsonElementIntoJsonArray(jsonElement);

      assertEquals(1, result.size());
      assertEquals("simple", result.get(0).getAsString());
   }

   @Test
   public void test_transform_json_array_into_json_array() {
      String element = "simple";
      JsonArray keyArray = new JsonArray();
      keyArray.add(element);

      JsonArray result = session.transformJsonElementIntoJsonArray(keyArray);

      assertEquals(1, result.size());
      assertEquals("simple", result.get(0).getAsString());
   }

   @Test
   public void test_get_command_set() throws IOException, WrongValueTypeException, NoSuchKeyException, WrongNestedKeyTypeException, NoSuchNestedKeyException {
      ServerThread sessionTemp = Mockito.spy(session);
      JsonArray keyArray = new JsonArray();
      keyArray.add("test1");
      JsonElement value = new JsonPrimitive("value1");
      Request request = new Request("set", keyArray, value);

      DatabaseCommand command = sessionTemp.getCommannd(request);
      try {
         command.execute(request);
      } catch (Exception e) {
         fail("Exception not expected");
      }

      assertNotNull(command);
      Mockito.verify(sessionTemp).setCommand(eq(request));
   }

   @Test
   public void test_get_command_get() throws IOException, WrongValueTypeException, NoSuchKeyException, WrongNestedKeyTypeException, NoSuchNestedKeyException {
      ServerThread sessionTemp = Mockito.spy(session);
      JsonArray keyArray = new JsonArray();
      keyArray.add("test1");
      Request request = new Request("get", keyArray, null);

      DatabaseCommand command = sessionTemp.getCommannd(request);

      try {
         command.execute(request);
      } catch (Exception e) {
         fail("Exception not expected");
      }

      assertNotNull(command);
      Mockito.verify(sessionTemp).getCommand(eq(request));
   }

   @Test
   public void test_get_command_delete() throws IOException, WrongValueTypeException, NoSuchKeyException, WrongNestedKeyTypeException, NoSuchNestedKeyException {
      ServerThread sessionTemp = Mockito.spy(session);
      JsonArray keyArray = new JsonArray();
      keyArray.add("test1");
      Request request = new Request("delete", keyArray, null);

      DatabaseCommand command = sessionTemp.getCommannd(request);
      try {
         command.execute(request);
      } catch (Exception e) {
         fail("Exception not expected");
      }

      assertNotNull(command);
      Mockito.verify(sessionTemp).deleteCommand(eq(request));
   }

   @Test
   public void test_get_command_exit() throws IOException, WrongValueTypeException, NoSuchKeyException, WrongNestedKeyTypeException, NoSuchNestedKeyException {
      ServerThread sessionTemp = Mockito.spy(session);
      Request request = new Request("exit", null, null);

      DatabaseCommand command = sessionTemp.getCommannd(request);
      try {
         command.execute(request);
      } catch (Exception e) {
         fail("Exception not expected");
      }

      assertNotNull(command);
      Mockito.verify(sessionTemp).exitCommand(eq(request));
   }

   @Test
   public void test_default_command() {
      Request request = new Request("unknown", null, null);

      Response response = session.defaultCommand(request);

      assertEquals("ERROR", response.response);
      assertNull(response.value);
      assertNull(response.reason);
   }
}