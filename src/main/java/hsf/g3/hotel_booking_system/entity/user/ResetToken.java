package hsf.g3.hotel_booking_system.entity.user;

import hsf.g3.hotel_booking_system.entity.Base;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "reset_token")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ResetToken extends Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false,unique = true)
    String token;

    String email;

    @Column(name = "expired_at")
    LocalDateTime expiredAt;

    @Column(name = "is_used",columnDefinition = "BIT DEFAULT 0")
    Boolean used=Boolean.FALSE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    User user;
}
