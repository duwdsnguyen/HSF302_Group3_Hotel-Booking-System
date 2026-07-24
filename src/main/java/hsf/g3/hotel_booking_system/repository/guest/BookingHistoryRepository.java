package hsf.g3.hotel_booking_system.repository.guest;

import hsf.g3.hotel_booking_system.entity.guest.BookingHistory;
import hsf.g3.hotel_booking_system.enums.room.BookingAction;
import hsf.g3.hotel_booking_system.enums.room.BookingStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookingHistoryRepository extends JpaRepository<BookingHistory,Long> {
    @Query("SELECT h FROM BookingHistory h " +
            "JOIN FETCH h.booking b " +
            "JOIN FETCH h.oldRoom " +
            "JOIN FETCH h.newRoom " +
            "WHERE b.customer.userId = :userId " +
            "AND h.action = :action " +
            "ORDER BY h.changedAt DESC, h.bookingHistoryId DESC")
    List<BookingHistory> findRoomChangeHistoryPerformedByUser(Long userId, BookingAction action);

    @Query("SELECT h FROM BookingHistory h " +
            "JOIN FETCH h.oldRoom " +
            "JOIN FETCH h.newRoom " +
            "LEFT JOIN FETCH h.changedBy " +
            "WHERE h.booking.id = :bookingId " +
            "AND h.action = :action " +
            "AND h.newStatus = :status " +
            "ORDER BY h.changedAt DESC, h.bookingHistoryId DESC")
    List<BookingHistory> findRoomChanges(Integer bookingId, BookingAction action, BookingStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT h FROM BookingHistory h " +
            "WHERE h.booking.id = :bookingId " +
            "AND h.action = :action " +
            "AND h.newStatus = :status " +
            "ORDER BY h.changedAt DESC, h.bookingHistoryId DESC")
    List<BookingHistory> findRoomChangesForUpdate(Integer bookingId, BookingAction action, BookingStatus status);
}
