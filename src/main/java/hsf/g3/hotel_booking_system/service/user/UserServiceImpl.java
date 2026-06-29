package hsf.g3.hotel_booking_system.service.user;

import hsf.g3.hotel_booking_system.controller.user.AuthController;
import hsf.g3.hotel_booking_system.dto.auth.LoginRequestDTO;
import hsf.g3.hotel_booking_system.dto.auth.RegisterRequestDTO;
import hsf.g3.hotel_booking_system.dto.user.UserInfoDTO;
import hsf.g3.hotel_booking_system.entity.user.Role;
import hsf.g3.hotel_booking_system.entity.user.User;
import hsf.g3.hotel_booking_system.enums.user.AppRole;
import hsf.g3.hotel_booking_system.repository.user.RoleRepository;
import hsf.g3.hotel_booking_system.repository.user.UserRepository;
import hsf.g3.hotel_booking_system.util.PasswordUtil;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    private final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserServiceImpl(ModelMapper modelMapper, UserRepository userRepository, RoleRepository roleRepository) {
        this.modelMapper = modelMapper;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }


    @Override
    public UserInfoDTO login(LoginRequestDTO loginRequestDTO) {
        User userFromDTO = userRepository.findUserByEmailWithRoles(loginRequestDTO.getEmail()).orElse(null);
        if(userFromDTO == null || !PasswordUtil.checkPassword(loginRequestDTO.getPassword(), userFromDTO.getPassword())){
            return null;
        }

        return modelMapper.map(userFromDTO,UserInfoDTO.class);
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
        Role role = roleRepository.findRoleByRoleCode(AppRole.GUEST).orElseGet( () -> roleRepository.save(new Role(AppRole.GUEST)));
        Set<Role> roles = Set.of(role);
        user.setRoles(roles);
        userRepository.save(user);
        return modelMapper.map(user, UserInfoDTO.class);
    }
}
