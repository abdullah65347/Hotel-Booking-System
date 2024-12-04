package com.main.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.main.entity.Booking;

public interface BookingRepository extends JpaRepository<Booking, Long> {
	
	List<Booking> findByRoomId(Long roomId);
	
	Optional<Booking> findByBookingConfirmationCode(String confirmationCode);
	
	List<Booking> findByUserId(Long userId);

}
