package hotel;
import additional_commands.Report;
import hotel_rooms.*;
import file_commands.*;
import reservations.Registration;
import reservations.RegistrationParser;
import reservations.RegistrationService;
import rooms_operations.RoomEditor;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static rooms_operations.ExtractRooms.parseRooms;




class SomeClass {
    private static List<Registration> registrationList = RegistrationParser.parseRegistrations("registrations.xml");
    private static List<Integer> roomsNumber = new ArrayList<>();
    public static void operation(LocalDate checkDate) {
        List<Room> rooms = parseRooms("rooms.xml");


        roomsNumber.clear();

        for(Registration registration : registrationList) {
            if (checkDate.isAfter(registration.getCheckInDate().minusDays(0)) &&
                    checkDate.isBefore(registration.getCheckOutDate().plusDays(0))) {
                roomsNumber.add(registration.getRoomNumber());
            }
        }

        for(Room room : rooms) {
            if(!(roomsNumber.contains((room.getRoomNumber())))) {
                System.out.println(room.toString());
            }
        }
    }
}

class UnavailableRoom {
    private int roomNumber;
    private LocalDate startDate;
    private LocalDate endDate;
    private String note;

    public UnavailableRoom(int roomNumber, LocalDate startDate, LocalDate endDate, String note) {
        this.roomNumber = roomNumber;
        this.startDate = startDate;
        this.endDate = endDate;
        this.note = note;
    }

    public int getRoomNumber() {return roomNumber;}
    public LocalDate getStartDate() {return startDate;}
    public LocalDate getEndDate() {return endDate; }
    public String getNote() {return note; }

    public String toXML() {
        return String.format(
                        "<unavailable>\n" +
                        "   <number>%s</number>\n" +
                        "   <startDate>%s</startDate>\n" +
                        "   <endDate>%s</endDate>\n" +
                        "   <note>%s</note>\n" +
                        "</unavailable>\n\n",
                getRoomNumber(), getStartDate(), getEndDate(), getNote());
    }
}

class UnavailableRoomsParser {

