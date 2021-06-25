package com.cancun.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cancun.api.model.Room;
import com.cancun.api.repository.RoomRepository;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {
	@Autowired
    private RoomRepository roomRepository;
        
    @GetMapping
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Room> findRoomById(@PathVariable(value = "id") long id) {
        Optional<Room> room = roomRepository.findById(id);
        
        return room.isPresent()
        		? ResponseEntity.ok().body(room.get())
        		: ResponseEntity.notFound().build();
    }
}
