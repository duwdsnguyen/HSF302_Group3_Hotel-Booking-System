package hsf.g3.hotel_booking_system.service.guest;

import hsf.g3.hotel_booking_system.dto.guest.room.request.RoomChangeRequest;
import hsf.g3.hotel_booking_system.dto.guest.room.response.RoomResponse;
import hsf.g3.hotel_booking_system.entity.room.Room;

import java.time.LocalDate;
import java.util.List;

public interface GuestRoomService {
    List<Room> searchAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate, Integer numberOfGuests);
    RoomResponse getAllAvailableRoom(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
    boolean changeRoom(RoomChangeRequest roomChangeRequest);
}
