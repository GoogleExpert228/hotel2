package rooms_operations;

import enums.RoomType;
import hotel_rooms.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class ExtractRooms {

    public static List<Room> parseRooms(String filePath) {
        List<Room> rooms = new ArrayList<>();

        try {
            File xmlFile = new File(filePath);
            FileInputStream fis = new FileInputStream(xmlFile);

            StringBuilder xmlStringBuilder = new StringBuilder();
            int ch;
            while ((ch = fis.read()) != -1) {
                xmlStringBuilder.append((char) ch);
            }

            String xmlString = xmlStringBuilder.toString();

            int roomStartIndex = xmlString.indexOf("<room>");
            while (roomStartIndex != -1) {
                int roomEndIndex = xmlString.indexOf("</room>", roomStartIndex);
                String roomXml = xmlString.substring(roomStartIndex, roomEndIndex + 7);

                int number = Integer.parseInt(getValueFromTag(roomXml, "number"));
                RoomType roomType = RoomType.valueOf(getValueFromTag(roomXml, "type"));
                boolean available = Boolean.parseBoolean(getValueFromTag(roomXml, "available"));
                String note = getValueFromTag(roomXml, "note");

                switch (roomType) {
                    case RoomType.SINGLE:
                        rooms.add(new SingleRoom(number, available, note));
                        break;
                    case RoomType.DOUBLE:
                        rooms.add(new DoubleRoom(number, available, note));
                        break;
                    case RoomType.TRIPLE:
                        rooms.add(new TripleRoom(number, available, note));
                        break;
                    case RoomType.LUXURY:
                        rooms.add(new LuxuryRoom(number, available, note));
                        break;
                    default:
                        // Handle unknown room type
                        break;
                }

                roomStartIndex = xmlString.indexOf("<room>", roomEndIndex);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rooms;
    }

    private static String getValueFromTag(String xml, String tag) {
        int startIndex = xml.indexOf("<" + tag + ">") + tag.length() + 2;
        int endIndex = xml.indexOf("</" + tag + ">", startIndex);
        return xml.substring(startIndex, endIndex);
    }
}
