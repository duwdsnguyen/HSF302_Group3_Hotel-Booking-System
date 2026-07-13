package hsf.g3.hotel_booking_system.dto.guest.room.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RoomResponse {
    List<RoomDTO> content;
    private Integer pageNumber;
    private Integer pageSize;
    private Long totalElements;
    private Integer totalPages;
    private boolean lastPage;

}
