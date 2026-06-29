package hsf.g3.hotel_booking_system.repository;

import hsf.g3.hotel_booking_system.entity.RoomType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomTypeRepository extends JpaRepository<RoomType, Integer> {

    List<RoomType> findByStatus(String status);

    boolean existsByTypeName(String name);

    List<RoomType> findByTypeNameContainingIgnoreCase(String roomTypeName);
}
