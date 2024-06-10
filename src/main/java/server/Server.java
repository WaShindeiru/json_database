package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private ExecutorService executorService = Executors.newFixedThreadPool(4);
    private ServerSocket server;
    private DatabaseFile database;

    private final static String address = "127.0.0.1";
    private final static int port = 23456;

    public Server(String path) {
        database = new DatabaseFile(path);
        try {
            server = new ServerSocket(port, 50, InetAddress.getByName(address));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Server() {
        this("./src/main/java/server/data/db.json");
    }

    public void serve() {

        System.out.println("Server started!");

            while (!server.isClosed()) {
                try {
                    Socket socket = server.accept();
                    ServerThread thread = new ServerThread(socket, this, database);
                    executorService.execute(thread);

                } catch (IOException swallow) {}
            }

            executorService.shutdown();
    }

    public synchronized void stopServer() {
        if (!server.isClosed()) {
            try {
                server.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized boolean isRunning() {
        return !this.server.isClosed();
    }
}
