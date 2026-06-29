package hsf.g3.hotel_booking_system.service.user;

import hsf.g3.hotel_booking_system.dto.auth.*;
import hsf.g3.hotel_booking_system.dto.user.UserInfoDTO;

public interface UserService {
    UserInfoDTO login (LoginRequestDTO loginRequestDTO);
    UserInfoDTO register(RegisterRequestDTO registerRequestDTO);
    ForgetPasswordResponse forgetPassword(ForgetPasswordRequest forgetPasswordRequest);
    boolean isValidToken( String token);
    boolean resetPassword(ResetPasswordRequest resetPasswordRequest,String token);
}
