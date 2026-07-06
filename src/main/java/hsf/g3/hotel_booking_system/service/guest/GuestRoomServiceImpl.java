package hsf.g3.hotel_booking_system.service.guest;

import hsf.g3.hotel_booking_system.entity.room.Room;
import hsf.g3.hotel_booking_system.enums.room.RoomStatus;
import hsf.g3.hotel_booking_system.repository.admin.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GuestRoomServiceImpl implements GuestRoomService {

    private final RoomRepository roomRepository;
    private final hsf.g3.hotel_booking_system.repository.admin.RoomTypeRepository roomTypeRepository;

    @Override
    public List<Room> searchAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate, Integer numberOfGuests, BigDecimal minPrice, BigDecimal maxPrice, Integer roomTypeId) {
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

        List<Room> availableRooms = roomRepository.findAvailableRooms(
                checkInDate,
                checkOutDate,
                numberOfGuests,
                RoomStatus.AVAILABLE,
                List.of("PENDING", "CONFIRMED")
        );

        if (minPrice != null) {
            availableRooms = availableRooms.stream()
                    .filter(room -> room.getRoomType().getBasePrice().compareTo(minPrice) >= 0)
                    .toList();
        }

        if (maxPrice != null) {
            availableRooms = availableRooms.stream()
                    .filter(room -> room.getRoomType().getBasePrice().compareTo(maxPrice) <= 0)
                    .toList();
        }

        if (roomTypeId != null) {
            availableRooms = availableRooms.stream()
                    .filter(room -> room.getRoomType().getRoomTypeId().equals(roomTypeId))
                    .toList();
        }

        return availableRooms;
    }

    @Override
    public Room getRoomById(Integer roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found with ID: " + roomId));
    }

    @Override
    public List<hsf.g3.hotel_booking_system.entity.room.RoomType> getAllRoomTypes() {
        return roomTypeRepository.findAll();
    }
}
