package hsf.g3.hotel_booking_system.dto.service;

import hsf.g3.hotel_booking_system.enums.service.ServiceStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class ServiceFormDTO {

    @NotBlank
    String serviceName;

    String description;

    @NotNull
    @DecimalMin("0.0")
    BigDecimal price;

    @NotNull
    ServiceStatus status;
}
