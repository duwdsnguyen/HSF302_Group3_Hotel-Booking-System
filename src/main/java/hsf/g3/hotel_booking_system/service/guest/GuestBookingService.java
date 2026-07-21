package hsf.g3.hotel_booking_system.service.guest;

import hsf.g3.hotel_booking_system.dto.guest.BookingDTO;
import hsf.g3.hotel_booking_system.dto.guest.request.BookingRequestDTO;

public interface GuestBookingService {
    BookingDTO createBooking(BookingRequestDTO request, Long customerId);
}

