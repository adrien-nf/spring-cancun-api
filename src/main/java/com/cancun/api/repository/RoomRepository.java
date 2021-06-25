package com.cancun.api.repository;

import org.springframework.stereotype.Repository;
import com.cancun.api.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {}