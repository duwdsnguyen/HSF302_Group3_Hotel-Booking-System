package hsf.g3.hotel_booking_system.entity;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "room_types")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_type_id")
    private Integer roomTypeId;

    @Column(name = "type_name", nullable = false, unique = true,  length = 100)
    private String typeName;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "max_guests",  nullable = false)
    private int maxGuests;

    @Column(name = "base_price", nullable = false, precision = 18, scale = 2)
    private BigDecimal basePrice;

    @Column(name = "status",  nullable = false, length = 30)
    private String status = "ACTIVE";
}
