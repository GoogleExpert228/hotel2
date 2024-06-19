package file_commands;

public class SaveFileCommand extends FileCommand {

    public SaveFileCommand(String fileName) {
        super(fileName);
    }

    @Override
    public void operation() {
        isFileSaved = true;
        contentWhenFileOpening = contentWhenFileClosing;
    }
}
