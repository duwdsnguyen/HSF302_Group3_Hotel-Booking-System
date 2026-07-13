package hsf.g3.hotel_booking_system.service.guest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import hsf.g3.hotel_booking_system.dto.guest.room.request.RoomChangeRequest;
import hsf.g3.hotel_booking_system.dto.guest.room.response.RoomResponse;
import hsf.g3.hotel_booking_system.dto.user.UserInfoDTO;
import hsf.g3.hotel_booking_system.entity.room.Room;

public interface GuestRoomService {
    List<Room> searchAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate, Integer numberOfGuests);
    RoomResponse getAllAvailableRoom(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
    boolean changeRoom(RoomChangeRequest roomChangeRequest, UserInfoDTO userInfoDTO);
    List<Room> searchAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate, Integer numberOfGuests, BigDecimal minPrice, BigDecimal maxPrice, Integer roomTypeId);
    Room getRoomById(Integer roomId);
    List<hsf.g3.hotel_booking_system.entity.room.RoomType> getAllRoomTypes();
}
