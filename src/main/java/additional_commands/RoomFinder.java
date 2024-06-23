package additional_commands;

import hotel_rooms.Room;
import reservations.Registration;
import reservations.RegistrationParser;
import rooms_operations.ExtractRooms;
import rooms_operations.RoomEditor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RoomFinder {

    public static Room findRoom(int beds, LocalDate fromDate, LocalDate toDate) {
        // Загрузка списка комнат из файла
        List<Room> rooms = ExtractRooms.parseRooms("rooms.xml");
        // Загрузка списка регистраций из файла
        List<Registration> registrations = RegistrationParser.parseRegistrations("registrations.xml");

        List<Room> availableRooms = new ArrayList<>();

        for (Room room : rooms) {
            if (room.getRoomCapacity() >= beds) {
                boolean isRoomOccupied = false;

                for (Registration registration : registrations) {
                    boolean isSameRoom = registration.getRoomNumber() == room.getRoomNumber();
                    boolean isOverlap = !(toDate.isBefore(registration.getCheckInDate()) || fromDate.isAfter(registration.getCheckOutDate()));

                    if (isSameRoom && isOverlap) {
                        isRoomOccupied = true;
                        break;
                    }
                }

                if (!isRoomOccupied) {
                    availableRooms.add(room);
                }
            }
        }

        if (availableRooms.isEmpty()) {
            System.out.println("No suitable room found.");
            return null;
        }

        // Предпочитаем комнаты с наименьшим количеством кроватей
        availableRooms.sort(Comparator.comparingInt(Room::getRoomCapacity));
        System.out.println("Found room: " + availableRooms.get(0).getRoomNumber() + " for " + beds + " beds from " + fromDate + " to " + toDate + ".");
        return availableRooms.get(0);

    }

    public static void main(String[] args) {
        // Предположим, что у нас есть список комнат и регистраций, которые мы загрузили из файлов
        List<Room> rooms = ExtractRooms.parseRooms("rooms.xml");
        List<Registration> registrations = RegistrationParser.parseRegistrations("registrations.xml");

        // Данные для поиска комнаты
        int beds = 3;
        LocalDate fromDate = LocalDate.of(2024, 7, 1);
        LocalDate toDate = LocalDate.of(2024, 7, 5);

        Room room = RoomFinder.findRoom(beds, fromDate, toDate);

    }
}


