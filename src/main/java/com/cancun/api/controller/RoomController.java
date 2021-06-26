package com.cancun.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cancun.api.model.Reservation;
import com.cancun.api.model.Room;
import com.cancun.api.repository.ReservationRepository;
import com.cancun.api.repository.RoomRepository;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.time.temporal.ChronoUnit;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {
	static int MAX_RESERVATION_DAYS = 3;
	static int MIN_DAYS_BEFORE_RESERVATION = 1;
	static int MAX_DAYS_BEFORE_RESERVATION = 30;
	
	@Autowired
    private RoomRepository roomRepository;
	
	@Autowired
    private ReservationRepository reservationRepository;
        
    @GetMapping
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    @PostMapping("/{id}/book")
    public ResponseEntity<Room> bookRoom(
    		@PathVariable(value = "id") long id,
    		@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(value = "start_date") LocalDate startDate, 
    		@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(value = "end_date") LocalDate endDate) {
        Optional<Room> room = roomRepository.findById(id);
        
        if(room.isEmpty()) {
        	return ResponseEntity.notFound().build();
        }
        
        if(!room.get().isAvailableForReservation(startDate, endDate)) {
        	return ResponseEntity.unprocessableEntity().build();
        }
        
        LocalDate todayDate = LocalDate.now();
        long reservationDuration = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        long daysBeforeReservationStarts = ChronoUnit.DAYS.between(todayDate, startDate);

        if(reservationDuration > MAX_RESERVATION_DAYS
        		|| daysBeforeReservationStarts < MIN_DAYS_BEFORE_RESERVATION
        		|| daysBeforeReservationStarts > MAX_DAYS_BEFORE_RESERVATION
        		|| startDate.isAfter(endDate)) {
        	return ResponseEntity.unprocessableEntity().build();
        }
        
        Reservation reservation = new Reservation();
        reservation.setRoom(room.get());
        reservation.setStartDate(startDate);
        reservation.setEndDate(endDate);
     
        Reservation newReservation = reservationRepository.save(reservation);
        
        return ResponseEntity.ok().body(room.get().addReservation(newReservation));
    }
    
    @GetMapping("/{id}/is-available")
    public ResponseEntity<Boolean> isRoomAvailable(
    		@PathVariable(value = "id") long id,
    		@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(value = "start_date") LocalDate startDate, 
    		@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(value = "end_date") LocalDate endDate) {
        Optional<Room> room = roomRepository.findById(id);
        
        return ResponseEntity.ok().body(room.get().isAvailableForReservation(startDate, endDate));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Room> findRoomById(@PathVariable(value = "id") long id) {
        Optional<Room> room = roomRepository.findById(id);
        
        return room.isPresent()
        		? ResponseEntity.ok().body(room.get())
        		: ResponseEntity.notFound().build();
    }
}
