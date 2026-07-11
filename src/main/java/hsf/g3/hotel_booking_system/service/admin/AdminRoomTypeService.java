package hsf.g3.hotel_booking_system.service.admin;

import hsf.g3.hotel_booking_system.dto.admin.RoomTypeRequestDTO;
import hsf.g3.hotel_booking_system.dto.admin.RoomTypeResponseDTO;
import hsf.g3.hotel_booking_system.enums.room.RoomTypeStatus;

import java.util.List;

public interface AdminRoomTypeService {
    List<RoomTypeResponseDTO> getAllRoomTypes();

    List<RoomTypeResponseDTO> getRoomTypes(String search, RoomTypeStatus status, String sort);

    RoomTypeResponseDTO getRoomTypeById(Integer roomTypeId);

    RoomTypeResponseDTO createRoomType(RoomTypeRequestDTO request);

    RoomTypeResponseDTO updateRoomType(Integer id, RoomTypeRequestDTO request);

    void deleteRoomType(int roomTypeId);
}
