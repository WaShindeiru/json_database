package server.command;

import client.Request;
import server.Response;
import server.WrongArgumentException;

import java.io.IOException;

@FunctionalInterface
public interface DatabaseCommand {

   Response execute(Request request) throws WrongArgumentException, IOException;
}
