package hsf.g3.hotel_booking_system.repository.admin;

import hsf.g3.hotel_booking_system.entity.room.RoomTypeImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomTypeImageRepository extends JpaRepository<RoomTypeImage, Integer> {

    List<RoomTypeImage> findByRoomType_RoomTypeIdOrderByDisplayOrderAsc(Integer roomTypeId);

    @Modifying
    @Query("DELETE FROM RoomTypeImage i WHERE i.imageId = :imageId AND i.roomType.roomTypeId = :roomTypeId")
    void deleteByImageIdAndRoomTypeId(@Param("imageId") Integer imageId,
                                      @Param("roomTypeId") Integer roomTypeId);
}