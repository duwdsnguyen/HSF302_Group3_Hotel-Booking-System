package hsf.g3.hotel_booking_system.repository.guest;

import hsf.g3.hotel_booking_system.entity.guest.Booking;
import hsf.g3.hotel_booking_system.enums.room.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    @Query("SELECT b FROM Booking b WHERE b.actualCheckIn IS NOT NULL AND b.customer.userId= :userId AND b.room.roomId= :roomId")
    Optional<Booking> getCheckedInBooking(Long userId, Integer roomId );

    @Query("SELECT b FROM Booking b WHERE " +
            "(:status IS NULL OR b.status = :status) AND " +
            "(:fullName IS NULL OR LOWER(b.customer.fullName) LIKE LOWER(:fullName)) AND " +
            "(:minPrice IS NULL OR b.totalAmount >= :minPrice) AND " +
            "(:maxPrice IS NULL OR b.totalAmount <= :maxPrice) " +
            "ORDER BY b.createdAt DESC")
    List<Booking> searchBooking(
            @Param("status") BookingStatus status,
            @Param("fullName") String fullName,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice
    );
}
