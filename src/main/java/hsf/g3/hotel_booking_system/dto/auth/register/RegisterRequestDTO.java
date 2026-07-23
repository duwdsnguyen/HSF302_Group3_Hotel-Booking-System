package hsf.g3.hotel_booking_system.dto.auth.register;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RegisterRequestDTO {

    @Email(message = "Email không đúng định dạng.Vui lòng thử lại.")
    @NotBlank(message = "Email không được để trống.")
    String email;

    @NotBlank(message = "Mật khẩu không được để trống.")
    @Size(min = 5,message = "Mật khẩu phải có trên 5 ký tự")
    String password;

    @NotBlank(message = "Tên không được để trống")
    @Size(max = 100,message = "Tên không được vượt quá 100 kí tự")
    String fullName;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Positive(message = "Phải là dãy số nguyên dương")
    @Size(min = 5,message = "Số điện thoại phải lớn hơn 5")
    String phone;

}
