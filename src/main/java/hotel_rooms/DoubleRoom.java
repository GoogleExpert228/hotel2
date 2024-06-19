package hotel_rooms;

import enums.RoomType;

public class DoubleRoom extends Room {

    public DoubleRoom(int roomNumber, boolean isAvailable, String note) {
        super(roomNumber, 3, isAvailable, note, RoomType.DOUBLE);
    }

    @Override
    public String toXML() {
        return String.format(
                "<room>\n" +
                        "   <number>%d</number>\n" +
                        "   <type>%s</type>\n" +
                        "   <capacity>%d</capacity>\n" +
                        "   <available>%b</available>\n" +
                        "   <note>%s</note>\n" +
                        "</room>\n",
                getRoomNumber(), getRoomType(), getRoomCapacity(), isAvailable(), getNote());
    }
}
