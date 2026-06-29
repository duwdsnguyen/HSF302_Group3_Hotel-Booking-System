package hsf.g3.hotel_booking_system.entity.room;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "room_types")
@Getter
@Setter
public class RoomType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_type_id")
    Long roomTypeId;

    @Column(name = "type_name", nullable = false, columnDefinition = "NVARCHAR(100)")
    String typeName;

    @Column(name = "description", columnDefinition = "NVARCHAR(500)")
    String description;

    @Column(name = "max_guests", nullable = false)
    Integer maxGuests;

    @Column(name = "base_price", nullable = false)
    BigDecimal basePrice;
}
