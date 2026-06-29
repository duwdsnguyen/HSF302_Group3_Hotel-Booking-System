package hsf.g3.hotel_booking_system.repository.guest;

import hsf.g3.hotel_booking_system.entity.guest.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer> {
    @Query("SELECT r FROM Room r " +
            "WHERE r.status = 'AVAILABLE' " +
            "AND r.roomType.maxGuests >= :numberOfGuests " +
            "AND r.id NOT IN (" +
            "    SELECT b.roomId FROM Booking b " +
            "    WHERE (b.checkInDate < :checkOutDate) " +
            "    AND (b.checkOutDate > :checkInDate) " +
            "    AND (b.status IN ('PENDING', 'CONFIRMED'))" +
            ")")
    List<Room> findAvailableRooms(
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate,
            @Param("numberOfGuests") Integer numberOfGuests);
}
