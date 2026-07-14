package hsf.g3.hotel_booking_system.entity.guest;

import hsf.g3.hotel_booking_system.entity.Base;
import hsf.g3.hotel_booking_system.entity.room.Room;
import hsf.g3.hotel_booking_system.entity.user.User;
import hsf.g3.hotel_booking_system.enums.room.BookingAction;
import hsf.g3.hotel_booking_system.enums.room.BookingStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "booking_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class BookingHistory extends Base {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_history_id")
    Long bookingHistoryId;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    Booking booking;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by")
    User changedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "old_status", length = 30)
    BookingStatus oldStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", length = 30)
    BookingStatus newStatus;

    @Column(name = "action", nullable = false, length = 100)
    @Enumerated(EnumType.STRING)
    BookingAction action;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "old_room_id")
    Room oldRoom;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "new_room_id")
    Room newRoom;

    @Column(name = "description", columnDefinition = "TEXT")
    String description;

    @Column(name = "changed_at", nullable = false)
    LocalDateTime changedAt;
}
