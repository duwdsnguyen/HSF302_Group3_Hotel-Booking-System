package hsf.g3.hotel_booking_system.repository.admin;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import hsf.g3.hotel_booking_system.entity.room.Room;
import hsf.g3.hotel_booking_system.enums.room.RoomStatus;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer> {

    boolean existsByRoomNumber(String roomNumber);

    boolean existsByRoomNumberAndRoomIdNot(String roomNumber, Integer roomId);

    @Query("SELECT r FROM Room r "
            + "WHERE r.status = :roomStatus "
            + "AND r.roomType.maxGuests >= :numberOfGuests "
            + "AND r.roomId NOT IN ("
            + "    SELECT b.room.roomId FROM Booking b "
            + "    WHERE b.checkInDate < :checkOutDate "
            + "    AND b.checkOutDate > :checkInDate "
            + "    AND b.status IN :bookingStatuses"
            + ")")
    List<Room> findAvailableRooms(
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate,
            @Param("numberOfGuests") Integer numberOfGuests,
            @Param("roomStatus") RoomStatus roomStatus,
            @Param("bookingStatuses") Collection<String> bookingStatuses);


    @Query("SELECT r FROM Room r WHERE r.status = RoomStatus.AVAILABLE")
    Page<Room> getAllAvailableRoom(Pageable pageable);

    Boolean existsRoomByRoomId(Integer roomId);

    Optional<Room> findRoomByRoomId(Integer roomId);
}
