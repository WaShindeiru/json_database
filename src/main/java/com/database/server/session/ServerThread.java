package com.griddynamics.server.session;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.griddynamics.client.request.Request;
import com.griddynamics.server.Server;
import com.griddynamics.server.storage.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ServerThread implements Runnable {

    private final Socket socket;
    private Server server;
    private DatabaseFile database;
    private Gson gson;
    private boolean closeServer;
    private static final Logger logger = LoggerFactory.getLogger(ServerThread.class);

    public ServerThread(Socket socket, Server server, DatabaseFile database) {
        this.socket = socket;
        this.server = server;
        this.database = database;
        this.gson = new Gson();
        this.closeServer = false;
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
            Response response;

            DatabaseCommand command = getCommannd(request);

            try {
                response = command.execute(request);
            } catch (NoSuchKeyException | NoSuchNestedKeyException | WrongNestedKeyTypeException |
                     WrongValueTypeException e1) {
                response = new Response("ERROR", null, e1.getMessage());
            } catch (IOException e2) {
                logger.info("Can't access the database file!");
                response = new Response("ERROR", null, "Can't access the database file!");
            }

            toSend = gson.toJson(response);
            output.writeUTF(toSend);
            System.out.println("Sent: " + toSend);

            if (closeServer) {
                socket.close();
                server.stopServer();
            }

        } catch (IOException swallow) {
            swallow.printStackTrace();
        }
    }

    public DatabaseCommand getCommannd(Request request) {
        String type = request.type;
        DatabaseCommand command;

        switch (type) {
            case "set" -> command = this::setCommand;
            case "get" -> command = this::getCommand;
            case "delete" -> command = this::deleteCommand;
            case "exit" -> command = this::exitCommand;
            default -> command = this::defaultCommand;
        }

        return command;
    }

    public Response getCommand(Request request) throws IOException, WrongValueTypeException, NoSuchKeyException, WrongNestedKeyTypeException, NoSuchNestedKeyException {
        JsonArray keyArray = transformJsonElementIntoJsonArray(request.key);
        Response response;

        JsonElement temp = database.get(keyArray);
        response = new Response("OK", temp, null);
        return response;
    }

    public Response setCommand(Request request) throws IOException {
        JsonArray keyArray = transformJsonElementIntoJsonArray(request.key);
        JsonElement value = request.value;
        Response response;

        database.set(keyArray, value);
        response = new Response("OK", null, null);
        return response;
    }

    public Response exitCommand(Request request) {
        closeServer = true;
        return new Response("OK", null, null);
    }

    public Response deleteCommand(Request request) throws IOException, NoSuchKeyException, WrongNestedKeyTypeException, NoSuchNestedKeyException {
        JsonArray keyArray = transformJsonElementIntoJsonArray(request.key);
        Response response;

        database.delete(keyArray);
        response = new Response("OK", null, null);
        return response;
    }

    public Response defaultCommand(Request request) {
       return new Response("ERROR", null, null);
    }

    public JsonArray transformJsonElementIntoJsonArray(JsonElement jsonElement) {
        JsonArray resultArray = new JsonArray();

        if (jsonElement != null && jsonElement.isJsonPrimitive()) {
            String jsonElementString = jsonElement.getAsString();
            resultArray = new JsonArray();
            resultArray.add(gson.fromJson(jsonElementString, JsonElement.class));
        } else if (jsonElement != null && jsonElement.isJsonArray()) {
            resultArray = jsonElement.getAsJsonArray();
        }

        return resultArray;
    }
}
