package hsf.g3.hotel_booking_system.service.guest;

import hsf.g3.hotel_booking_system.entity.guest.Booking;
import hsf.g3.hotel_booking_system.entity.room.Room;
import hsf.g3.hotel_booking_system.entity.service.HotelService;
import hsf.g3.hotel_booking_system.enums.room.RoomStatus;
import hsf.g3.hotel_booking_system.repository.admin.HotelServiceRepository;
import hsf.g3.hotel_booking_system.repository.admin.RoomRepository;
import hsf.g3.hotel_booking_system.repository.guest.BookingRepository;
import hsf.g3.hotel_booking_system.repository.user.UserRepository;
import hsf.g3.hotel_booking_system.entity.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GuestBookingServiceImpl implements GuestBookingService {

    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;
    private final HotelServiceRepository hotelServiceRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Booking createBooking(Integer roomId, List<Long> serviceIds, LocalDate checkIn, LocalDate checkOut, Integer guests, Long customerId) {
        if (checkIn == null || checkOut == null) {
            throw new IllegalArgumentException("Please select check-in and check-out dates.");
        }
        if (checkIn.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Check-in date cannot be in the past.");
        }
        if (!checkOut.isAfter(checkIn)) {
            throw new IllegalArgumentException("Check-out date must be after check-in date.");
        }
        if (guests == null || guests <= 0) {
            throw new IllegalArgumentException("Number of guests must be greater than 0.");
        }
        if (roomId == null) {
            throw new IllegalArgumentException("Please select a room.");
        }

        // Validate duplicates by checking availability
        List<Room> availableRooms = roomRepository.findAvailableRooms(
                checkIn, checkOut, 1, RoomStatus.AVAILABLE, List.of("PENDING", "CONFIRMED"));

        List<Integer> availableRoomIds = availableRooms.stream().map(Room::getRoomId).collect(Collectors.toList());
        if (!availableRoomIds.contains(roomId)) {
            throw new IllegalArgumentException("Room " + roomId + " is already booked or currently unavailable. Please select another room.");
        }

        Room selectedRoom = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found."));

        List<HotelService> selectedServices = java.util.Collections.emptyList();
        if (serviceIds != null && !serviceIds.isEmpty()) {
            selectedServices = hotelServiceRepository.findAllById(serviceIds);
        }
        long days = ChronoUnit.DAYS.between(checkIn, checkOut);
        
        BigDecimal roomsTotal = selectedRoom.getRoomType().getBasePrice().multiply(BigDecimal.valueOf(days));
                
        BigDecimal servicesTotal = selectedServices.stream()
                .map(HotelService::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
                
        BigDecimal totalAmount = roomsTotal.add(servicesTotal);

        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found."));

        Booking booking = new Booking();
        booking.setRoom(selectedRoom);

        List<hsf.g3.hotel_booking_system.entity.guest.BookingService> bookingServices = selectedServices.stream().map(service -> {
            hsf.g3.hotel_booking_system.entity.guest.BookingService bs = new hsf.g3.hotel_booking_system.entity.guest.BookingService();
            bs.setBooking(booking);
            bs.setService(service);
            bs.setQuantity(1);
            bs.setPrice(service.getPrice());
            return bs;
        }).collect(Collectors.toList());

        booking.setBookingServices(bookingServices);
        booking.setCustomer(customer);
        booking.setCheckInDate(checkIn);
        booking.setCheckOutDate(checkOut);
        booking.setNumberOfGuests(guests);
        booking.setTotalAmount(totalAmount);
        booking.setStatus("PENDING");

        return bookingRepository.save(booking);
    }
}
