package server;

import client.Request;
import client.RequestDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.*;
import java.net.Socket;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class ServerThreadTest {
   private static String databasePath = "./src/test/resources/db.json";
   private static Gson gson = new GsonBuilder().registerTypeAdapter(Request.class, new RequestDeserializer()).create();

   @Mock
   private Socket socket;
   @Mock
   private Server server;
   private DatabaseFile database;
   private ServerThread session;
   private FileAccess fileAccess = new FileAccess();

   @BeforeEach
   public void beforeEach() {
      String inputJson = "{\"Bulbasaur\": \"grass\"}";
      try {
         fileAccess.writeToFile(databasePath, inputJson);
      } catch (IOException e) {
         fail("Exception thrown when not expected");
      }
      database = new DatabaseFile(databasePath);

      session = new ServerThread(socket, server, database);
   }

   @Test
   public void test_happy_path_run() throws IOException {
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
      dataOutputStream.writeUTF("{\"type\":\"get\",\"key\":\"Bulbasaur\"}");
      dataOutputStream.flush();
      dataOutputStream.close();
      ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
      DataInputStream input = new DataInputStream(byteArrayInputStream);

      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      DataOutputStream output = new DataOutputStream(baos);
      Mockito.when(socket.getInputStream()).thenReturn(input);
      Mockito.when(socket.getOutputStream()).thenReturn(output);

      session.run();

      byte[] data = baos.toByteArray();
      DataInputStream testInput = new DataInputStream(new ByteArrayInputStream(data));
      String resultString = testInput.readUTF();

      Response response = gson.fromJson(resultString, Response.class);
      assertNotNull(response);
      assertThat(response.response).isEqualTo("OK");
      assertThat(response.value).isEqualTo(new JsonPrimitive("grass"));
   }
}
