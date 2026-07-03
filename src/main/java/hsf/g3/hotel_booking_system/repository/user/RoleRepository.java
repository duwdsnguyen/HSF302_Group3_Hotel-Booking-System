package hsf.g3.hotel_booking_system.repository.user;

import hsf.g3.hotel_booking_system.entity.user.Role;
import hsf.g3.hotel_booking_system.enums.user.AppRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role,Long> {
    Optional<Role> findRoleByRoleCode(AppRole roleCode);
}
