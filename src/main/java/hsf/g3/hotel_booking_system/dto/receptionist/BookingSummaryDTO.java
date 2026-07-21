package hsf.g3.hotel_booking_system.dto.receptionist;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingSummaryDTO {
    private long totalBookings;
    private long pendingCount;
    private long confirmedCount;
    private long checkedInCount;
    private long checkedOutCount;
    private long cancelledCount;
    private BigDecimal totalRevenue;
}