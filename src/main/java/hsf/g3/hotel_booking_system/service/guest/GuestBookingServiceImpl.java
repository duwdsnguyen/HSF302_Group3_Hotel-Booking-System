package hsf.g3.hotel_booking_system.service.guest;

import hsf.g3.hotel_booking_system.dto.guest.BookingDTO;
import hsf.g3.hotel_booking_system.dto.guest.BookingHistoryDTO;
import hsf.g3.hotel_booking_system.dto.guest.request.BookingRequestDTO;
import hsf.g3.hotel_booking_system.entity.guest.Booking;
import hsf.g3.hotel_booking_system.entity.guest.BookingHistory;
import hsf.g3.hotel_booking_system.entity.room.Room;
import hsf.g3.hotel_booking_system.entity.room.RoomTypeImage;
import hsf.g3.hotel_booking_system.entity.service.HotelService;
import hsf.g3.hotel_booking_system.entity.user.User;
import hsf.g3.hotel_booking_system.enums.room.BookingAction;
import hsf.g3.hotel_booking_system.enums.room.BookingStatus;
import hsf.g3.hotel_booking_system.enums.room.RoomStatus;
import hsf.g3.hotel_booking_system.repository.admin.HotelServiceRepository;
import hsf.g3.hotel_booking_system.repository.admin.RoomRepository;
import hsf.g3.hotel_booking_system.repository.guest.BookingHistoryRepository;
import hsf.g3.hotel_booking_system.repository.guest.BookingRepository;
import hsf.g3.hotel_booking_system.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GuestBookingServiceImpl implements GuestBookingService {

    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;
    private final HotelServiceRepository hotelServiceRepository;
    private final UserRepository userRepository;
    private final BookingHistoryRepository bookingHistoryRepository;

    // ── Create ─────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public BookingDTO createBooking(BookingRequestDTO request, Long customerId) {
        LocalDate checkIn = request.getCheckInDate();
        LocalDate checkOut = request.getCheckOutDate();
        Integer guests = request.getNumberOfGuests();
        Integer roomId = request.getRoomId();
        List<Long> serviceIds = request.getServiceIds();

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

        List<Room> availableRooms = roomRepository.findAvailableRooms(
                checkIn, checkOut, 1, RoomStatus.AVAILABLE, List.of("PENDING", "CONFIRMED"));

        List<Integer> availableRoomIds = availableRooms.stream()
                .map(Room::getRoomId).collect(Collectors.toList());
        if (!availableRoomIds.contains(roomId)) {
            throw new IllegalArgumentException(
                    "Room " + roomId + " is already booked or currently unavailable. Please select another room.");
        }

        Room selectedRoom = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found."));

        List<HotelService> selectedServices = Collections.emptyList();
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
        booking.setCustomer(customer);
        booking.setCheckInDate(checkIn);
        booking.setCheckOutDate(checkOut);
        booking.setNumberOfGuests(guests);
        booking.setTotalAmount(totalAmount);
        booking.setStatus(BookingStatus.PENDING);

        List<hsf.g3.hotel_booking_system.entity.guest.BookingService> bookingServices = selectedServices.stream()
                .map(service -> {
                    hsf.g3.hotel_booking_system.entity.guest.BookingService bs =
                            new hsf.g3.hotel_booking_system.entity.guest.BookingService();
                    bs.setBooking(booking);
                    bs.setService(service);
                    bs.setQuantity(1);
                    bs.setPrice(service.getPrice());
                    return bs;
                }).collect(Collectors.toList());
        booking.setBookingServices(bookingServices);

        Booking savedBooking = bookingRepository.save(booking);

        BookingDTO responseDTO = new BookingDTO();
        responseDTO.setId(savedBooking.getId());
        responseDTO.setCustomerId(customer.getUserId().intValue());
        responseDTO.setRoomId(selectedRoom.getRoomId());
        responseDTO.setCheckInDate(savedBooking.getCheckInDate());
        responseDTO.setCheckOutDate(savedBooking.getCheckOutDate());
        responseDTO.setActualCheckIn(savedBooking.getActualCheckIn());
        responseDTO.setActualCheckOut(savedBooking.getActualCheckOut());
        responseDTO.setNumberOfGuests(savedBooking.getNumberOfGuests());
        responseDTO.setTotalAmount(savedBooking.getTotalAmount());
        responseDTO.setStatus(savedBooking.getStatus());
        return responseDTO;
    }

    // ── History ────────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public List<BookingHistoryDTO> getBookingHistory(Long customerId, BookingStatus status, String keyword) {
        String kw = (keyword != null && !keyword.isBlank()) ? keyword.trim() : null;
        List<Booking> bookings = bookingRepository.searchByCustomer(customerId, status, kw);
        return bookings.stream().map(this::toHistoryDTO).collect(Collectors.toList());
    }

    // ── Detail ─────────────────────────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public BookingHistoryDTO getBookingDetail(Integer bookingId, Long customerId) {
        Booking booking = bookingRepository.findByIdAndCustomerId(bookingId, customerId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found."));
        return toHistoryDTO(booking);
    }

    // ── Cancel ─────────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public void cancelBooking(Integer bookingId, Long customerId) {
        Booking booking = bookingRepository.findByIdAndCustomerId(bookingId, customerId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found."));

        BookingStatus currentStatus = booking.getStatus();
        if (currentStatus != BookingStatus.PENDING && currentStatus != BookingStatus.CONFIRMED) {
            throw new IllegalArgumentException(
                    "Cannot cancel this booking. Only bookings with status PENDING or CONFIRMED can be cancelled.");
        }

        User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found."));

        // Record history
        BookingHistory history = BookingHistory.builder()
                .booking(booking)
                .changedBy(customer)
                .oldStatus(currentStatus)
                .newStatus(BookingStatus.CANCELLED)
                .action(BookingAction.CANCEL_BOOKING)
                .description("Cancelled by guest")
                .changedAt(LocalDateTime.now())
                .build();
        bookingHistoryRepository.save(history);

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }

    // ── Mapping helper ─────────────────────────────────────────────────────────

    private BookingHistoryDTO toHistoryDTO(Booking b) {
        BookingHistoryDTO dto = new BookingHistoryDTO();
        dto.setId(b.getId());
        dto.setCheckInDate(b.getCheckInDate());
        dto.setCheckOutDate(b.getCheckOutDate());
        dto.setActualCheckIn(b.getActualCheckIn());
        dto.setActualCheckOut(b.getActualCheckOut());
        dto.setNumberOfGuests(b.getNumberOfGuests());
        dto.setTotalAmount(b.getTotalAmount());
        dto.setStatus(b.getStatus());
        dto.setCreatedAt(b.getCreatedAt());

        if (b.getRoom() != null) {
            dto.setRoomNumber(b.getRoom().getRoomNumber());
            dto.setFloorNumber(b.getRoom().getFloorNumber());
            if (b.getRoom().getRoomType() != null) {
                dto.setRoomTypeName(b.getRoom().getRoomType().getTypeName());
                dto.setRoomImageUrls(b.getRoom().getRoomType().getImages().stream()
                        .map(RoomTypeImage::getImageUrl)
                        .collect(Collectors.toList()));
            }
        }

        if (b.getBookingServices() != null) {
            List<BookingHistoryDTO.BookingServiceDTO> services = b.getBookingServices().stream()
                    .map(bs -> new BookingHistoryDTO.BookingServiceDTO(
                            bs.getService() != null ? bs.getService().getServiceName() : "Unknown",
                            bs.getPrice(),
                            bs.getQuantity()))
                    .collect(Collectors.toList());
            dto.setServices(services);
        }

        return dto;
    }
}