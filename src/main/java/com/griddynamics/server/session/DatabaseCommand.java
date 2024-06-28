package com.griddynamics.server.session;

import com.griddynamics.client.request.Request;
import com.griddynamics.server.storage.NoSuchNestedKeyException;
import com.griddynamics.server.storage.NoSuchKeyException;
import com.griddynamics.server.storage.WrongNestedKeyTypeException;
import com.griddynamics.server.storage.WrongValueTypeException;

import java.io.IOException;

@FunctionalInterface
public interface DatabaseCommand {

   Response execute(Request request) throws IOException, NoSuchKeyException, NoSuchNestedKeyException, WrongNestedKeyTypeException, WrongValueTypeException;
}
