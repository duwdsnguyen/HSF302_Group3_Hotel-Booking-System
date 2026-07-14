package hsf.g3.hotel_booking_system.repository.guest;

import hsf.g3.hotel_booking_system.entity.guest.Booking;
import hsf.g3.hotel_booking_system.entity.room.Room;
import hsf.g3.hotel_booking_system.enums.room.BookingStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM Booking b " +
            "WHERE b.actualCheckIn IS NOT NULL " +
            "AND b.actualCheckOut IS NULL " +
            "AND b.customer.userId = :userId " +
            "AND b.room.roomId = :roomId")
    Optional<Booking> getCheckedInBooking( Long userId, Integer roomId);

    @Query("SELECT b.room FROM Booking b " +
            "WHERE b.actualCheckIn IS NOT NULL " +
            "AND b.actualCheckOut IS NULL " +
            "AND b.customer.userId = :userId " +
            "ORDER BY b.room.roomNumber")
    List<Room> getCheckedInRooms(Long userId);

    @Query("SELECT CASE WHEN COUNT(b) > 0 THEN true ELSE false END FROM Booking b " +
            "WHERE b.room.roomId = :roomId " +
            "AND b.checkInDate < :checkOutDate " +
            "AND b.checkOutDate > :checkInDate " +
            "AND b.status IN :blockingStatuses")
    boolean existsBlockingBooking( Integer roomId, LocalDate checkInDate, LocalDate checkOutDate, Collection<BookingStatus> blockingStatuses);
}
