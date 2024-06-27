package server.command;

import client.request.Request;
import server.exception.NoSuchKeyException;
import server.exception.NoSuchNestedKeyException;
import server.exception.WrongNestedKeyTypeException;
import server.exception.WrongValueTypeException;
import server.response.Response;

import java.io.IOException;

@FunctionalInterface
public interface DatabaseCommand {

   Response execute(Request request) throws IOException, NoSuchKeyException, NoSuchNestedKeyException, WrongNestedKeyTypeException, WrongValueTypeException;
}
