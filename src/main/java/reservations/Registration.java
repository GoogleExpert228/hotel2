package reservations;

import java.time.LocalDate;
import java.io.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Registration {
    private int roomNumber;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private String note;
    private int guestsNumber;

    public Registration(int roomNumber, LocalDate checkInDate, LocalDate checkOutDate, String note) {
        this.roomNumber = roomNumber;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.note = note;
    }

    public Registration(int roomNumber, LocalDate checkInDate, LocalDate checkOutDate, String note, int guestsNumber) {
        this.roomNumber = roomNumber;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.note = note;
        this.guestsNumber = guestsNumber;
    }

    public int getRoomNumber() { return roomNumber; }
    public LocalDate getCheckInDate() {
        return checkInDate;
    }
    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }
    public String getNote() { return note; }
    public int getGuestsNumber() { return guestsNumber; }

    public String toXML() {
        return String.format(
                        "<registration>\n" +
                                "   <number>%s</number>\n" +
                                "   <checkInDate>%s</checkInDate>\n" +
                                "   <checkOutDate>%s</checkOutDate>\n" +
                                "   <note>%s</note>\n" +
                                "   <guestsNumber>%s</guestsNumber>\n" +
                        "</registration>\n\n",
                getRoomNumber(), getCheckInDate(), getCheckOutDate(), getNote(), getGuestsNumber());
    }

    public static void main(String[] args) {

        List<Registration> r1 = RegistrationParser.parseRegistrations("registrations.xml");

        for(Registration registration: r1) {
            System.out.println(registration.roomNumber);
        }



        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter command: ");
        String command = scanner.nextLine();

        if (command.startsWith("checkin")) {
            String[] parts = command.split(" ");
            if (parts.length >= 5) {
                try {
                    int roomNumber = Integer.parseInt(parts[1]);
                    LocalDate checkInDate = LocalDate.parse(parts[2], DateTimeFormatter.ISO_LOCAL_DATE);
                    LocalDate checkOutDate = LocalDate.parse(parts[3], DateTimeFormatter.ISO_LOCAL_DATE);
                    String note = parts[4];
                    int guestsNumber = parts.length == 6 ? Integer.parseInt(parts[5]) : 2;

                    Registration registration = new Registration(roomNumber, checkInDate, checkOutDate, note, guestsNumber);
                    RegistrationService.checkIn(registration);

                    System.out.printf("Registered room number %d from %s to %s \n",
                            roomNumber, checkInDate, checkOutDate, note, guestsNumber);
                } catch (NumberFormatException | DateTimeParseException e) {
                    System.err.println("Invalid input format. Please use: checkin <room> <from> <to> <note> [<guests>]");
                }
            } else {
                System.err.println("Insufficient parameters. Please use: checkin <room> <from> <to> <note> [<guests>]");
            }
        } else {
            System.err.println("Unknown command.");
        }
    }
}

