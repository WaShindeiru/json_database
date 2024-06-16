package server.command;

import client.request.Request;
import server.response.Response;
import server.exception.WrongArgumentException;

import java.io.IOException;

@FunctionalInterface
public interface DatabaseCommand {

   Response execute(Request request) throws WrongArgumentException, IOException;
}
