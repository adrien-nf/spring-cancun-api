package com.cancun.api.repository;

import org.springframework.stereotype.Repository;

import com.cancun.api.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {}