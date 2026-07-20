package hsf.g3.hotel_booking_system.dto.guest.room.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class RoomChangeHistoryDTO {
    private static final DateTimeFormatter DISPLAY_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private Long bookingHistoryId;
    private Integer bookingId;
    private String oldRoomNumber;
    private String newRoomNumber;
    private LocalDateTime changedAt;
    private String description;

    public String getChangedAtDisplay() {
        return changedAt == null ? "" : changedAt.format(DISPLAY_FORMAT);
    }
}
