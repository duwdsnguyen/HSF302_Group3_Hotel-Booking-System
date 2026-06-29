package hsf.g3.hotel_booking_system.dto.room;

import hsf.g3.hotel_booking_system.enums.user.RoomStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomRequestDTO {
    private String roomNumber;
    private Integer roomTypeId;
    private Integer floorNumber;
    private RoomStatus status;
    private String description;
}
