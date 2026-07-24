package hsf.g3.hotel_booking_system.service.receptionist;

import hsf.g3.hotel_booking_system.dto.receptionist.BookingDetailDTO;
import hsf.g3.hotel_booking_system.dto.receptionist.BookingHistoryDTO;
import hsf.g3.hotel_booking_system.dto.receptionist.BookingSummaryDTO;
import hsf.g3.hotel_booking_system.entity.guest.Booking;
import hsf.g3.hotel_booking_system.enums.room.BookingStatus;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface BookingService {
    List<Booking> searchBookings(String status, String customerName, BigDecimal minPrice, BigDecimal maxPrice);

    BookingDetailDTO getBookingDetailsById(int bookingId);

    void confirmBooking(int bookingId);

    void cancelBooking(int bookingId);

    void checkIn(int bookingId);

    void checkOut(int bookingId);

    BookingHistoryDTO getPendingRoomChange(int bookingId);

    void approveRoomChange(int bookingId, Long receptionistId);

    void rejectRoomChange(int bookingId, Long receptionistId, String reason);

    BookingSummaryDTO getBookingSummary();

    Page<Booking> searchBookingPaged(
            Integer bookingId, BookingStatus status,
            String customerName, String phone,
            LocalDate checkInFrom, LocalDate checkInTo,
            LocalDate checkOutFrom, LocalDate checkOutTo,
            BigDecimal minPrice, BigDecimal maxPrice,
            String sortField, String sortDir,
             int page, int size
     );
}
