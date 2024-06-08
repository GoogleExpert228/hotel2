package HotelRooms;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class RoomEditor {
    private static final String fileName = "rooms.xml"; // Укажите путь к вашему файлу

    public static void editRoom(int roomNumber, String newNote) {
        try {
            Path path = Paths.get(fileName);
            List<String> lines = Files.readAllLines(path);
            for (int i = 0; i < lines.size() - 4; i++) {
                String line = lines.get(i);
                if (line.contains("<number>" + roomNumber + "</number>")) {
                    // Найдена строка с нужным номером комнаты
                    lines.set(i + 4, "   <note>" + newNote + "</note>");
                    break;
                }
            }
            Files.write(path, lines);
            System.out.println("Room " + roomNumber + " edited successfully.");
        } catch (IOException e) {
            System.out.println("An error occurred while editing room " + roomNumber + ": " + e.getMessage());
        }
    }

    public static void editRoom(int roomNumber, boolean isAvailable) {
        try {
            Path path = Paths.get(fileName);
            List<String> lines = Files.readAllLines(path);
            for (int i = 0; i < lines.size() - 3; i++) {
                String line = lines.get(i);
                if (line.contains("<number>" + roomNumber + "</number>")) {
                    // Найдена строка с нужным номером комнаты
                    lines.set(i + 3, "   <available>" + isAvailable + "</available>");
                    break;
                }
            }
            Files.write(path, lines);
            System.out.println("Room " + roomNumber + " edited successfully.");
        } catch (IOException e) {
            System.out.println("An error occurred while editing room " + roomNumber + ": " + e.getMessage());
        }
    }
}
