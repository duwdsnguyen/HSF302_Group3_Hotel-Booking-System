package hsf.g3.hotel_booking_system.entity.guest;

import hsf.g3.hotel_booking_system.entity.service.HotelService;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "booking_services")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_service_id")
    private Integer bookingServiceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private HotelService service;

    @Column(name = "quantity", nullable = false)
    private Integer quantity = 1;

    @Column(name = "price", precision = 18, scale = 2, nullable = false)
    private BigDecimal price;
}
