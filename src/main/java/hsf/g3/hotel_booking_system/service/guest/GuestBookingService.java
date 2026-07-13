package hsf.g3.hotel_booking_system.service.guest;

import hsf.g3.hotel_booking_system.entity.guest.Booking;

import java.time.LocalDate;

import java.util.List;

public interface GuestBookingService {
    Booking createBooking(Integer roomId, List<Long> serviceIds, LocalDate checkIn, LocalDate checkOut, Integer guests, Long customerId);
}
