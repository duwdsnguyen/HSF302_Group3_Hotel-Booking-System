package hsf.g3.hotel_booking_system.repository.user;

import hsf.g3.hotel_booking_system.entity.user.ResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ResetTokenRepository extends JpaRepository<ResetToken, Long> {

    Boolean existsByTokenAndUsedFalseAndExpiredAtAfter(String token, LocalDateTime expiredAtAfter);

    Optional<ResetToken> findByToken(String token);

}
