package hsf.g3.hotel_booking_system.dto.auth;

import hsf.g3.hotel_booking_system.entity.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
