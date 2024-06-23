package additional_commands;

import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class UnavailableRoomsParser {

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
