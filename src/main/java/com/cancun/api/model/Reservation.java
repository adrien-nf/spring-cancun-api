package com.cancun.api.model;

import java.time.LocalDate;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sun.istack.NotNull;

@Entity
@Table(name = "reservations")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "room_id", nullable = false)
    @JsonBackReference
    @NotNull
    private Room room;
    
    @Column(nullable = false)
    private LocalDate startDate;
    
    @Column(nullable = false)
    private LocalDate endDate;
	
    public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public Room getRoom() {
		return room;
	}
	
	public void setRoom(Room room) {
		this.room = room;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}
	
	public boolean isOverlappingDates(LocalDate startDate, LocalDate endDate) {
		return !(this.endDate.isBefore(startDate) || this.startDate.isAfter(endDate));
	}
	
	public boolean belongsToRoom(Room room) {
		return room.equals(this.getRoom());
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj != null && this.getId() == ((Reservation) obj).getId();
	}
}