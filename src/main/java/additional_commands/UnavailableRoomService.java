package additional_commands;

import java.io.*;
import java.util.List;

public class UnavailableRoomService {
    private static final String UNAVAILABLE_FILE = "unavailable.xml";

    public static void makeUnavailable(UnavailableRoom room) {
        String roomXML = room.toXML();

        try {
            // Handle the unavailable file
            StringBuilder content = handleFile(new File(UNAVAILABLE_FILE), roomXML);
            if (content == null) {
                return; // If an error occurred or duplicate room was found, exit the method
            }

            // Write the updated content back to the unavailable file
            writeToFile(new File(UNAVAILABLE_FILE), content);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void cancelUnavailable(int roomNumber) {
        try {
            // Load unavailable rooms from the file
            List<UnavailableRoom> unavailableRooms = UnavailableRoomsParser.parseUnavailableRooms(UNAVAILABLE_FILE);

            // Find the room to remove by room number
            UnavailableRoom roomToRemove = null;
            for (UnavailableRoom room : unavailableRooms) {
                if (room.getRoomNumber() == roomNumber) {
                    roomToRemove = room;
                    break;
                }
            }

            // If the room is found, remove it
            if (roomToRemove != null) {
                unavailableRooms.remove(roomToRemove);
                // Rewrite the updated list of unavailable rooms to the file
                writeUnavailableRoomsToXML(unavailableRooms);
                System.out.println("Room " + roomNumber + " is now available.");
            } else {
                System.out.println("No unavailable room found for room number " + roomNumber + ".");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static StringBuilder handleFile(File file, String roomXML) throws IOException {
        StringBuilder content = new StringBuilder();
        boolean isFileNew = !file.exists();

        // If the file exists, read its content
        if (!isFileNew) {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            boolean insideRootElement = false;

            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
                if (line.trim().equals("<unavailabilities>")) {
                    insideRootElement = true;
                }
            }

            reader.close();

            // Check if the room already exists
            if (content.indexOf(roomXML) != -1) {
                System.out.println("Duplicate unavailable room. Not adding to file.");
                return null;
            }

            // If insideRootElement is false, it means the XML structure is corrupted
            if (!insideRootElement) {
                System.err.println("Error: The XML file is corrupted.");
                return null;
            }

            // Remove the closing tag of root element to append new room
            int lastIndex = content.lastIndexOf("</unavailabilities>");
            if (lastIndex != -1) {
                content.delete(lastIndex, content.length());
            }
        } else {
            content.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            content.append("<unavailabilities>\n");
        }

        // Append the new room
        content.append(roomXML);
        content.append("</unavailabilities>");

        return content;
    }

    private static void writeToFile(File file, StringBuilder content) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(content.toString());
        writer.close();
    }

    private static void writeUnavailableRoomsToXML(List<UnavailableRoom> rooms) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(UNAVAILABLE_FILE));
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write("<unavailabilities>\n");
            for (UnavailableRoom room : rooms) {
                writer.write(room.toXML());
            }
            writer.write("</unavailabilities>");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
