package com.main.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.main.DTO.BookingDTO;
import com.main.DTO.Response;
import com.main.entity.Booking;
import com.main.entity.Room;
import com.main.entity.User;
import com.main.exception.OurException;
import com.main.repo.BookingRepository;
import com.main.repo.RoomRepository;
import com.main.repo.UserRepository;
import com.main.service.interfac.BookingService;
import com.main.utils.Utils;

@Service
public class BookingServiceImpl implements BookingService {
	@Autowired
	private BookingRepository bookingRepository;
	@Autowired
	private RoomRepository roomRepository;
	@Autowired
	private UserRepository userRepository;

	@Override
	public Response saveBooking(Long roomId, Long userId, Booking bookingRequest) {
		Response response = new Response();
		try {
			if (bookingRequest.getCheckOutDate().isBefore(bookingRequest.getCheckInDate())) {
				throw new IllegalArgumentException("Check in date must come after check out date");
			}
			Room room = roomRepository.findById(roomId).orElseThrow(() -> new OurException("Room not found"));
			User user = userRepository.findById(userId).orElseThrow(() -> new OurException("User not found"));

			List<Booking> existingBookings = room.getBookings();
			if (!roomIsAvailable(bookingRequest, existingBookings)) {
				throw new OurException("Room not Available for selected date range");
			}
			
			bookingRequest.setRoom(room);
			bookingRequest.setUser(user);
			String bookingConfirmationCode = Utils.generateRandomConfirmationCode(10);
			bookingRequest.setBookingConfirmationCode(bookingConfirmationCode);
			bookingRepository.save(bookingRequest);
			response.setStatusCode(200);
			response.setMessage("Successful");
			response.setBookingConfirmationCode(bookingConfirmationCode);
		} catch (OurException e) {
			response.setStatusCode(404);
			response.setMessage(e.getMessage());
		} catch (Exception e) {
			response.setStatusCode(500);
			response.setMessage("Error saving a room");
		}
		return response;
	}

	@Override
	public Response findBookingByConfirmationCode(String confirmationCode) {
		Response response = new Response();
		try {
			Booking booking = bookingRepository.findByBookingConfirmationCode(confirmationCode).orElseThrow(() -> new OurException("Booking Not Found"));
            BookingDTO bookingDTO = Utils.mapBookingEntityToBookingDTOPlusBookedRooms(booking, true);
			response.setStatusCode(200);
			response.setBooking(bookingDTO);
			response.setMessage("Successful");
		} catch (OurException e) {
			response.setStatusCode(404);
			response.setMessage(e.getMessage());
		} catch (Exception e) {
			response.setStatusCode(500);
			response.setMessage("Error finding a room");
		}
		return response;
	}

	@Override
	public Response getAllBookings() {
		Response response = new Response();
		try {
			List<Booking> bookingList = bookingRepository.findAll(Sort.by(Sort.Direction.DESC,"id"));
			List<BookingDTO> bookingDTOList = Utils.mapBookingListEntityToBookingListDTO(bookingList);
			response.setStatusCode(200);
			response.setBookingList(bookingDTOList);
			response.setMessage("Successful");
		} catch (OurException e) {
			response.setStatusCode(404);
			response.setMessage(e.getMessage());
		} catch (Exception e) {
			response.setStatusCode(500);
			response.setMessage("Error getting all bookings");
		}
		return response;
	}

	@Override
	public Response cancelBooking(Long bookingId) {
		Response response = new Response();
		try {
			bookingRepository.findById(bookingId).orElseThrow(()->new OurException("Booking not found"));
			bookingRepository.deleteById(bookingId);
			response.setStatusCode(200);
			response.setMessage("Successful");
		} catch (OurException e) {
			response.setStatusCode(404);
			response.setMessage(e.getMessage());
		} catch (Exception e) {
			response.setStatusCode(500);
			response.setMessage("Error cancelling a booking");
		}
		return response;
	}

	private boolean roomIsAvailable(Booking bookingRequest, List<Booking> existingBookings) {
		return existingBookings.stream()
				.noneMatch(existingBooking -> bookingRequest.getCheckInDate().equals(existingBooking.getCheckInDate())
						|| bookingRequest.getCheckOutDate().isBefore(existingBooking.getCheckOutDate())
						|| (bookingRequest.getCheckInDate().isAfter(existingBooking.getCheckInDate())
								&& bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckOutDate()))
						|| (bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate())

								&& bookingRequest.getCheckOutDate().equals(existingBooking.getCheckOutDate()))
						|| (bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate())

								&& bookingRequest.getCheckOutDate().isAfter(existingBooking.getCheckOutDate()))

						|| (bookingRequest.getCheckInDate().equals(existingBooking.getCheckOutDate())
								&& bookingRequest.getCheckOutDate().equals(existingBooking.getCheckInDate()))

						|| (bookingRequest.getCheckInDate().equals(existingBooking.getCheckOutDate())
								&& bookingRequest.getCheckOutDate().equals(bookingRequest.getCheckInDate())));

	}
}
