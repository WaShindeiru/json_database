package server;

import client.Request;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.io.*;
import java.net.Socket;

public class ServerThread implements Runnable {

    private final Socket socket;
    private Server server;
    private DatabaseFile database;
    private Gson gson;

    public ServerThread(Socket socket, Server server, DatabaseFile database) {
        this.socket = socket;
        this.server = server;
        this.database = database;
        this.gson = new Gson();
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

            toSend = this.takeActionJson(request);
            output.writeUTF(toSend);
            System.out.println("Sent: " + toSend);

            if (request.type.equals("exit")) {
                socket.close();
                server.stopServer();
            }

        } catch (IOException swallow) {
        }
    }


    public String takeActionJson(Request request) {

        String command = request.type;
        JsonElement key = request.key;
        JsonElement value = request.value;
        String result = "";
        Response response;

        JsonArray keyArray = new JsonArray();

        // key is a single value, transform it into array
        if (key != null && key.isJsonPrimitive()) {
            String keyString = key.getAsString();
            keyArray = new JsonArray();
            keyArray.add(gson.fromJson(keyString, JsonElement.class));
        } else if (key != null && key.isJsonArray()) {
            keyArray = key.getAsJsonArray();
        }

        try {
            switch (command) {
                case "set":
                    database.set(keyArray, value);
                    response = new Response("OK", null, null);
                    result = gson.toJson(response);
                    break;

                case "delete":
                    database.delete(keyArray);
                    response = new Response("OK", null, null);
                    result = gson.toJson(response);
                    break;

                case "get":
                    JsonElement temp = database.get(keyArray);
                    response = new Response("OK", temp, null);
                    result = gson.toJson(response);
                    break;

                case "exit":
                    response = new Response("OK", null, null);
                    result = gson.toJson(response);
                    break;

                default:
                    response = new Response("ERROR", null, null);
                    result = gson.toJson(response);
            }

        } catch (WrongArgumentException e) {
            response = new Response("ERROR", null, e.getMessage());
            result = gson.toJson(response);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}
