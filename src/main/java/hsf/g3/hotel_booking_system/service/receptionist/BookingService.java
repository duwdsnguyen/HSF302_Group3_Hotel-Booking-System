package hsf.g3.hotel_booking_system.service.receptionist;

import hsf.g3.hotel_booking_system.dto.receptionist.BookingDetailDTO;
import hsf.g3.hotel_booking_system.entity.guest.Booking;
import hsf.g3.hotel_booking_system.enums.room.BookingStatus;

import java.math.BigDecimal;
import java.util.List;

public interface BookingService{
    List<Booking> searchBookings(String status, String customerName, BigDecimal minPrice, BigDecimal maxPrice);
    BookingDetailDTO getBookingDetailsById(int bookingId);
    void confirmBooking(int bookingId);
    void cancelBooking(int bookingId);
    void checkIn(int bookingId);
    void checkOut(int bookingId);
}
