package hsf.g3.hotel_booking_system.service.guest;

import hsf.g3.hotel_booking_system.entity.room.Room;
import hsf.g3.hotel_booking_system.enums.user.RoomStatus;
import hsf.g3.hotel_booking_system.repository.room.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GuestRoomServiceImpl implements GuestRoomService {

    private final RoomRepository roomRepository;

    @Override
    public List<Room> searchAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate, Integer numberOfGuests) {
        if (checkInDate.isAfter(checkOutDate) || checkInDate.isEqual(checkOutDate)) {
            throw new IllegalArgumentException("Check-in date must be before check-out date.");
        }
        if (numberOfGuests <= 0) {
            throw new IllegalArgumentException("Number of guests must be greater than 0.");
        }

        List<String> activeBookingStatuses = List.of("PENDING", "CONFIRMED", "CHECKED_IN");
        return roomRepository.findAvailableRooms(
                checkInDate,
                checkOutDate,
                numberOfGuests,
                RoomStatus.AVAILABLE,
                activeBookingStatuses
        );
    }

    @Override
    public Room getRoomById(Integer roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found with ID: " + roomId));
    }
}
