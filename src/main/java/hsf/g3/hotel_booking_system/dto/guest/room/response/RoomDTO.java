package hsf.g3.hotel_booking_system.dto.guest.room.response;

import hsf.g3.hotel_booking_system.enums.room.RoomStatus;
import lombok.*;
import lombok.experimental.FieldDefaults;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RoomDTO {
    Integer roomId;
    String roomNumber;
    String typeName;
    Integer floorNumber;
    RoomStatus status;
    String description;
}