    public static List<UnavailableRoom> parseUnavailableRooms(String filePath) {
        List<UnavailableRoom> unavailableRooms = new ArrayList<>();

        try {
            File xmlFile = new File(filePath);
            FileInputStream fis = new FileInputStream(xmlFile);

            StringBuilder xmlStringBuilder = new StringBuilder();
            int ch;
            while ((ch = fis.read()) != -1) {
                xmlStringBuilder.append((char) ch);
            }

            String xmlString = xmlStringBuilder.toString();

            int unavailableStartIndex = xmlString.indexOf("<unavailable>");
            while (unavailableStartIndex != -1) {
                int unavailableEndIndex = xmlString.indexOf("</unavailable>", unavailableStartIndex);
                String unavailableXml = xmlString.substring(unavailableStartIndex, unavailableEndIndex + 14);

                int roomNumber = Integer.parseInt(getValueFromTag(unavailableXml, "number"));
                LocalDate startDate = LocalDate.parse(getValueFromTag(unavailableXml, "startDate"), DateTimeFormatter.ISO_LOCAL_DATE);
                LocalDate endDate = LocalDate.parse(getValueFromTag(unavailableXml, "endDate"), DateTimeFormatter.ISO_LOCAL_DATE);
                String note = getValueFromTag(unavailableXml, "note");

                unavailableRooms.add(new UnavailableRoom(roomNumber, startDate, endDate, note));

                unavailableStartIndex = xmlString.indexOf("<unavailable>", unavailableEndIndex);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return unavailableRooms;
    }

    private static String getValueFromTag(String xml, String tag) {
        int startIndex = xml.indexOf("<" + tag + ">") + tag.length() + 2;
        int endIndex = xml.indexOf("</" + tag + ">", startIndex);
        return xml.substring(startIndex, endIndex);
    }
}

class UnavailableRoomService {
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

public class Main {
    public static void main(String[] args) {
        /*
        UnavailableRoomService.cancelUnavailable(101);
        Report.usedRooms( LocalDate.of(2024, 5, 14),  LocalDate.of(2024, 9, 14));
        //RoomFileWriter.generateRooms();
        RoomEditor.editRoom(101, "new first note");
        RoomEditor.editRoom(105, "new second note");
        // RoomEditor.editRoom(101, false);
        // RoomEditor.editRoom(203, false);

        SomeClass.operation();


         */
        Scanner scanner = new Scanner(System.in);
        boolean isFileOpen = false;
        String currentFile = null;

        System.out.println("If you don't know commands name, enter help command then\n");
        System.out.println("First you need to open the file: ");
        System.out.println("registrations.xml: if you want to checkIn, checkOut room or availability(list of free rooms) or find");
        System.out.println("unavailability.xml: if you want to unavailable any room");
        System.out.println("history.xml: if you want report\n");

        while (true) {
            System.out.print("Enter the command: ");
            String command = scanner.nextLine();
            String[] commandArguments = command.split("\\s+");

            if (commandArguments[0].equalsIgnoreCase("open") && commandArguments.length == 2) {
                currentFile = commandArguments[1];
                Command openFileCommand = new OpenFileCommand(currentFile);
                openFileCommand.operation();
                isFileOpen = true;
            }

            if (isFileOpen) {
                switch (commandArguments[0].toLowerCase()) {
                    case "close":
                        Command closeFileCommand = new CloseFileCommand(currentFile);
                        closeFileCommand.operation();
                        isFileOpen = false;
                        break;

                    case "checkin":
                        if (commandArguments.length >= 5) {
                            try {
                                int room = Integer.parseInt(commandArguments[1]);
                                LocalDate from = LocalDate.parse(commandArguments[2]);
                                LocalDate to = LocalDate.parse(commandArguments[3]);
                                String note = commandArguments[4];
                                int guests = commandArguments.length == 6 ? Integer.parseInt(commandArguments[5]) : 2; //to correct it
                                Registration registration = new Registration(room, from, to, note, guests);
                                RegistrationService.checkIn(registration);
                            } catch (DateTimeParseException e) {
                                System.out.println("Invalid date format. Please enter dates in the format yyyy-MM-dd.");
                            }
                        }  else {
                            System.out.println("Invalid command. Usage: checkin <room> <from> <to> <note> [<guests>]");
                        }
                        break;

                    case "availability":
                        LocalDate date = commandArguments.length == 2 ? LocalDate.parse(commandArguments[1]) : LocalDate.now();
                        SomeClass.operation(LocalDate.of(2024, 7, 15));
                        break;

                    case "checkout":
                        if (commandArguments.length == 2) {
                            int room = Integer.parseInt(commandArguments[1]);
                            RegistrationService.checkOut(room);
                        } else {
                            System.out.println("Invalid command. Usage: checkout <room>");
                        }
                        break;

                    case "report":
                        if (commandArguments.length == 3) {
                            try {
                                LocalDate from = LocalDate.parse(commandArguments[1]);
                                LocalDate to = LocalDate.parse(commandArguments[2]);
                                Report.usedRooms(from, to);
                            } catch (DateTimeParseException e) {
                                System.out.println("Invalid date format. Please enter dates in the format yyyy-MM-dd.");
                            }
                        } else {
                            System.out.println("Invalid command. Usage: report <from> <to>");
                        }
                        break;

                    case "find":
                        if (commandArguments.length == 4) {
                            int beds = Integer.parseInt(commandArguments[1]);
                            LocalDate from = LocalDate.parse(commandArguments[2]);
                            LocalDate to = LocalDate.parse(commandArguments[3]);
                 //           RoomService.findRoom(beds, from, to);
                        } else {
                            System.out.println("Invalid command. Usage: find <beds> <from> <to>");
                        }
                        break;

                    case "unavailable":
                        if (commandArguments.length == 5) {
                            int room = Integer.parseInt(commandArguments[1]);
                            LocalDate from = LocalDate.parse(commandArguments[2]);
                            LocalDate to = LocalDate.parse(commandArguments[3]);
                            String note = commandArguments[4];
                            UnavailableRoom unavailableRoom = new UnavailableRoom(room, from, to, note);
                            UnavailableRoomService.makeUnavailable(unavailableRoom);
                        } else {
                            System.out.println("Invalid command. Usage: unavailable <room> <from> <to> <note>");
                        }
                        break;

                    case "save":
                        Command saveFileCommand = new SaveFileCommand(currentFile);
                        saveFileCommand.operation();
                        break;

                    case "saveas":
                        if (commandArguments.length == 2) {
                            Command saveAsFileCommand = new SaveAsFileCommand(currentFile, commandArguments[1]);
                            saveAsFileCommand.operation();
                        } else {
                            System.out.println("Invalid command. Usage: saveas <file>");
                        }
                        break;
                }
            } else {
                if (!commandArguments[0].equalsIgnoreCase("help") && !commandArguments[0].equalsIgnoreCase("exit")) {
                    System.out.println("First you need to open the file!");
                }
            }

            if (commandArguments[0].equalsIgnoreCase("help")) {
                System.out.println("\nThe following commands are supported:\n"
                        + " open <file> - opens <file>\n"
                        + " close - closes currently opened file\n"
                        + " save - saves the currently open file\n"
                        + " saveas <file> - saves the currently open file in <file>\n"
                        + " checkin <room> <from> <to> <note> [<guests>] - register in a room\n"
                        + " availability [<date>] - shows available rooms on <date>\n"
                        + " checkout <room> - checks out from a room\n"
                        + " report <from> <to> - reports room usage between <from> and <to>\n"
                        + " find <beds> <from> <to> - finds a room with <beds> between <from> and <to>\n"
                        + " unavailable <room> <from> <to> <note> - marks a room as unavailable\n"
                        + " help - prints this information\n"
                        + " exit - exits the program\n");
                continue;
            }

            if (commandArguments[0].equalsIgnoreCase("exit")) {
                System.out.println("Exiting the program...");
                break;
            }
        }
    }
}

//checkIn 303 2024-07-06 2024-07-12 "some text" 2