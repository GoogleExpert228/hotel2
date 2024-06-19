package file_commands;

import java.util.Scanner;

public class CloseFileCommand extends FileCommand {

    public CloseFileCommand(String fileName) {
        super(fileName);
    }

    @Override
    public void operation() {
        Scanner scanner = new Scanner(System.in);
        if (isFileSaved) {
            System.out.println("File closed successfully!");
        } else {
            System.out.println("Do you want to save the file before closing it?");
            System.out.print("Enter answer y/n:");
            String answer = scanner.nextLine();

            if (answer.equalsIgnoreCase("y")) {
                isFileSaved = true;
                System.out.println("File closed and saved successfully!");
            } else if (answer.equalsIgnoreCase("n")) {
                readWhenClosed(getFileName()); // Читаем файл перед закрытием
                if (contentWhenFileOpening.toString().equals(contentWhenFileClosing.toString())) {
                } else {
                    clearFile(getFileName()); // Очищаем файл
                    writeToFile(getFileName(), contentWhenFileOpening.toString()); // Пишем в файл
                }
                System.out.println("File closed successfully!");
                isFileSaved = false;
            } else {
                System.out.println("This answer is not provided, please answer either y(yes) or n(no)!");
            }
        }
    }
}
