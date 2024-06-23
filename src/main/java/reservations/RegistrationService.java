package reservations;

import additional_commands.UnavailableRoom;
import additional_commands.UnavailableRoomsParser;
import rooms_operations.RoomEditor;

import java.io.*;
import java.time.LocalDate;
import java.util.List;

public class RegistrationService {
    private static final String REGISTRATIONS_FILE = "registrations.xml";
    private static final String HISTORY_FILE = "history.xml";

    public List<Registration> loadRegistrations(String filePath) {
        return RegistrationParser.parseRegistrations(filePath);
    }

    public static void checkIn(Registration registration) {
        String reservationXML = registration.toXML();
        LocalDate registrationStartDate = registration.getCheckInDate();
        LocalDate registrationEndDate = registration.getCheckOutDate();

        try {
            // Load unavailable rooms
            List<UnavailableRoom> unavailableRooms = UnavailableRoomsParser.parseUnavailableRooms("unavailable.xml");

            // Check if the room is unavailable during the registration period
            for (UnavailableRoom room : unavailableRooms) {
                System.out.println("Unavailable Room: " + room.getRoomNumber());
                System.out.println("Start Date: " + room.getStartDate() + ", End Date: " + room.getEndDate());

                boolean isSameRoom = room.getRoomNumber() == registration.getRoomNumber();
                boolean isOverlap = !(registrationEndDate.isBefore(room.getStartDate()) || registrationStartDate.isAfter(room.getEndDate()));

                if (isSameRoom && isOverlap) {
                    System.out.println("Room " + registration.getRoomNumber() + " is unavailable from " +
                            room.getStartDate() + " to " + room.getEndDate() + ". Note: " + room.getNote());
                    return;
                }
            }

            // Handle the registrations file
            StringBuilder contentRegistrations = handleFile(new File(REGISTRATIONS_FILE), reservationXML);
            if (contentRegistrations == null) {
                return; // If an error occurred or duplicate reservation was found, exit the method
            }

            // Handle the history file
            StringBuilder contentHistory = handleFile(new File(HISTORY_FILE), reservationXML);
            if (contentHistory == null) {
                return; // If an error occurred or duplicate reservation was found, exit the method
            }

            // Write the updated content back to the registrations file
            writeToFile(new File(REGISTRATIONS_FILE), contentRegistrations);

            // Write the updated content back to the history file
            writeToFile(new File(HISTORY_FILE), contentHistory);
            System.out.println("Registration for room " + registration.getRoomNumber() + " has been checked in.");
            RoomEditor.editRoom(registration.getRoomNumber(), false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static StringBuilder handleFile(File file, String reservationXML) throws IOException {
        StringBuilder content = new StringBuilder();
        boolean isFileNew = !file.exists();

        // If the file exists, read its content
        if (!isFileNew) {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            boolean insideRootElement = false;

            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
                if (line.trim().equals("<registrations>")) {
                    insideRootElement = true;
                }
            }

            reader.close();

            // Check if the reservation already exists
            if (content.indexOf(reservationXML) != -1) {
                System.out.println("Duplicate reservation. Not adding to file.");
                return null;
            }

            // If insideRootElement is false, it means the XML structure is corrupted
            if (!insideRootElement) {
                System.err.println("Error: The XML file is corrupted.");
                return null;
            }

            // Remove the closing tag of root element to append new reservation
            int lastIndex = content.lastIndexOf("</registrations>");
            if (lastIndex != -1) {
                content.delete(lastIndex, content.length());
            }
        } else {
            content.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            content.append("<registrations>\n");
        }

        // Append the new reservation
        content.append(reservationXML);
        content.append("</registrations>");

        return content;
    }

    private static void writeToFile(File file, StringBuilder content) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(content.toString());
        writer.close();
    }


    public static void checkOut(int roomNumber) {
        try {
            // Загрузка регистраций из файла
            List<Registration> registrations = RegistrationParser.parseRegistrations(REGISTRATIONS_FILE);

            // Поиск регистрации по номеру комнаты
            Registration registrationToRemove = null;
            for (Registration registration : registrations) {
                if (registration.getRoomNumber() == roomNumber) {
                    registrationToRemove = registration;
                    break;
                }
            }

            // Если регистрация найдена, удалить ее
            if (registrationToRemove != null) {
                registrations.remove(registrationToRemove);
                // Перезапись обновленного списка регистраций в файл
                writeRegistrationsToXML(registrations);
                System.out.println("Registration for room " + roomNumber + " has been checked out.");
                RoomEditor.editRoom(roomNumber, true);
            } else {
                System.out.println("No registration found for room " + roomNumber + ".");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Метод для записи обновленного списка регистраций в файл
    private static void writeRegistrationsToXML(List<Registration> registrations) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(REGISTRATIONS_FILE));
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            writer.write("<registrations>\n");
            for (Registration registration : registrations) {
                writer.write(registration.toXML());
            }
            writer.write("</registrations>");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
