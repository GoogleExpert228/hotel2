package additional_commands;

import hotel_rooms.Room;
import reservations.Registration;
import reservations.RegistrationParser;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static rooms_operations.ExtractRooms.parseRooms;

public class Availability {
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
