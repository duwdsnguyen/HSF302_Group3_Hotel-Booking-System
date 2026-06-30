package hsf.g3.hotel_booking_system.service.admin;

import hsf.g3.hotel_booking_system.dto.admin.RoomRequestDTO;
import hsf.g3.hotel_booking_system.dto.admin.RoomResponseDTO;
import hsf.g3.hotel_booking_system.enums.user.RoomStatus;

import java.util.List;

public interface AdminRoomService {
    List<RoomResponseDTO> getAllRooms();

    List<RoomResponseDTO> getRooms(String search, RoomStatus status, Integer roomTypeId, String sort);

    RoomResponseDTO getRoomById(Integer roomId);

    RoomResponseDTO createRoom(RoomRequestDTO request);

    RoomResponseDTO updateRoom(Integer id, RoomRequestDTO request);

    void deleteRoom(Integer roomId);
}
