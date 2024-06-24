package hotel;

import additional_commands.*;
import hotel_rooms.*;
import file_commands.*;
import reservations.Registration;
import reservations.RegistrationService;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

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
                if (!currentFile.equals("registrations.xml") && !currentFile.equals("unavailability.xml") && !currentFile.equals("history.xml")) {
                    System.out.println("You can only open the following files: registrations.xml, unavailability.xml, history.xml");
                    continue;
                }
                if (new java.io.File(currentFile).exists()) {
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
                        if (!"registrations.xml".equals(currentFile)) {
                            System.out.println("Please open the registrations.xml file to use the availability command.");
                            break;
                        }
                        if (commandArguments.length == 2) {
                            try {
                                LocalDate date = LocalDate.parse(commandArguments[1]);
                                Availability.operation(date);
                            } catch (DateTimeParseException e) {
                                System.out.println("Invalid date format. Please enter dates in the format yyyy-MM-dd.");
                            }
                        } else {
                            Availability.operation(LocalDate.now());
                        }
                        break;

                    case "report":
                        if (!"history.xml".equals(currentFile)) {
                            System.out.println("Please open the history.xml file to use the report command.");
                            break;
                        }
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
                        if (!"registrations.xml".equals(currentFile)) {
                            System.out.println("Please open the registrations.xml file to use the find command.");
                            break;
                        }
                        if (commandArguments.length == 4) {
                            try {
                                int beds = Integer.parseInt(commandArguments[1]);
                                LocalDate fromDate = LocalDate.parse(commandArguments[2]);
                                LocalDate toDate = LocalDate.parse(commandArguments[3]);
                                Room room = RoomFinder.findRoom(beds, fromDate, toDate);
                            } catch (DateTimeParseException e) {
                                System.out.println("Invalid date format. Please enter dates in the format yyyy-MM-dd.");
                            }
                        } else {
                            System.out.println("Invalid command. Usage: find <beds> <from> <to>");
                        }
                        break;

                    case "unavailable":
                        if (!"unavailability.xml".equals(currentFile)) {
                            System.out.println("Please open the unavailability.xml file to use the unavailable command.");
                            break;
                        }
                        if (commandArguments.length == 5) {
                            try {
                                int room = Integer.parseInt(commandArguments[1]);
                                LocalDate from = LocalDate.parse(commandArguments[2]);
                                LocalDate to = LocalDate.parse(commandArguments[3]);
                                String note = commandArguments[4];
                                UnavailableRoom unavailableRoom = new UnavailableRoom(room, from, to, note);
                                UnavailableRoomService.makeUnavailable(unavailableRoom);
                            } catch (DateTimeParseException e) {
                                System.out.println("Invalid date format. Please enter dates in the format yyyy-MM-dd.");
                            }
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
