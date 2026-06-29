package hsf.g3.hotel_booking_system.entity.guest;

import hsf.g3.hotel_booking_system.entity.RoomType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rooms")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id")
    private Long id;

    @Column(name = "room_number", nullable = false, unique = true, length = 20)
    private String roomNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "room_type_id", nullable = false)
    private RoomType roomType;

    @Column(name = "floor_number")
    private int floorNumber;

    @Column(name = "status", nullable = false, length = 30)
    private String status = "Available";

    @Column(name = "description", length = 500)
    private String description;
}