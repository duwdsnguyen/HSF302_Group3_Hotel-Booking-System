package hsf.g3.hotel_booking_system.service.receptionist;

import hsf.g3.hotel_booking_system.dto.receptionist.BookingDetailDTO;
import hsf.g3.hotel_booking_system.entity.guest.Booking;
import hsf.g3.hotel_booking_system.entity.guest.BookingHistory;
import hsf.g3.hotel_booking_system.entity.room.Room;
import hsf.g3.hotel_booking_system.entity.user.User;
import hsf.g3.hotel_booking_system.enums.room.BookingAction;
import hsf.g3.hotel_booking_system.enums.room.BookingStatus;
import hsf.g3.hotel_booking_system.enums.room.RoomStatus;
import hsf.g3.hotel_booking_system.exception.AppException;
import hsf.g3.hotel_booking_system.exception.ErrorCode;
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
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService{
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final BookingHistoryRepository bookingHistoryRepository;

    @Override
    public List<Booking> searchBookings(String status, String customerName, BigDecimal minPrice, BigDecimal maxPrice) {
        BookingStatus bookingStatus = (status == null || status.equals("ALL") ? null : BookingStatus.valueOf(status));
        if (customerName != null && !customerName.trim().isEmpty()) {
            customerName = "%" + customerName + "%";
        } else {
            customerName = null;
        }
        return bookingRepository.searchBooking(bookingStatus, customerName, minPrice, maxPrice);
    }

    @Override
    public BookingDetailDTO getBookingDetailsById(int bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Đơn booking " + bookingId + " không tồn tại"));
        return toDto(booking);
    }

    @Override
    public void confirmBooking(int bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Đơn booking " + bookingId + " không tồn tại"));
        if(booking.getStatus() != BookingStatus.PENDING) {
            throw new RuntimeException("Chỉ duyệt đơn với trạng thái 'PENDING'");
        }
        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);
    }

    @Override
    public void cancelBooking(int bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Đơn booking " + bookingId + " không tồn tại"));
        if(booking.getStatus() != BookingStatus.PENDING) {
            throw new RuntimeException("Chỉ duyệt đơn với trạng thái 'PENDING'");
        }
        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }

    @Override
    @Transactional(noRollbackFor = IllegalStateException.class)
    public void checkIn(int bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Đơn booking " + bookingId + " không tồn tại"));
        if(booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new RuntimeException("Chỉ duyệt đơn với trạng thái 'CONFIRMED'");
        }

        LocalDate today = LocalDate.now();
        
        if(today.isBefore(booking.getCheckInDate())) {
            throw new RuntimeException("Chưa đến ngày check-in. Khách không thể check-in sớm hơn ngày đã đặt!");
        }
        
        LocalDate expectedCheckIn = booking.getCheckInDate().plusDays(1);
        if(today.isAfter(expectedCheckIn)) {
            booking.setStatus(BookingStatus.CANCELLED);
            bookingRepository.saveAndFlush(booking);
            throw new IllegalStateException("Đơn đã tự động bị hủy do quá hạn check-in");
        }

        booking.setStatus(BookingStatus.CHECKED_IN);
        booking.setActualCheckIn(LocalDateTime.now());

        Room room = booking.getRoom();
        room.setStatus(RoomStatus.OCCUPIED);

        roomRepository.save(room);
        bookingRepository.save(booking);
    }

    @Override
    public void checkOut(int bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Đơn booking " + bookingId + " không tồn tại"));
        if(booking.getStatus() != BookingStatus.CHECKED_IN) {
            throw new RuntimeException("Chỉ duyệt đơn với trạng thái 'CHECKED_IN'");
        }
        LocalDate today = LocalDate.now();
        
        if(today.isAfter(booking.getCheckOutDate())) {
            long lateDays = ChronoUnit.DAYS.between(booking.getCheckOutDate(), today);
            BigDecimal roomPrice = booking.getRoom().getRoomType().getBasePrice();
            BigDecimal additionalFee = roomPrice.multiply(BigDecimal.valueOf(lateDays));

            booking.setTotalAmount(booking.getTotalAmount().add(additionalFee));
        }
        booking.setStatus(BookingStatus.CHECKED_OUT);
        booking.setActualCheckOut(LocalDateTime.now());

        Room room = booking.getRoom();
        room.setStatus(RoomStatus.AVAILABLE);

        roomRepository.save(room);
        bookingRepository.save(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingHistory getPendingRoomChange(int bookingId) {
        List<BookingHistory> requests = bookingHistoryRepository.findRoomChanges(
                bookingId,
                BookingAction.CHANGE_ROOM,
                BookingStatus.ROOM_CHANGE_PENDING);
        return requests.isEmpty() ? null : requests.getFirst();
    }

    @Override
    public void approveRoomChange(int bookingId, Long receptionistId) {
        User receptionist = getReceptionist(receptionistId);
        Booking booking = getPendingRoomChangeBooking(bookingId);
        BookingHistory request = getPendingRoomChangeForUpdate(bookingId);

        if (booking.getActualCheckIn() == null || booking.getActualCheckOut() != null) {
            throw new AppException(ErrorCode.ROOM_CHANGE_BOOKING_NOT_CHECKED_IN);
        }

        Integer oldRoomId = request.getOldRoom().getRoomId();
        Integer newRoomId = request.getNewRoom().getRoomId();
        if (!booking.getRoom().getRoomId().equals(oldRoomId)) {
            throw new AppException(ErrorCode.ROOM_CHANGE_ASSIGNMENT_CHANGED);
        }

        List<Room> lockedRooms = roomRepository.findRoomsByIdsForUpdate(
                Set.of(oldRoomId, newRoomId));
        if (lockedRooms.size() != 2) {
            throw new AppException(ErrorCode.ROOM_CHANGE_ROOM_NOT_FOUND);
        }

        Room oldRoom = findRoom(lockedRooms, oldRoomId);
        Room newRoom = findRoom(lockedRooms, newRoomId);
        validateTargetRoom(booking, newRoom);

        oldRoom.setStatus(RoomStatus.AVAILABLE);
        newRoom.setStatus(RoomStatus.OCCUPIED);
        booking.setRoom(newRoom);
        booking.setStatus(BookingStatus.CHECKED_IN);

        request.setChangedBy(receptionist);
        request.setNewStatus(BookingStatus.ROOM_CHANGE_APPROVED);
        request.setDescription("Receptionist approved room change from room "
                + oldRoom.getRoomNumber() + " to room " + newRoom.getRoomNumber() + ".");

        roomRepository.save(oldRoom);
        roomRepository.save(newRoom);
        bookingRepository.save(booking);
        bookingHistoryRepository.save(request);
    }

    @Override
    public void rejectRoomChange(int bookingId, Long receptionistId, String reason) {
        User receptionist = getReceptionist(receptionistId);
        Booking booking = getPendingRoomChangeBooking(bookingId);
        BookingHistory request = getPendingRoomChangeForUpdate(bookingId);
        String normalizedReason = reason == null ? "" : reason.trim();
        if (normalizedReason.length() > 500) {
            throw new AppException(ErrorCode.ROOM_CHANGE_REJECTION_REASON_TOO_LONG);
        }

        booking.setStatus(BookingStatus.CHECKED_IN);
        request.setChangedBy(receptionist);
        request.setNewStatus(BookingStatus.ROOM_CHANGE_REJECTED);
        request.setDescription("Receptionist rejected room change from room "
                + request.getOldRoom().getRoomNumber() + " to room "
                + request.getNewRoom().getRoomNumber()
                + (normalizedReason.isEmpty() ? "." : ". Reason: " + normalizedReason));

        bookingRepository.save(booking);
        bookingHistoryRepository.save(request);
    }

    private Booking getPendingRoomChangeBooking(int bookingId) {
        Booking booking = bookingRepository.findByIdForUpdate(bookingId)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_CHANGE_BOOKING_NOT_FOUND));
        if (booking.getStatus() != BookingStatus.ROOM_CHANGE_PENDING) {
            throw new AppException(ErrorCode.ROOM_CHANGE_REQUEST_NOT_PENDING);
        }
        return booking;
    }

    private BookingHistory getPendingRoomChangeForUpdate(int bookingId) {
        List<BookingHistory> requests = bookingHistoryRepository.findRoomChangesForUpdate(
                bookingId,
                BookingAction.CHANGE_ROOM,
                BookingStatus.ROOM_CHANGE_PENDING);
        if (requests.isEmpty()) {
            throw new AppException(ErrorCode.ROOM_CHANGE_REQUEST_NOT_FOUND);
        }
        return requests.getFirst();
    }

    private User getReceptionist(Long receptionistId) {
        if (receptionistId == null) {
            throw new AppException(ErrorCode.ROOM_CHANGE_RECEPTIONIST_SESSION_INVALID);
        }
        return userRepository.findById(receptionistId)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_CHANGE_RECEPTIONIST_NOT_FOUND));
    }

    private Room findRoom(List<Room> rooms, Integer roomId) {
        return rooms.stream()
                .filter(room -> room.getRoomId().equals(roomId))
                .findFirst()
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_CHANGE_ROOM_NOT_FOUND));
    }

    private void validateTargetRoom(Booking booking, Room newRoom) {
        if (newRoom.getStatus() != RoomStatus.AVAILABLE) {
            throw new AppException(ErrorCode.ROOM_CHANGE_ROOM_NOT_AVAILABLE);
        }
        if (newRoom.getRoomType().getMaxGuests() < booking.getNumberOfGuests()) {
            throw new AppException(ErrorCode.ROOM_CHANGE_CAPACITY_EXCEEDED);
        }

        LocalDate changeDate = LocalDate.now();
        if (!booking.getCheckOutDate().isAfter(changeDate)) {
            throw new AppException(ErrorCode.ROOM_CHANGE_BOOKING_NOT_ELIGIBLE);
        }

        boolean hasBlockingBooking = bookingRepository.existsBlockingBooking(
                newRoom.getRoomId(),
                changeDate,
                booking.getCheckOutDate(),
                Set.of(
                        BookingStatus.PENDING,
                        BookingStatus.CONFIRMED,
                        BookingStatus.CHECKED_IN,
                        BookingStatus.ROOM_CHANGE_PENDING));
        if (hasBlockingBooking) {
            throw new AppException(ErrorCode.ROOM_CHANGE_ROOM_RESERVED);
        }
    }

    private BookingDetailDTO toDto(Booking booking) {
        BookingDetailDTO dto = new BookingDetailDTO();
        dto.setBookingId(booking.getId());
        dto.setStatus(booking.getStatus().toString());
        dto.setCheckInDate(booking.getCheckInDate());
        dto.setCheckOutDate(booking.getCheckOutDate());
        dto.setActualCheckIn(booking.getActualCheckIn());
        dto.setActualCheckOut(booking.getActualCheckOut());
        dto.setTotalAmount(booking.getTotalAmount());
        dto.setNumberOfGuests(booking.getNumberOfGuests());
        dto.setCreatedAt(booking.getCreatedAt());
        if (booking.getCustomer() != null) {
            dto.setCustomerId(booking.getCustomer().getUserId());
            dto.setCustomerName(booking.getCustomer().getFullName());
            dto.setCustomerEmail(booking.getCustomer().getEmail());
            dto.setCustomerPhone(booking.getCustomer().getPhone());
        }
        if (booking.getRoom() != null) {
            dto.setRoomNumber(booking.getRoom().getRoomNumber());
            if (booking.getRoom().getRoomType() != null) {
                dto.setRoomTypeName(booking.getRoom().getRoomType().getTypeName());
            }
        }
        dto.setServices(booking.getBookingServices());
        return dto;
    }
}
