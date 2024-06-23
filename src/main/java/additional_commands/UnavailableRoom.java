package additional_commands;

import java.time.LocalDate;

public class UnavailableRoom {
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

    public int getRoomNumber() {
        return roomNumber;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public String getNote() {
        return note;
    }

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
