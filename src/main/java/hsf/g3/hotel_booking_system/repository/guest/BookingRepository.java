package hsf.g3.hotel_booking_system.repository.guest;

import hsf.g3.hotel_booking_system.entity.guest.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    @Query("SELECT b FROM Booking b WHERE b.actualCheckIn IS NOT NULL AND b.customer.userId= :userId AND b.room.roomId= :roomId")
    Optional<Booking> getCheckedInBooking(Long userId, Integer roomId );
}
