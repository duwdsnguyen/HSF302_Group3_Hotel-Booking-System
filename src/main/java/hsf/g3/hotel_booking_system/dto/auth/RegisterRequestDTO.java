package hsf.g3.hotel_booking_system.dto.auth;

import hsf.g3.hotel_booking_system.entity.user.Role;
import hsf.g3.hotel_booking_system.enums.user.AppRole;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.Length;

import java.util.HashSet;
import java.util.Set;


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
    String phone;

}
