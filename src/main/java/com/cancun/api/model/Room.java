package com.cancun.api.model;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "rooms")
public class Room {
    @Id
    private long id;
    @Column
    private String name;
    @OneToMany(mappedBy = "room")
    @JsonManagedReference
    private List<Reservation> reservations;

    public long getId() {
		return id;
	}

    public void setId(long id) {
		this.id = id;
	}
    
    public void setReservations(List<Reservation> reservations) {
		this.reservations = reservations;
	}

    public List<Reservation> getReservations() {
		return reservations;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public boolean isAvailableForReservation(LocalDate startDate, LocalDate endDate) {
		return reservations.stream().filter(r -> r.isOverlappingDates(startDate, endDate)).count() == 0;
	}
	
	public Room addReservation(Reservation r) {
		this.reservations.add(r);
		return this;
	}
}