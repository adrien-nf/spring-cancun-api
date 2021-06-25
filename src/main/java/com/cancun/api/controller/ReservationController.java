package com.cancun.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.cancun.api.model.Reservation;
import com.cancun.api.repository.ReservationRepository;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {
	@Autowired
    private ReservationRepository bookingRepository;

	@PostMapping
    public Reservation store(@Validated @RequestBody Reservation booking) {
        return bookingRepository.save(booking);
    }
}
