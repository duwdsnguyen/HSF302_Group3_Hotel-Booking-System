package hsf.g3.hotel_booking_system.repository.room;

import hsf.g3.hotel_booking_system.entity.room.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer> {

    boolean existsByRoomNumber(String roomNumber);
}
