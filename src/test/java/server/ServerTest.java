package server;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

public class ServerTest {

   @Test
   public void test_server_closes() {
      Server server = new Server(23455, "127.0.0.2");
      Thread serverThread = new Thread(server::serve);
      serverThread.start();

      try {
         Thread.sleep(500);
      } catch (InterruptedException e) {
         fail("Server thread interrupted");
      }

      server.stopServer();

      assertThat(server.isRunning()).isFalse();
   }
}
