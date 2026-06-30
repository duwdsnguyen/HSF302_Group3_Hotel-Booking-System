package hsf.g3.hotel_booking_system.service.guest;

import hsf.g3.hotel_booking_system.entity.room.Room;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface GuestRoomService {
    List<Room> searchAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate, Integer numberOfGuests, BigDecimal minPrice, BigDecimal maxPrice, Integer roomTypeId);
    Room getRoomById(Integer roomId);
    List<hsf.g3.hotel_booking_system.entity.room.RoomType> getAllRoomTypes();
}
