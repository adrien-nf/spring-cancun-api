package com.cancun.api.model;

import javax.persistence.*;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "reservations")
public class Reservation {
    @Id
    private long id;	
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name="room_id")
    @JsonBackReference
    private Room room;

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
    
}