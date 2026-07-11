package hsf.g3.hotel_booking_system.dto.auth.reset_password;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ResetPasswordRequest {

    @NotBlank(message = "Mật khẩu không được để trống.")
    @Size(min = 5,message = "Mật khẩu phải có trên 5 ký tự")
    String newPassword;


    @NotBlank(message = "Mật khẩu không được để trống.")
    @Size(min = 5,message = "Mật khẩu phải có trên 5 ký tự")
    String confirmedPassword;
}
