package hsf.g3.hotel_booking_system.service.user;

import hsf.g3.hotel_booking_system.dto.auth.LoginRequestDTO;
import hsf.g3.hotel_booking_system.dto.auth.RegisterRequestDTO;
import hsf.g3.hotel_booking_system.dto.user.UserInfoDTO;

public interface UserService {
    UserInfoDTO login (LoginRequestDTO loginRequestDTO);
    UserInfoDTO register(RegisterRequestDTO registerRequestDTO);
}
