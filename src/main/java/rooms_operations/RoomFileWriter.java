package HotelRooms;

import java.io.FileWriter;
import java.io.IOException;

public class RoomFileWriter {
    private static final String fileName = "rooms.xml";

    public static void generateRooms() {
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write("<hotel>\n");
            for (int floor = 1; floor <= 4; floor++) {
                for (int roomNumber = 1; roomNumber <= 5; roomNumber++) {
                    int roomCode = floor * 100 + roomNumber;
                    Room room;
                    if (floor == 1) {
                        room = new SingleRoom(roomCode, true, "Some default text for the single room!");
                    } else if (floor == 2) {
                        room = new DoubleRoom(roomCode, true, "Some default text for the double room!");
                    } else if (floor == 3) {
                        room = new TripleRoom(roomCode, true, "Some default text for the triple room!");
                    } else {
                        room = new LuxuryRoom(roomCode, true, "Some default text for the luxury room!");
                    }
                    writer.write(room.toXML());
                }
            }
            writer.write("</hotel>");
            System.out.println("Rooms generated successfully.");
        } catch (IOException e) {
            System.out.println("An error occurred while generating rooms: " + e.getMessage());
        }
    }
}
