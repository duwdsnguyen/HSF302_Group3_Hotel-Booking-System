package hsf.g3.hotel_booking_system.service.guest;

import hsf.g3.hotel_booking_system.entity.guest.Room;
import hsf.g3.hotel_booking_system.repository.guest.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {
    private final RoomRepository roomRepository;

    @Override
    public List<Room> searchAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate, Integer numberOfGuests) {
        if(checkInDate == null || checkOutDate == null) {
            throw new IllegalArgumentException("Please select both check-in and check-out dates.");
        }
        if (checkInDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Check-in date cannot be in the past.");
        }
        if(checkInDate.isAfter(checkOutDate)) {
            throw new IllegalArgumentException("Check-out date must be after check-in date.");
        }
        if (numberOfGuests == null || numberOfGuests <= 0) {
            throw new IllegalArgumentException("Number of guests must be greater than 0.");
        }
        return roomRepository.findAvailableRooms(checkInDate, checkOutDate, numberOfGuests);
    }
}
