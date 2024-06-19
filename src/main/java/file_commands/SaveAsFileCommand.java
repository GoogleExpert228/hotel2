package file_commands;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class SaveAsFileCommand extends FileCommand {
    String anotherFile;

    public SaveAsFileCommand(String fileName, String anotherFile) {
        super(fileName);
        this.anotherFile = anotherFile;
    }

    @Override
    public void operation() {
        StringBuilder contentToTransfer = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(getFileName()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                contentToTransfer.append(line).append("\n");
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File not found: " + getFileName(), e);
        } catch (IOException e) {
            throw new RuntimeException("Error reading file: " + getFileName(), e);
        }

        writeToFile(anotherFile, contentToTransfer.toString());
    }
}
