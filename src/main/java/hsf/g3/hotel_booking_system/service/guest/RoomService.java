package hsf.g3.hotel_booking_system.service.guest;

import hsf.g3.hotel_booking_system.entity.guest.Room;
import java.time.LocalDate;
import java.util.List;

public interface RoomService {
    List<Room> searchAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate, Integer numberOfGuests);
}
