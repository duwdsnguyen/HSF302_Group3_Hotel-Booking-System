package hsf.g3.hotel_booking_system.service.receptionist;

import hsf.g3.hotel_booking_system.dto.receptionist.BookingDetailDTO;
import hsf.g3.hotel_booking_system.dto.receptionist.BookingSummaryDTO;
import hsf.g3.hotel_booking_system.entity.guest.Booking;
import hsf.g3.hotel_booking_system.entity.room.Room;
import hsf.g3.hotel_booking_system.enums.room.BookingStatus;
import hsf.g3.hotel_booking_system.enums.room.RoomStatus;
import hsf.g3.hotel_booking_system.repository.admin.RoomRepository;
import hsf.g3.hotel_booking_system.repository.guest.BookingRepository;
import hsf.g3.hotel_booking_system.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;

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
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new RuntimeException("Chỉ duyệt đơn với trạng thái 'PENDING'");
        }
        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);
    }

    @Override
    public void cancelBooking(int bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Đơn booking " + bookingId + " không tồn tại"));
        if (booking.getStatus() != BookingStatus.PENDING) {
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
        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new RuntimeException("Chỉ duyệt đơn với trạng thái 'CONFIRMED'");
        }

        LocalDate today = LocalDate.now();

        if (today.isBefore(booking.getCheckInDate())) {
            throw new RuntimeException("Chưa đến ngày check-in. Khách không thể check-in sớm hơn ngày đã đặt!");
        }

        LocalDate expectedCheckIn = booking.getCheckInDate().plusDays(1);
        if (today.isAfter(expectedCheckIn)) {
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
        if (booking.getStatus() != BookingStatus.CHECKED_IN) {
            throw new RuntimeException("Chỉ duyệt đơn với trạng thái 'CHECKED_IN'");
        }
        LocalDate today = LocalDate.now();

        if (today.isAfter(booking.getCheckOutDate())) {
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

    @Override
    public BookingSummaryDTO getBookingSummary() {
        List<Object[]> countData = bookingRepository.countByStatus();

        long pendingCount = 0, confirmedCount = 0, checkedInCount = 0;
        long checkedOutCount = 0, cancelledCount = 0, totalBookings = 0;

        for (Object[] row : countData) {
            BookingStatus status = (BookingStatus) row[0];
            long count = (long) row[1];
            switch (status) {
                case PENDING -> pendingCount = count;
                case CONFIRMED -> confirmedCount = count;
                case CHECKED_IN -> checkedInCount = count;
                case CHECKED_OUT -> checkedOutCount = count;
                case COMPLETED -> checkedOutCount += count;
                case CANCELLED -> cancelledCount = count;
            }
            totalBookings += count;
        }

        List<BookingStatus> revenueStatuses = Arrays.asList(
                BookingStatus.CHECKED_OUT, BookingStatus.COMPLETED
        );
        BigDecimal totalRevenue = bookingRepository.sumTotalAmountByStatus(revenueStatuses);

        return BookingSummaryDTO.builder()
                .totalBookings(totalBookings)
                .pendingCount(pendingCount)
                .confirmedCount(confirmedCount)
                .checkedInCount(checkedInCount)
                .checkedOutCount(checkedOutCount)
                .cancelledCount(cancelledCount)
                .totalRevenue(totalRevenue)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Booking> searchBookingPaged(
            Integer bookingId, BookingStatus status,
            String customerName, String phone,
            LocalDate checkInFrom, LocalDate checkInTo,
            LocalDate checkOutFrom, LocalDate checkOutTo,
            BigDecimal minPrice, BigDecimal maxPrice,
            String sortField, String sortDir,
            int page, int size
    ) {

        if (customerName != null && !customerName.trim().isEmpty()) {
            customerName = "%" + customerName.trim() + "%";
        } else {
            customerName = null;
        }
        if (phone != null && !phone.trim().isEmpty()) {
            phone = "%" + phone.trim() + "%";
        } else {
            phone = null;
        }

        Sort sort;
        if (sortField != null && !sortField.trim().isEmpty()) {
            Sort.Direction dir = (sortDir != null && sortDir.equalsIgnoreCase("asc"))
                    ? Sort.Direction.ASC : Sort.Direction.DESC;
            sort = Sort.by(dir, sortField);
        } else {
            sort = Sort.by(Sort.Direction.DESC, "createdAt");
        }
        Pageable pageable = PageRequest.of(page, size, sort);

        return bookingRepository.searchBookingPaged(
                bookingId, status, customerName, phone,
                checkInFrom, checkInTo, checkOutFrom, checkOutTo,
                minPrice, maxPrice, pageable
        );
    }
}
