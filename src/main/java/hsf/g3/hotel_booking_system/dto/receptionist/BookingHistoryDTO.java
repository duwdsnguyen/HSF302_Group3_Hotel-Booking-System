package hsf.g3.hotel_booking_system.dto.receptionist;

import hsf.g3.hotel_booking_system.enums.room.BookingAction;
import hsf.g3.hotel_booking_system.enums.room.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingHistoryDTO {
    private Long bookingHistoryId;
    private Integer bookingId;
    private Long changedById;
    private String changedByName;
    private BookingStatus oldStatus;
    private BookingStatus newStatus;
    private BookingAction action;
    private Integer oldRoomId;
    private String oldRoomNumber;
    private Integer newRoomId;
    private String newRoomNumber;
    private String description;
    private LocalDateTime changedAt;
}
