package com.cancun.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cancun.api.model.Reservation;
import com.cancun.api.model.Room;
import com.cancun.api.repository.ReservationRepository;
import com.cancun.api.repository.RoomRepository;
import com.cancun.api.service.RoomService;

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
        
        if(!RoomService.validateRoomUpdateParameters(room.get(), startDate, endDate)) {
        	return ResponseEntity.unprocessableEntity().build();
        }
        
        Reservation reservation = new Reservation(room.get(), startDate, endDate);
     
        Reservation newReservation = reservationRepository.save(reservation);
        
        return ResponseEntity.ok().body(room.get().book(newReservation));
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
    
    @DeleteMapping("/{id}/reservations/{reservationId}")
    public ResponseEntity<Room> unbookRoom(
    		@PathVariable(value = "id") long id,
    		@PathVariable(value = "reservationId") long reservationId) {
        Optional<Room> room = roomRepository.findById(id);
        Optional<Reservation> reservation = reservationRepository.findById(reservationId);
        
        if(room.isEmpty() || reservation.isEmpty()) {
        	return ResponseEntity.notFound().build();
        }
        
        if(!reservation.get().belongsToRoom(room.get())) {
        	return ResponseEntity.unprocessableEntity().build();
        }
        
        reservationRepository.deleteById(reservation.get().getId());
        
        return ResponseEntity.ok().body(room.get().unbook(reservation.get()));
    }
    
    @PatchMapping("/{id}/reservations/{reservationId}")
    public ResponseEntity<Room> rebookRoom(
    		@PathVariable(value = "id") long id,
    		@PathVariable(value = "reservationId") long reservationId,
    		@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(value = "start_date") LocalDate startDate, 
    		@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam(value = "end_date") LocalDate endDate) {
        Optional<Room> room = roomRepository.findById(id);
        Optional<Reservation> reservation = reservationRepository.findById(reservationId);
        
        if(room.isEmpty() || reservation.isEmpty()) {
        	return ResponseEntity.notFound().build();
        }
        
        room.get().unbook(reservation.get());
        if(!reservation.get().belongsToRoom(room.get())
        		|| !RoomService.validateRoomUpdateParameters(room.get(), startDate, endDate)) {
        	return ResponseEntity.unprocessableEntity().build();
        }
        
        Reservation newReservation = reservation.get();
        newReservation.setStartDate(startDate);
        newReservation.setEndDate(endDate);
        reservationRepository.save(newReservation);
        
        return ResponseEntity.ok().body(room.get().book(newReservation));
    }
}
