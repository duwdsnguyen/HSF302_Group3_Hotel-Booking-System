package hsf.g3.hotel_booking_system.service.receptionist;

import hsf.g3.hotel_booking_system.entity.guest.Booking;

import hsf.g3.hotel_booking_system.dto.receptionist.BookingDetailDTO;

import java.util.List;

public interface BookingService {
    List<BookingDetailDTO> getBookingsForCheckIn(String search, Double minPrice, Double maxPrice, Integer roomTypeId);
    List<BookingDetailDTO> getBookingForCheckOut(String search, Double minPrice, Double maxPrice, Integer roomTypeId);
    void checkIn(int bookingId);
    void checkOut(int bookingId);
}
