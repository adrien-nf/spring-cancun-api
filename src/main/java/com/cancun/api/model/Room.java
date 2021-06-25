package com.cancun.api.model;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonManagedReference;

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
    private List<Reservation> bookings;

    public long getId() {
		return id;
	}

    public void setId(long id) {
		this.id = id;
	}
    
    public void setBookings(List<Reservation> bookings) {
		this.bookings = bookings;
	}

    public List<Reservation> getBookings() {
		return bookings;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
    
}