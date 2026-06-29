package hsf.g3.hotel_booking_system.entity.service;

import hsf.g3.hotel_booking_system.enums.service.ServiceStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.math.BigDecimal;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "services")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class HotelService {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "service_id")
    Long serviceId;

    @Column(name = "service_name", columnDefinition = "NVARCHAR(100)", nullable = false)
    String serviceName;

    @Column(name = "description", columnDefinition = "NVARCHAR(500)")
    String description;

    @Column(name = "price", nullable = false)
    BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", columnDefinition = "VARCHAR(30)", nullable = false)
    ServiceStatus status = ServiceStatus.ACTIVE;

}
