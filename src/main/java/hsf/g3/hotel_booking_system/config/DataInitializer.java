package hsf.g3.hotel_booking_system.config;

import hsf.g3.hotel_booking_system.entity.user.Role;
import hsf.g3.hotel_booking_system.entity.user.User;
import hsf.g3.hotel_booking_system.enums.user.AppRole;
import hsf.g3.hotel_booking_system.repository.user.RoleRepository;
import hsf.g3.hotel_booking_system.repository.user.UserRepository;
import hsf.g3.hotel_booking_system.util.PasswordUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner init(RoleRepository roleRepository, UserRepository userRepository) {
        return args -> {
            Role adminRole = roleRepository.findRoleByRoleCode(AppRole.ADMIN).orElseGet(() -> roleRepository.save(new Role(AppRole.ADMIN)));
            Role receptionistRole = roleRepository.findRoleByRoleCode(AppRole.RECEPTIONIST).orElseGet(() -> roleRepository.save(new Role((AppRole.RECEPTIONIST))));
            Role guestRole = roleRepository.findRoleByRoleCode(AppRole.GUEST).orElseGet(() -> roleRepository.save(new Role(AppRole.GUEST)));
            Set<Role> adRole = Set.of(adminRole);
            Set<Role> recepRole = Set.of(receptionistRole);
            Set<Role> gRole = Set.of(guestRole);

            if (!userRepository.existsUserByEmail("admin123@gmail.com")) {
                User user = new User("Nguyễn Văn Admin", "admin123@gmail.com", PasswordUtil.hashPassword("admin123"), "123456789");
                userRepository.save(user);
            }

            if (!userRepository.existsUserByEmail("recept123@gmail.com")) {
                User user = new User("Nguyễn Văn Lễ Tân", "recept123@gmail.com", PasswordUtil.hashPassword("recept123"), "0914505521");
                userRepository.save(user);
            }

            if (!userRepository.existsUserByEmail("guest123@gmail.com")) {
                User user = new User("Nguyễn Văn Khách", "guest123@gmail.com", PasswordUtil.hashPassword("guest123"), "0914505521");
                userRepository.save(user);
            }


            userRepository.findUserByEmailWithRoles("admin123@gmail.com").ifPresent((user) -> {
                user.setRoles(adRole);
                userRepository.save(user);
            });

            userRepository.findUserByEmailWithRoles("guest123@gmail.com").ifPresent((user) -> {
                user.setRoles(gRole);
                userRepository.save(user);
            });

            userRepository.findUserByEmailWithRoles("recept123@gmail.com").ifPresent((user) -> {
                user.setRoles(recepRole);
                userRepository.save(user);
            });
        };
    }
}
