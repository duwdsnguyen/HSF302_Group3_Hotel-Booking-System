package hsf.g3.hotel_booking_system.service.user;

import hsf.g3.hotel_booking_system.dto.auth.forget_password.request.ForgetPasswordRequest;
import hsf.g3.hotel_booking_system.dto.auth.forget_password.response.ForgetPasswordResponse;
import hsf.g3.hotel_booking_system.dto.auth.login.LoginRequestDTO;
import hsf.g3.hotel_booking_system.dto.auth.register.RegisterRequestDTO;
import hsf.g3.hotel_booking_system.dto.auth.reset_password.ResetPasswordRequest;
import hsf.g3.hotel_booking_system.dto.user.UserInfoDTO;

public interface UserService {
    UserInfoDTO login (LoginRequestDTO loginRequestDTO);
    UserInfoDTO register(RegisterRequestDTO registerRequestDTO);
    ForgetPasswordResponse forgetPassword(ForgetPasswordRequest forgetPasswordRequest);
    boolean isValidToken( String token);
    boolean resetPassword(ResetPasswordRequest resetPasswordRequest, String token);
}
