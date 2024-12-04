package com.main.service.interfac;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import com.main.DTO.Response;

public interface RoomService {

	Response addNewRoom(MultipartFile photo, String roomType, BigDecimal roomPrice, String description);

	List<String> getAllRoomTypes();

	Response getAllRooms();

	Response deleteRoom(Long roomId);

	Response updateRoom(Long roomId, String roomType, BigDecimal roomPrice, MultipartFile photo, String roomDescription);

	Response getRoomById(Long roomId);

	Response getAvailableRoomsByDateAndType(LocalDate checkInDate, LocalDate checkOutDate, String roomType);

	Response getAllAvailableRooms();

}
