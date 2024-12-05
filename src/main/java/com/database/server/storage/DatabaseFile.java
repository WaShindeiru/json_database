package com.griddynamics.server.storage;

import com.google.gson.*;
import com.griddynamics.server.storage.util.FileAccess;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DatabaseFile {

    private final String databasePath;
    private final FileAccess fileAccess;
    private final Gson gson;
    private final JsonObject inMemory;

    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private Lock readLock = lock.readLock();
    private Lock writeLock = lock.writeLock();
    private static final Logger logger = LoggerFactory.getLogger(DatabaseFile.class);

    public DatabaseFile(String filePath) {
        this.databasePath = filePath;
        JsonObject inMemoryTemp;
        fileAccess = new FileAccess();
        gson = new GsonBuilder().setPrettyPrinting().create();

        try {
            inMemoryTemp = getDatabase();
        } catch (IOException e) {
            inMemoryTemp = new JsonObject();
            try {
                fileAccess.writeToFile(databasePath, gson.toJson(inMemoryTemp));
            } catch (IOException ex) {
                logger.info(ex.getMessage());
            }
        }
        inMemory = inMemoryTemp;
    }

    public DatabaseFile() {
        this("./src/main/resources/db.json");
    }

    private JsonObject getDatabase() throws IOException {

        String temp = fileAccess.readFromFile(databasePath);
        return gson.fromJson(temp, JsonObject.class);
    }

    public void set(JsonArray keyArray, JsonElement value) throws IOException {

        writeLock.lock();
        try {
            JsonObject temp = inMemory;

            for (int i=0; i<keyArray.size() - 1; i++) {
                String keyTemp = keyArray.get(i).getAsString();
                if (temp.has(keyTemp)) {
                    if (temp.get(keyTemp).isJsonObject())
                        temp = temp.get(keyTemp).getAsJsonObject();
                    else {
                        temp = setInnerJsonObject(keyArray, temp, i, keyTemp);
                        break;
                    }
                } else {
                    temp = setInnerJsonObject(keyArray, temp, i, keyTemp);
                    break;
                }
            }

            temp.add(keyArray.get(keyArray.size() - 1).getAsString(), value);

            String result = gson.toJson(inMemory);
            fileAccess.writeToFile(databasePath, result);

        } finally {
            writeLock.unlock();
        }
    }

    private JsonObject setInnerJsonObject(JsonArray keyArray, JsonObject temp, int i, String keyTemp) {
        JsonObject tempInner = new JsonObject();
        JsonObject head = tempInner.getAsJsonObject();

        for (int j=i + 1; j<keyArray.size() - 1; j++) {
            String keyTempInner = keyArray.get(j).getAsString();
            tempInner.add(keyTempInner, new JsonObject());
            tempInner = tempInner.getAsJsonObject(keyTempInner);
        }

        temp.add(keyTemp, head);
        temp = tempInner;
        return temp;
    }

    public JsonElement get(JsonArray keyArray) throws WrongNestedKeyTypeException, NoSuchNestedKeyException, NoSuchKeyException, WrongValueTypeException {

        JsonElement tempElement;

        readLock.lock();
        try {
            JsonObject temp = inMemory;

            for (int i=0; i<keyArray.size() - 1; i++) {
                String key = keyArray.get(i).getAsString();

                if (temp.has(key)) {
                    if (temp.get(key).isJsonObject())
                        temp = temp.get(key).getAsJsonObject();
                    else
                        throw new WrongNestedKeyTypeException("Wrong nested key type: " + gson.toJson(keyArray));
                } else
                    throw new NoSuchNestedKeyException("No such nested key: " + gson.toJson(keyArray));
            }

            if (temp.has(keyArray.get(keyArray.size() - 1).getAsString())) {
                tempElement = temp.get(keyArray.get(keyArray.size() - 1).getAsString());
            } else
                throw new NoSuchKeyException("No such key: " + gson.toJson(keyArray));

        } finally {
            readLock.unlock();
        }

        if (tempElement.isJsonPrimitive()) {
            return tempElement.getAsJsonPrimitive();

        } else if (tempElement.isJsonObject()) {
            return tempElement.getAsJsonObject();
        } else if (tempElement.isJsonArray()) {
            return tempElement.getAsJsonArray();
        } else throw new WrongValueTypeException("Wrong value for given key: " + gson.toJson(keyArray));
    }

    public void delete(JsonArray keyArray) throws WrongNestedKeyTypeException, NoSuchNestedKeyException, NoSuchKeyException, IOException {

        writeLock.lock();
        try {
            JsonObject temp = inMemory;

            for (int i=0; i<keyArray.size() - 1; i++) {
                String key = keyArray.get(i).getAsString();

                if (temp.has(key))
                    if (temp.get(key).isJsonObject())
                        temp = temp.get(key).getAsJsonObject();
                    else
                        throw new WrongNestedKeyTypeException("Wrong nested key type: " + gson.toJson(keyArray));
                else
                    throw new NoSuchNestedKeyException("No such nested key: " + gson.toJson(keyArray));
            }

            if (temp.has(keyArray.get(keyArray.size() - 1).getAsString())) {
                temp.remove(keyArray.get(keyArray.size() - 1).getAsString());
            } else
                throw new NoSuchKeyException("No such key: " + gson.toJson(keyArray));

            String result = gson.toJson(inMemory);
            fileAccess.writeToFile(databasePath, result);

        } finally {
            writeLock.unlock();
        }
    }

    public FileAccess getFileAccess() {
        return fileAccess;
    }
}
