package com.database.server.session;

import com.database.client.request.Request;
import com.database.server.storage.NoSuchNestedKeyException;
import com.database.server.storage.NoSuchKeyException;
import com.database.server.storage.WrongNestedKeyTypeException;
import com.database.server.storage.WrongValueTypeException;

import java.io.IOException;

@FunctionalInterface
public interface DatabaseCommand {

   Response execute(Request request) throws IOException, NoSuchKeyException, NoSuchNestedKeyException, WrongNestedKeyTypeException, WrongValueTypeException;
}
