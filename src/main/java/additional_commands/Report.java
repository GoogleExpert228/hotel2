package additional_commands;

import reservations.Registration;
import reservations.RegistrationParser;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class Report {
    private static List<Registration> registrationList = RegistrationParser.parseRegistrations("history.xml");

    public static void usedRooms(LocalDate startDate, LocalDate endDate) {
        List<Registration> filteredRegistrations = new ArrayList<>();

        // Filter registrations within the specified date range
        for (Registration registration : registrationList) {
            if ((registration.getCheckInDate().isEqual(startDate) || registration.getCheckInDate().isAfter(startDate)) &&
                    (registration.getCheckOutDate().isEqual(endDate) || registration.getCheckOutDate().isBefore(endDate))) {
                filteredRegistrations.add(registration);
            }
        }

        // Aggregate the days for each room number
        List<Integer> processedRoomNumbers = new ArrayList<>();
        for (int i = 0; i < filteredRegistrations.size(); i++) {
            Registration reg1 = filteredRegistrations.get(i);
            if (!processedRoomNumbers.contains(reg1.getRoomNumber())) {
                long totalDays = ChronoUnit.DAYS.between(reg1.getCheckInDate(), reg1.getCheckOutDate());

                for (int j = i + 1; j < filteredRegistrations.size(); j++) {
                    Registration reg2 = filteredRegistrations.get(j);
                    if (reg1.getRoomNumber() == reg2.getRoomNumber()) {
                        totalDays += ChronoUnit.DAYS.between(reg2.getCheckInDate(), reg2.getCheckOutDate());
                    }
                }

                System.out.println("Room number: " + reg1.getRoomNumber() + " used for " + totalDays + " days.");
                processedRoomNumbers.add(reg1.getRoomNumber());
            }
        }
    }
}