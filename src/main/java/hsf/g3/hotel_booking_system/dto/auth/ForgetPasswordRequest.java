package hsf.g3.hotel_booking_system.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ForgetPasswordRequest {

    @Email(message = "Email phải đúng format")
    @NotBlank(message = "Không được để email bị trống")
    String email;
}
