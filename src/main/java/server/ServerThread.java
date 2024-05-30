package server;

import client.Request;
import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;

public class ServerThread implements Runnable {

    private final Socket socket;
    private DatabaseManagement service;
    private Server server;

    public ServerThread(Socket socket, DatabaseManagement service, Server server) {
        this.socket = socket;
        this.service = service;
        this.server = server;
    }

    @Override
    public void run() {

        try (socket;
             DataInputStream input = new DataInputStream(socket.getInputStream());
             DataOutputStream output = new DataOutputStream(socket.getOutputStream())
        ) {
            String toSend;
            String received = input.readUTF().trim();
            System.out.println("Received: " + received);

            Request request = new Gson().fromJson(received, Request.class);

            toSend = service.takeActionJson(request);
            output.writeUTF(toSend);
            System.out.println("Sent: " + toSend);

            if (request.type.equals("exit")) {
                socket.close();
                server.stopServer();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
