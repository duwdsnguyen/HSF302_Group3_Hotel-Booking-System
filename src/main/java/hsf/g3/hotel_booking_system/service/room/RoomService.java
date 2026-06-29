package hsf.g3.hotel_booking_system.service.room;

import hsf.g3.hotel_booking_system.dto.room.RoomRequestDTO;
import hsf.g3.hotel_booking_system.dto.room.RoomResponseDTO;
import hsf.g3.hotel_booking_system.enums.user.RoomStatus;

import java.util.List;

public interface RoomService {
    List<RoomResponseDTO> getAllRooms();

    List<RoomResponseDTO> getRooms(String search, RoomStatus status, Integer roomTypeId, String sort);

    RoomResponseDTO getRoomById(Integer roomId);

    RoomResponseDTO createRoom(RoomRequestDTO request);

    RoomResponseDTO updateRoom(Integer id, RoomRequestDTO request);

    void deleteRoom(Integer roomId);
}
