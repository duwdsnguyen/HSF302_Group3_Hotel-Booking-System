package hsf.g3.hotel_booking_system.dto.user;

import hsf.g3.hotel_booking_system.entity.user.Role;
import hsf.g3.hotel_booking_system.enums.user.UserStatus;
import lombok.*;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class UserInfoDTO {

    Long userId;
    String fullName;
    String email;
    String phone;
    UserStatus status;
    Set<Role> roles;
}
