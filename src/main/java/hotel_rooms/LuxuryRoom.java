package hotel_rooms;

import enums.RoomType;

public class LuxuryRoom extends Room {
    public LuxuryRoom(int roomNumber, boolean isAvailable, String note) {
        super(roomNumber, 5, isAvailable, note, RoomType.LUXURY);
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
