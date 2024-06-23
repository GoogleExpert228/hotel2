package hotel;

import additional_commands.Report;
import additional_commands.UnavailableRoom;
import additional_commands.UnavailableRoomService;
import hotel_rooms.*;
import file_commands.*;
import reservations.Registration;
import reservations.RegistrationParser;
import reservations.RegistrationService;

import java.time.LocalDate;
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

        for (Registration registration : registrationList) {
            if (checkDate.isAfter(registration.getCheckInDate().minusDays(0)) &&
                    checkDate.isBefore(registration.getCheckOutDate().plusDays(0))) {
                roomsNumber.add(registration.getRoomNumber());
            }
        }

        for (Room room : rooms) {
            if (!(roomsNumber.contains((room.getRoomNumber())))) {
                System.out.println(room.toString());
            }
        }
    }
}

public class Main {
    public static void main(String[] args) {
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
                if (new java.io.File(currentFile).exists()) {
                    System.out.println("File exists");
                    Command openFileCommand = new OpenFileCommand(currentFile);
                    openFileCommand.operation();
                    isFileOpen = true;
                } else {
                    System.out.println("File not found: " + currentFile);
                }
                continue;  // Continue to next iteration to avoid printing "Unknown command"
            }

            if (isFileOpen) {
                switch (commandArguments[0].toLowerCase()) {
                    case "close":
                        Command closeFileCommand = new CloseFileCommand(currentFile);
                        closeFileCommand.operation();
                        isFileOpen = false;
                        currentFile = null;
                        break;

                    case "checkin":
                        if (!"registrations.xml".equals(currentFile)) {
                            System.out.println("Please open the registrations.xml file to use the checkin command.");
                            break;
                        }
                        if (commandArguments.length >= 5) {
                            try {
                                int room = Integer.parseInt(commandArguments[1]);
                                LocalDate from = LocalDate.parse(commandArguments[2]);
                                LocalDate to = LocalDate.parse(commandArguments[3]);
                                String note = commandArguments[4];
                                int guests = commandArguments.length == 6 ? Integer.parseInt(commandArguments[5]) : 2;
                                Registration registration = new Registration(room, from, to, note, guests);
                                RegistrationService.checkIn(registration);
                            } catch (DateTimeParseException e) {
                                System.out.println("Invalid date format. Please enter dates in the format yyyy-MM-dd.");
                            }
                        } else {
                            System.out.println("Invalid command. Usage: checkin <room> <from> <to> <note> [<guests>]");
                        }
                        break;

                    case "checkout":
                        if (!"registrations.xml".equals(currentFile)) {
                            System.out.println("Please open the registrations.xml file to use the checkout command.");
                            break;
                        }
                        if (commandArguments.length == 2) {
                            int room = Integer.parseInt(commandArguments[1]);
                            RegistrationService.checkOut(room);
                        } else {
                            System.out.println("Invalid command. Usage: checkout <room>");
                        }
                        break;

                    case "availability":
                        if (commandArguments.length == 2) {
                            try {
                                LocalDate date = LocalDate.parse(commandArguments[1]);
                                SomeClass.operation(date);
                            } catch (DateTimeParseException e) {
                                System.out.println("Invalid date format. Please enter dates in the format yyyy-MM-dd.");
                            }
                        } else {
                            SomeClass.operation(LocalDate.now());
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
                            // RoomService.findRoom(beds, from, to);
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

                    default:
                        System.out.println("Unknown command. Use help to see the list of available commands.");
                        break;
                }
            } else {
                if (!commandArguments[0].equalsIgnoreCase("help") && !commandArguments[0].equalsIgnoreCase("exit") && !commandArguments[0].equalsIgnoreCase("open")) {
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
            }

            if (commandArguments[0].equalsIgnoreCase("exit")) {
                System.out.println("Exiting the program...");
                break;
            }
        }
    }
}