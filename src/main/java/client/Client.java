package client;

import client.args.Args;
import client.request.Request;
import client.request.RequestDeserializer;
import com.beust.jcommander.JCommander;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import server.database.FileAccess;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class Client {

    private DataInputStream input;
    private DataOutputStream output;
    private Socket socket;

    private FileAccess fileAccess = new FileAccess();
    private final String path;
    private String toSend;
    private final Gson gson = new GsonBuilder().registerTypeAdapter(Request.class, new RequestDeserializer()).create();

    public Client(String path) {
       this.path = path;
    }

    public Client() {
       this("./src/main/java/client/data/");
    }

    public DataInputStream getInput() {
       return input;
    }

    public void start() throws IOException {
       String address = "127.0.0.1";
       int port = 23456;

       this.socket = new Socket(InetAddress.getByName(address), port);
       input = new DataInputStream(socket.getInputStream());
       output = new DataOutputStream(socket.getOutputStream());
       System.out.println("Client started!");
    }

    public void cleanUp() throws IOException {
       this.input.close();
       this.output.close();
       this.socket.close();
    }

    public void makeRequest(String[] args) throws IOException {
       Args clientArgs = new Args();
       JCommander parser = JCommander.newBuilder()
             .addObject(clientArgs)
             .build();

       parser.parse(args);

       Request request;

       if (clientArgs.filePath != null) {
          try {
             toSend = fileAccess.readFromFile(path + clientArgs.filePath);
             // check if JSON is valid
             gson.fromJson(toSend, Request.class);

          } catch (IOException e) {
             System.out.println("Wrong file path");
             throw new IOException(e);
          }

       } else {

          request = new Request(clientArgs.action, clientArgs.key, clientArgs.data);
          toSend = new Gson().toJson(request);
       }

       output.writeUTF(toSend);
    }

    public void provideOuput() throws IOException {
       System.out.println("Sent: " + toSend);

       String received = input.readUTF();
       System.out.println("Received: " + received);
    }

    public void connect(String[] args) {
        try {
           start();
           makeRequest(args);
           provideOuput();
           cleanUp();

        } catch (IOException e) {
           System.out.println("Socket error");
        } catch (JsonSyntaxException e) {
           System.out.println("The JSON file is not correct");
        }
    }
}
