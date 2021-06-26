package com.cancun.api.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import com.cancun.api.model.Room;

public class RoomService {

	public static int MAX_RESERVATION_DAYS = 3;
	public static int MIN_DAYS_BEFORE_RESERVATION = 1;
	public static int MAX_DAYS_BEFORE_RESERVATION = 30;

	public static boolean validateRoomUpdateParameters(Room room, LocalDate startDate, LocalDate endDate) {
		return room.isAvailableForReservation(startDate, endDate)
		&& RoomService.areDatesCorrect(startDate, endDate);
	}
	
	private static boolean areDatesCorrect(LocalDate startDate, LocalDate endDate) {
        LocalDate todayDate = LocalDate.now();
        long reservationDuration = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        long daysBeforeReservationStarts = ChronoUnit.DAYS.between(todayDate, startDate);

		return reservationDuration <= MAX_RESERVATION_DAYS
        		&& daysBeforeReservationStarts >= MIN_DAYS_BEFORE_RESERVATION
        		&& daysBeforeReservationStarts <= MAX_DAYS_BEFORE_RESERVATION
        		&& startDate.isBefore(endDate);
	}
}
