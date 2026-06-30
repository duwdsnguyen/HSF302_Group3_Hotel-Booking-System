package hsf.g3.hotel_booking_system.repository.receptionist;

import hsf.g3.hotel_booking_system.entity.guest.Booking;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    @Query("SELECT b FROM Booking b where b.status = 'CONFIRMED'")
    List<Booking> findAllConfirmedBookings();

    @Query("SELECT b FROM Booking b where b.status = 'CHECKED_IN'")
    List<Booking> findAllCheckedInBookings();
}
