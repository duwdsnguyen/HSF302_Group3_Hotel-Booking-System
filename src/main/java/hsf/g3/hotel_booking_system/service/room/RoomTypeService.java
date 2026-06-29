package hsf.g3.hotel_booking_system.service.room;


import hsf.g3.hotel_booking_system.dto.room.RoomTypeRequestDTO;
import hsf.g3.hotel_booking_system.dto.room.RoomTypeResponseDTO;
import hsf.g3.hotel_booking_system.enums.user.RoomTypeStatus;

import java.util.List;

public interface RoomTypeService {
    List<RoomTypeResponseDTO> getAllRoomTypes();

    List<RoomTypeResponseDTO> getRoomTypes(String search, RoomTypeStatus status, String sort);

    RoomTypeResponseDTO getRoomTypeById(Integer roomTypeId);

    RoomTypeResponseDTO createRoomType(RoomTypeRequestDTO request);

    RoomTypeResponseDTO updateRoomType(Integer id, RoomTypeRequestDTO request);

    void deleteRoomType(int roomTypeId);
}
