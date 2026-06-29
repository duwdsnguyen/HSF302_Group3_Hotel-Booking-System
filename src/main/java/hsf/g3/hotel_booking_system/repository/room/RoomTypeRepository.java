package hsf.g3.hotel_booking_system.repository.room;

import hsf.g3.hotel_booking_system.entity.room.RoomType;
import java.util.List;

import hsf.g3.hotel_booking_system.enums.user.RoomTypeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomTypeRepository extends JpaRepository<RoomType, Integer> {

    List<RoomType> findByStatus(RoomTypeStatus status);

    boolean existsByTypeName(String name);

    List<RoomType> findByTypeNameContainingIgnoreCase(String roomTypeName);
}
