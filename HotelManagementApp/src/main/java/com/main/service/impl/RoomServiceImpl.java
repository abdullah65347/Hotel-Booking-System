package com.main.service.impl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.main.DTO.Response;
import com.main.DTO.RoomDTO;
import com.main.entity.Room;
import com.main.exception.OurException;
import com.main.repo.RoomRepository;
import com.main.service.GoogleCloudStorage;
import com.main.service.interfac.RoomService;
import com.main.utils.Utils;

@Service
public class RoomServiceImpl implements RoomService {
	@Autowired
	private RoomRepository roomRepository;
	@Autowired
	private GoogleCloudStorage googleCloudStorage;

	@Override
	public Response addNewRoom(MultipartFile photo, String roomType, BigDecimal roomPrice, String description) {
		Response response = new Response();
		try {
			String imageUrl = googleCloudStorage.saveImageToS3(photo);
			Room room = new Room();
			room.setRoomPhotoUrl(imageUrl);
			room.setRoomType(roomType);
			room.setRoomPrice(roomPrice);
			room.setRoomDescription(description);
			Room savedRoom = roomRepository.save(room);
			RoomDTO roomDTO = Utils.mapRoomEntityToRoomDTO(savedRoom);
			response.setStatusCode(200);
			response.setMessage("Successful");
			response.setRoom(roomDTO);
		} catch (Exception e) {
			response.setStatusCode(500);
			response.setMessage("Error saving a room");
		}
		return response;
	}

	@Override
	public List<String> getAllRoomTypes() {
		return roomRepository.findDistinctRoomTypes();
	}

	@Override
	public Response getAllRooms() {
		Response response = new Response();
		try {

			List<Room> roomList = roomRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
			List<RoomDTO> roomDTOList = Utils.mapRoomListEntityToRoomListDTO(roomList);

			response.setStatusCode(200);
			response.setMessage("Successful");
			response.setRoomList(roomDTOList);
		} catch (Exception e) {
			response.setStatusCode(500);
			response.setMessage("Error getting rooms");
		}
		return response;
	}

	@Override
	public Response deleteRoom(Long roomId) {
		Response response = new Response();
		try {
			roomRepository.findById(roomId).orElseThrow(() -> new OurException("Room Not Found"));
			roomRepository.deleteById(roomId);

			response.setStatusCode(200);
			response.setMessage("Successful");
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
	public Response updateRoom(Long roomId, String roomType, BigDecimal roomPrice, MultipartFile photo,String roomDescription) {
		Response response = new Response();
		try {
            String imageUrl = null;
            if (photo != null && !photo.isEmpty()) {
                imageUrl = googleCloudStorage.saveImageToS3(photo);
            }
            Room room = roomRepository.findById(roomId).orElseThrow(() -> new OurException("Room Not Found"));
            if (roomType != null) room.setRoomType(roomType);
            if (roomPrice != null) room.setRoomPrice(roomPrice);
            if (roomDescription != null) room.setRoomDescription(roomDescription);
            if (imageUrl != null) room.setRoomPhotoUrl(imageUrl);

            Room updatedRoom = roomRepository.save(room);
            RoomDTO roomDTO = Utils.mapRoomEntityToRoomDTO(updatedRoom);

			response.setStatusCode(200);
			response.setMessage("Successful");
			response.setRoom(roomDTO);
		} catch (Exception e) {
			response.setStatusCode(500);
			response.setMessage("Error saving a room");
		}
		return response;
	}

	@Override
	public Response getRoomById(Long roomId) {
		Response response = new Response();
		try {
            Room room = roomRepository.findById(roomId).orElseThrow(() -> new OurException("Room Not Found"));
            RoomDTO roomDTO = Utils.mapRoomEntityToRoomDTOPlusBookings(room);
            response.setStatusCode(200);
            response.setMessage("successful");
            response.setRoom(roomDTO);

        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error saving a room " + e.getMessage());
        }
        return response;
	}

	@Override
	public Response getAvailableRoomsByDateAndType(LocalDate checkInDate, LocalDate checkOutDate, String roomType) {
		Response response = new Response();
		try {
			List<Room> availableRooms = roomRepository.findAvailableRoomsByDateAndTypes(checkInDate, checkOutDate,
					roomType);
			List<RoomDTO> roomDTOList = Utils.mapRoomListEntityToRoomListDTO(availableRooms);

			response.setStatusCode(200);
			response.setMessage("Successful");
			response.setRoomList(roomDTOList);
		} catch (Exception e) {
			response.setStatusCode(500);
			response.setMessage("Error saving a room");
		}
		return response;
	}

	@Override
	public Response getAllAvailableRooms() {
		Response response = new Response();
		try {
			List<Room> roomList = roomRepository.getAllAvailableRooms();
			List<RoomDTO> roomDTOList = Utils.mapRoomListEntityToRoomListDTO(roomList);

			response.setStatusCode(200);
			response.setMessage("Successful");
			response.setRoomList(roomDTOList);
		} catch (OurException e) {
			response.setStatusCode(404);
			response.setMessage(e.getMessage());
		} catch (Exception e) {
			response.setStatusCode(500);
			response.setMessage("Error while getting rooms");
		}
		return response;
	}

}