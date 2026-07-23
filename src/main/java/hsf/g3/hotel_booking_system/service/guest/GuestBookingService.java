package hsf.g3.hotel_booking_system.service.guest;

import hsf.g3.hotel_booking_system.dto.guest.BookingDTO;
import hsf.g3.hotel_booking_system.dto.guest.BookingHistoryDTO;
import hsf.g3.hotel_booking_system.dto.guest.request.BookingRequestDTO;
import hsf.g3.hotel_booking_system.enums.room.BookingStatus;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface GuestBookingService {
    BookingDTO createBooking(BookingRequestDTO request, Long customerId);

    @Transactional(readOnly = true)
    List<BookingHistoryDTO> getBookingHistory(Long customerId, BookingStatus status, String keyword);

    @Transactional(readOnly = true)
    BookingHistoryDTO getBookingDetail(Integer bookingId, Long customerId);

    @Transactional
    void cancelBooking(Integer bookingId, Long customerId);
}

