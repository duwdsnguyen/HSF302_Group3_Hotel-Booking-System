package hsf.g3.hotel_booking_system.service.user;

import hsf.g3.hotel_booking_system.dto.auth.forget_password.request.ForgetPasswordRequest;
import hsf.g3.hotel_booking_system.dto.auth.forget_password.response.ForgetPasswordResponse;
import hsf.g3.hotel_booking_system.dto.auth.login.LoginRequestDTO;
import hsf.g3.hotel_booking_system.dto.auth.register.RegisterRequestDTO;
import hsf.g3.hotel_booking_system.dto.auth.reset_password.ResetPasswordRequest;
import hsf.g3.hotel_booking_system.dto.user.UserInfoDTO;
import hsf.g3.hotel_booking_system.entity.user.ResetToken;
import hsf.g3.hotel_booking_system.entity.user.Role;
import hsf.g3.hotel_booking_system.entity.user.User;
import hsf.g3.hotel_booking_system.enums.user.AppRole;
import hsf.g3.hotel_booking_system.exception.AppException;
import hsf.g3.hotel_booking_system.exception.ErrorCode;
import hsf.g3.hotel_booking_system.repository.user.ResetTokenRepository;
import hsf.g3.hotel_booking_system.repository.user.RoleRepository;
import hsf.g3.hotel_booking_system.repository.user.UserRepository;
import hsf.g3.hotel_booking_system.service.external.MailService;
import hsf.g3.hotel_booking_system.util.PasswordUtil;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ResetTokenRepository resetTokenRepository;
    private final MailService mailService ;

    public UserServiceImpl(ModelMapper modelMapper, UserRepository userRepository, RoleRepository roleRepository, ResetTokenRepository resetTokenRepository, MailService mailService) {
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.resetTokenRepository = resetTokenRepository;
        this.mailService = mailService;
    }


    @Override
    public UserInfoDTO login(LoginRequestDTO loginRequestDTO) {
        User userFromDB = userRepository.findUserByEmailWithRoles(loginRequestDTO.getEmail()).orElse(null);
        if(userFromDB == null || !PasswordUtil.checkPassword(loginRequestDTO.getPassword(), userFromDB.getPassword())){
            return null;
        }

        return modelMapper.map(userFromDB,UserInfoDTO.class);
    }

    @Override
    public UserInfoDTO register(RegisterRequestDTO registerRequestDTO) {
        if(userRepository.existsUserByEmail(registerRequestDTO.getEmail())){
            return null;
        }
        User user = new User();
        user.setFullName(registerRequestDTO.getFullName());
        user.setEmail(registerRequestDTO.getEmail());
        user.setPassword(PasswordUtil.hashPassword(registerRequestDTO.getPassword()));
        user.setPhone(registerRequestDTO.getPhone());
        Role guestRole = Role.builder().roleCode(AppRole.GUEST).build();
        Role role = roleRepository.findRoleByRoleCode(AppRole.GUEST).orElseGet( () -> roleRepository.save(guestRole));
        Set<Role> roles = Set.of(role);
        user.setRoles(roles);
        userRepository.save(user);
        return modelMapper.map(user, UserInfoDTO.class);
    }
    
    @Override
    public ForgetPasswordResponse forgetPassword(ForgetPasswordRequest forgetPasswordRequest) {
        ResetToken resetToken = createResetToken(forgetPasswordRequest.getEmail());
        if(resetToken != null){
            mailService.sendResetPasswordEmail(forgetPasswordRequest.getEmail(),"http://localhost:8080/v1/auth/reset-password?token="+resetToken.getToken());
        }
        return modelMapper.map(resetToken, ForgetPasswordResponse.class);
    }

    private ResetToken createResetToken(String email){
        if(!userRepository.existsUserByEmail(email)){
            return null;
        }
        ResetToken resetToken = new ResetToken();
        resetToken.setToken(UUID.randomUUID().toString());
        resetToken.setEmail(email);
        User user = userRepository.findUserByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        resetToken.setUser(user);
        resetToken.setExpiredAt(LocalDateTime.now().plusHours(24));
        return resetTokenRepository.save(resetToken);
    }
    @Override
    public boolean isValidToken(String token) {
        return resetTokenRepository.existsByTokenAndUsedFalseAndExpiredAtAfter(token,LocalDateTime.now()) ;
    }

    @Transactional
    @Override
    public boolean resetPassword(ResetPasswordRequest resetPasswordRequest, String token) {
        User userInDB = userRepository.findUserByResetToken(token).orElse(null);
        ResetToken resetToken = resetTokenRepository.findByToken(token).orElse(null);
        if(userInDB != null && resetToken != null){
            userInDB.setPassword(PasswordUtil.hashPassword(resetPasswordRequest.getNewPassword()));
            resetToken.setUsed(true);
            userRepository.save(userInDB);
            resetTokenRepository.save(resetToken);
            return true;
        }
        return false;
    }

}
