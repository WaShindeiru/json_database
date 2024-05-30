package client;

import com.beust.jcommander.JCommander;
import com.google.gson.Gson;
import server.FileAccess;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class Client {

    private DataInputStream input;
    private DataOutputStream output;

    private FileAccess fileAccess = new FileAccess();
//    private final static String path = "./JSON Database with Java/task/src/client/data/";
    private final static String path = "./src/main/java/client/data/";

    public void connect(String[] args) {

        String address = "127.0.0.1";
        int port = 23456;

        Args clientArgs = new Args();
        JCommander parser = JCommander.newBuilder()
                .addObject(clientArgs)
                .build();
        parser.parse(args);

        try (Socket socket = new Socket(InetAddress.getByName(address), port)) {
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());

            System.out.println("Client started!");

            Request request;
            String toSend;

            if (clientArgs.filePath != null) {
               try {
                  toSend = fileAccess.readFromFile(path + clientArgs.filePath);
               } catch (IOException e) {
                  System.out.println("Wrong file path");
                  throw new IOException(e);
               }

            } else {

               request = new Request(clientArgs.action, clientArgs.key, clientArgs.data);
               toSend = new Gson().toJson(request);
            }

            output.writeUTF(toSend);
            System.out.println("Sent: " + toSend);

            String received = input.readUTF();
            System.out.println("Received: " + received);

        } catch (IOException e) {
            System.out.println("Socket error");
        }
    }
}
