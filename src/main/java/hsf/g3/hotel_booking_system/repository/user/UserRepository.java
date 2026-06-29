package hsf.g3.hotel_booking_system.repository.user;

import hsf.g3.hotel_booking_system.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.email= :email")
    Optional<User> findUserByEmailWithRoles(String email);

    Optional<User> findUserByEmail(String email);

    Boolean existsUserByEmail(String email);

    @Query("SELECT u FROM User u JOIN u.resetTokens rt WHERE rt.token= :resetToken ")
    Optional<User> findUserByResetToken(String resetToken);
}
