package hsf.g3.hotel_booking_system.dto.auth.login;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginRequestDTO {

    @Email(message = "Email không đúng định dạng.Vui lòng thử lại.")
    @NotBlank(message = "Email không được để trống.")
    String email;

    @NotBlank(message = "Mật khẩu không được để trống.")
    String password;
}
