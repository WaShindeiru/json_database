package server;

import com.google.gson.*;

import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DatabaseFile {

//    private final static String databasePath = "./db.json";
    private final String databasePath;
    private final FileAccess fileAccess;
    private final Gson gson;
    private final JsonObject inMemory;

    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private Lock readLock = lock.readLock();
    private Lock writeLock = lock.writeLock();

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
                e.printStackTrace();
            }
        }
        inMemory = inMemoryTemp;
    }

    public DatabaseFile() {
        this("./src/main/java/server/data/db.json");
    }

    private JsonObject getDatabase() throws IOException {

        String temp = fileAccess.readFromFile(databasePath);
        return gson.fromJson(temp, JsonObject.class);
    }

    public void set(JsonArray keyArray, JsonElement value) throws WrongArgumentException, IOException {

        writeLock.lock();
        try {
            JsonObject temp = inMemory;

            for (int i=0; i<keyArray.size() - 1; i++) {
                String keyTemp = keyArray.get(i).getAsString();
                if (temp.has(keyTemp)) {
                    if (temp.get(keyTemp).isJsonObject())
                        temp = temp.get(keyTemp).getAsJsonObject();
                    else
                        throw new WrongArgumentException("No such key");
                } else {
                    throw new WrongArgumentException("No such key");
                }
            }

            temp.add(keyArray.get(keyArray.size() - 1).getAsString(), value);

            String result = gson.toJson(inMemory);
            fileAccess.writeToFile(databasePath, result);

        } finally {
            writeLock.unlock();
        }
    }

    public JsonElement get(JsonArray keyArray) throws WrongArgumentException, IOException {

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
                        throw new WrongArgumentException("No such key");
                } else
                    throw new WrongArgumentException("No such key");
            }

            if (temp.has(keyArray.get(keyArray.size() - 1).getAsString())) {
                tempElement = temp.get(keyArray.get(keyArray.size() - 1).getAsString());
            } else
                throw new WrongArgumentException("No such key");

        } finally {
            readLock.unlock();
        }

        if (tempElement.isJsonPrimitive()) {
            return tempElement.getAsJsonPrimitive();

        } else if (tempElement.isJsonObject()) {
            return tempElement.getAsJsonObject();
        } else if (tempElement.isJsonArray()) {
            return tempElement.getAsJsonArray();
        } else throw new WrongArgumentException("No such type!");
    }

    public void delete(JsonArray keyArray) throws WrongArgumentException, IOException {

        writeLock.lock();
        try {
            JsonObject temp = inMemory;

            for (int i=0; i<keyArray.size() - 1; i++) {
                String key = keyArray.get(i).getAsString();

                if (temp.has(key))
                    if (temp.get(key).isJsonObject())
                        temp = temp.get(key).getAsJsonObject();
                    else
                        throw new WrongArgumentException("No such key");
                else
                    throw new WrongArgumentException("No such key");
            }

            temp.remove(keyArray.get(keyArray.size() - 1).getAsString());

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
