package file_commands;

import java.io.*;

public abstract class FileCommand implements Command {
    protected static StringBuilder contentWhenFileOpening = new StringBuilder();
    protected static StringBuilder contentWhenFileClosing = new StringBuilder();
    protected static boolean isFileSaved = false;
    private String fileName;

    public FileCommand(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    protected void readWhenOpen(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                contentWhenFileOpening.append(line).append("\n");
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File not found: " + filePath, e);
        } catch (IOException e) {
            throw new RuntimeException("Error reading file: " + filePath, e);
        }
    }

    protected void readWhenClosed(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                contentWhenFileClosing.append(line).append("\n");
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File not found: " + filePath, e);
        } catch (IOException e) {
            throw new RuntimeException("Error reading file: " + filePath, e);
        }
    }

    protected void clearFile(String filePath) {
        try (FileWriter writer = new FileWriter(filePath, false)) {
            // This opens the file for writing, but since we don't write anything,
            // it effectively clears the content.
        } catch (IOException e) {
            throw new RuntimeException("Error clearing file: " + filePath, e);
        }
    }

    protected void writeToFile(String filePath, String newContent) {

        try (FileWriter writer = new FileWriter(filePath, false)) {
            writer.write(newContent);
        } catch (IOException e) {
            throw new RuntimeException("Error writing to file: " + filePath, e);
        }
    }
}
