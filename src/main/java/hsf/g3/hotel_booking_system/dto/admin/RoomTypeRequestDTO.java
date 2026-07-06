package hsf.g3.hotel_booking_system.dto.admin;

import hsf.g3.hotel_booking_system.enums.room.RoomTypeStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class RoomTypeRequestDTO {
    @NotBlank(message = "Tên loại phòng không được để trống")
    private String typeName;
    private String description;

    @NotNull(message = "Số khách không được để trống")
    @Min(value = 1, message = "Số khách phải lớn hơn 0")
    private Integer maxGuests;

    @NotNull(message = "Giá phòng không được để trống")
    @Min(value = 1, message = "Giá phòng phải lớn hơn 0")
    private BigDecimal basePrice;

    @NotNull(message = "Vui lòng chọn trạng thái")
    private RoomTypeStatus status;
}

