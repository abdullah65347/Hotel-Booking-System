package com.main.repo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.main.entity.Room;

public interface RoomRepository extends JpaRepository<Room, Long> {

	@Query("SELECT DISTINCT r.roomType from Room r")
	List<String> findDistinctRoomTypes();

	@Query("SELECT r FROM Room r WHERE r.roomType LIKE %:roomType%  AND r.id NOT IN (SELECT bk.room.id from Booking bk where (bk.checkInDate <= :checkOutDate) AND (bk.checkOutDate >= :checkInDate)) ")
	List<Room> findAvailableRoomsByDateAndTypes(LocalDate checkInDate, LocalDate checkOutDate, String roomType);
	
	@Query("SELECT r FROM Room r where r.id NOT IN (SELECT b.room.id FROM Booking b)")
	List<Room> getAllAvailableRooms();

}
