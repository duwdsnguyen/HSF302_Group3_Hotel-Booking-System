package hsf.g3.hotel_booking_system.dto.guest.room.request;

import hsf.g3.hotel_booking_system.enums.user.RoomStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomChangeRequest {
    Integer roomId;
    Integer pageNumber;
    Integer pageSize;
    String sortBy;
    String sortOrder;
}
