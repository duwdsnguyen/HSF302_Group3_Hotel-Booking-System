package hsf.g3.hotel_booking_system.service.guest;

import hsf.g3.hotel_booking_system.entity.room.Room;
import hsf.g3.hotel_booking_system.enums.user.RoomStatus;
import hsf.g3.hotel_booking_system.repository.admin.RoomRepository;
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
        if (checkInDate == null || checkOutDate == null) {
            throw new IllegalArgumentException("Please select both check-in and check-out dates.");
        }

        if (checkInDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Check-in date cannot be in the past.");
        }

        if (!checkOutDate.isAfter(checkInDate)) {
            throw new IllegalArgumentException("Check-out date must be after check-in date.");
        }

        if (numberOfGuests == null || numberOfGuests <= 0) {
            throw new IllegalArgumentException("Number of guests must be greater than 0.");
        }

        return roomRepository.findAvailableRooms(
                checkInDate,
                checkOutDate,
                numberOfGuests,
                RoomStatus.AVAILABLE,
                List.of("PENDING", "CONFIRMED")
        );
    }

    @Override
    public Room getRoomById(Integer roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found with ID: " + roomId));
    }
}
