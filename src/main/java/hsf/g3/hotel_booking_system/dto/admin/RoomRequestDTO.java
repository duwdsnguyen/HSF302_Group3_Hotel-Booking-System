package hsf.g3.hotel_booking_system.dto.admin;

import hsf.g3.hotel_booking_system.enums.room.RoomStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomRequestDTO {
    @NotBlank(message = "Tên phòng không được để trống")
    private String roomNumber;

    @NotNull(message = "Vui lòng chọn loại phòng")
    private Integer roomTypeId;

    @NotNull(message = "Tầng không được để trống")
    @Min(value = 1, message = "Tầng phải lớn hơn 0")
    private Integer floorNumber;

    @NotNull(message = "Vui lòng chọn trạng thái")
    private RoomStatus status;
    private String description;
}
