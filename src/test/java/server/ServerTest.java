package server;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

public class ServerTest {

   @Test
   public void test_server_closes() {
      Server server = new Server();
      Thread serverThread = new Thread(() -> server.serve());
      serverThread.start();

      try {
         Thread.sleep(500); // Give the server some time to start
      } catch (InterruptedException e) {
         fail("Server thread interrupted");
      }

      server.stopServer();

      assertThat(server.isRunning()).isFalse();
   }
}
