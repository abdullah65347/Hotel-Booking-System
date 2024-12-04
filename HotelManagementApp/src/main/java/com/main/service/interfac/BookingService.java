package com.main.service.interfac;

import com.main.DTO.Response;
import com.main.entity.Booking;

public interface BookingService {

	Response saveBooking(Long roomId, Long userId, Booking bookingRequest);
	
	Response findBookingByConfirmationCode(String confirmationCode);
	
	Response getAllBookings();
	
	Response cancelBooking(Long bookingId);
}
