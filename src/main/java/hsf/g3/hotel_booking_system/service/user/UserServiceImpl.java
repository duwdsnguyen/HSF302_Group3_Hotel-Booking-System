package hsf.g3.hotel_booking_system.service.user;

import hsf.g3.hotel_booking_system.dto.auth.LoginRequestDTO;
import hsf.g3.hotel_booking_system.dto.user.UserInfoDTO;
import hsf.g3.hotel_booking_system.entity.user.User;
import hsf.g3.hotel_booking_system.exception.ResourceNotFoundException;
import hsf.g3.hotel_booking_system.repository.user.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(ModelMapper modelMapper, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserInfoDTO login(LoginRequestDTO loginRequestDTO) {
        User userFromDTO = userRepository.findUserByEmailAndPassword(loginRequestDTO.getEmail(), loginRequestDTO.getPassword()).orElseThrow(() -> new ResourceNotFoundException("user","email",loginRequestDTO.getEmail()));
        if(!passwordEncoder.matches(loginRequestDTO.getPassword(), userFromDTO.getPassword())){
            throw new ResourceNotFoundException("User", "password", "Invalid password");
        }

        return modelMapper.map(userFromDTO,UserInfoDTO.class);
    }
}
