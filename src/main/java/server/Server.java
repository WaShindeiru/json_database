package server;

import server.internal.ServerThread;
import server.storage.DatabaseFile;

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

    private static String address;
    private static int port;

    public Server(String path, int port, String address) {
        this.port = port;
        this.address = address;
        database = new DatabaseFile(path);
        try {
            server = new ServerSocket(port, 50, InetAddress.getByName(address));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Server(int port, String address) {
        this("./src/main/resources/db.json", port, address);
    }

    public Server(String path) {
        this(path, 23456, "127.0.0.1");
    }

    public Server() {
        this("./src/main/resources/db.json");
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
