package file_commands;

import java.io.File;

public class OpenFileCommand extends FileCommand {

    public OpenFileCommand(String fileName) {
        super(fileName);
    }

    @Override
    public void operation() {
        File fileForClosing = new File(getFileName());
        if (fileForClosing.exists()) {
            System.out.println("File exists");
            readWhenOpen(getFileName());
            System.out.println(contentWhenFileOpening.toString());
        } else
            System.out.println("File not found");
    }
}
