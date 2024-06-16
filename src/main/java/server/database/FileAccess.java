package server.database;

import java.io.*;

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
