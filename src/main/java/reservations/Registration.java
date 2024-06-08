package reservations;

import java.time.LocalDate;

public class Reservation {
    private int roomNumber;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private String note;
    private int guestsNumber;

    public Reservation(int roomNumber, LocalDate checkInDate, LocalDate checkOutDate, String note) {
        this.roomNumber = roomNumber;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.note = note;
    }

    public Reservation(int roomNumber, LocalDate checkInDate, LocalDate checkOutDate, String note, int guestsNumber) {
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
                        "<reservation>\n" +
                                "   <number>%s</number>\n" +
                                "   <checkInDate>%s</checkInDate>\n" +
                                "   <checkOutDate>%s</checkOutDate>\n" +
                                "   <note>%s</note>\n" +
                                "   <guestNumber>%s</guestNumber>\n" +
                        "</reservation>\n",
                getRoomNumber(), getCheckInDate(), getCheckOutDate(), getNote(), getGuestsNumber());
    }
}
