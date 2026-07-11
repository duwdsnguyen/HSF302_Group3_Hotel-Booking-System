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
    public Booking createBooking(List<Integer> roomIds, List<Long> serviceIds, LocalDate checkIn, LocalDate checkOut, Integer guests, Long customerId) {
        if (checkIn == null || checkOut == null) {
            throw new IllegalArgumentException("Vui lòng chọn ngày nhận và trả phòng.");
        }
        if (checkIn.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Ngày nhận phòng không thể ở quá khứ.");
        }
        if (!checkOut.isAfter(checkIn)) {
            throw new IllegalArgumentException("Ngày trả phòng phải sau ngày nhận phòng.");
        }
        if (guests == null || guests <= 0) {
            throw new IllegalArgumentException("Số lượng khách phải lớn hơn 0.");
        }
        if (roomIds == null || roomIds.isEmpty()) {
            throw new IllegalArgumentException("Vui lòng chọn ít nhất một phòng.");
        }

        // Validate duplicates by checking availability
        List<Room> availableRooms = roomRepository.findAvailableRooms(
                checkIn, checkOut, 1, RoomStatus.AVAILABLE, List.of("PENDING", "CONFIRMED"));

        List<Integer> availableRoomIds = availableRooms.stream().map(Room::getRoomId).collect(Collectors.toList());
        for (Integer roomId : roomIds) {
            if (!availableRoomIds.contains(roomId)) {
                throw new IllegalArgumentException("Phòng " + roomId + " đã được đặt hoặc không khả dụng. Vui lòng chọn phòng khác.");
            }
        }

        List<Room> selectedRooms = availableRooms.stream()
                .filter(r -> roomIds.contains(r.getRoomId()))
                .collect(Collectors.toList());

        List<HotelService> selectedServices = java.util.Collections.emptyList();
        if (serviceIds != null && !serviceIds.isEmpty()) {
            selectedServices = hotelServiceRepository.findAllById(serviceIds);
        }

        if (selectedRooms.size() > 1) {
            throw new IllegalArgumentException("Hệ thống chỉ hỗ trợ đặt 1 phòng cho mỗi đơn đặt phòng. Vui lòng chọn lại.");
        }
        
        Room selectedRoom = selectedRooms.get(0);
        long days = ChronoUnit.DAYS.between(checkIn, checkOut);
        
        BigDecimal roomsTotal = selectedRoom.getRoomType().getBasePrice().multiply(BigDecimal.valueOf(days));
                
        BigDecimal servicesTotal = selectedServices.stream()
                .map(HotelService::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
                
        BigDecimal totalAmount = roomsTotal.add(servicesTotal);

        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy khách hàng."));

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
