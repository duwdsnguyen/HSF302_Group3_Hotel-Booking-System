package hsf.g3.hotel_booking_system.repository.admin;

import hsf.g3.hotel_booking_system.entity.room.Room;
import hsf.g3.hotel_booking_system.enums.user.RoomStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer> {

    boolean existsByRoomNumber(String roomNumber);

    @Query("SELECT r FROM Room r " +
            "WHERE r.status = :roomStatus " +
            "AND r.roomType.maxGuests >= :numberOfGuests " +
            "AND r.roomId NOT IN (" +
            "    SELECT b.roomId FROM Booking b " +
            "    WHERE b.checkInDate < :checkOutDate " +
            "    AND b.checkOutDate > :checkInDate " +
            "    AND b.status IN :bookingStatuses" +
            ")")
    List<Room> findAvailableRooms(
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate,
            @Param("numberOfGuests") Integer numberOfGuests,
            @Param("roomStatus") RoomStatus roomStatus,
            @Param("bookingStatuses") Collection<String> bookingStatuses);
}
