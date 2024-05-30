package server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.util.Map;

public class FileAccess {

    public String readFromFile(String filePath) throws IOException {
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            line = br.readLine();

            while (line != null) {
                resultStringBuilder.append(line).append("\n");
                line = br.readLine();
            }
        }

        return resultStringBuilder.toString();
    }

    public void writeToFile(String filePath, String content) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false))) {
            writer.write(content);
            writer.newLine();
        }
    }
}
