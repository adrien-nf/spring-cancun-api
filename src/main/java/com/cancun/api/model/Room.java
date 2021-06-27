package com.cancun.api.model;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rooms")
public class Room {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	@Column
	private String name;
	@OneToMany(mappedBy = "room")
	@JsonManagedReference
	private List<Reservation> reservations;
	
	public Room() {
		setReservations((new ArrayList<Reservation>()));
	}
	
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
	
	public Room book(Reservation r) {
		this.reservations.add(r);
		return this;
	}
	
	public Room unbook(Reservation r) {
		int index = reservations.indexOf(r);
		if(index != -1)			
			this.reservations.remove(index);
		return this;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj != null && this.getId() == ((Room) obj).getId();
	}
}