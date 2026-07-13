package hsf.g3.hotel_booking_system.dto.auth.forget_password.response;

import hsf.g3.hotel_booking_system.entity.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ForgetPasswordResponse {
    String token;
    LocalDateTime expiredAt;
    String email;
    Boolean used;
    User user;

}
