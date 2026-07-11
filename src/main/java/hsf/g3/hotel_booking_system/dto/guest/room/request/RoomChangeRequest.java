package hsf.g3.hotel_booking_system.dto.guest.room.request;
import lombok.*;
import lombok.experimental.FieldDefaults;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomChangeRequest {
    Integer newRoomId;
    Integer pageNumber;
    Integer pageSize;
    String sortBy;
    String sortOrder;
}
