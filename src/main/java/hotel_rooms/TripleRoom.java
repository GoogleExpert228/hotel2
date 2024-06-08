package HotelRooms;

import Enums.RoomType;
import HotelRooms.Room;

public class TripleRoom extends Room {

    public TripleRoom(int roomNumber, boolean isAvailable, String note) {
        super(roomNumber, 5, isAvailable, note, RoomType.TRIPLE);
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
