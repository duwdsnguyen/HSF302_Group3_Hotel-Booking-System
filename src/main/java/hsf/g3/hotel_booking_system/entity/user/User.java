package hsf.g3.hotel_booking_system.entity.user;


import hsf.g3.hotel_booking_system.entity.Base;
import hsf.g3.hotel_booking_system.enums.user.UserStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.Nationalized;
import java.util.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@Builder
public class User extends Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    Long userId;

    @Column(name = "full_name",columnDefinition = "NVARCHAR(255)")
    @Nationalized
    String fullName;

    @Column(unique = true,nullable = false)
    String email;

    @Column(unique = true,nullable = false)
    String password;

    String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "status",columnDefinition = "VARCHAR(25)")
    UserStatus status = UserStatus.ACTIVE;

    @ToString.Exclude
    @ManyToMany(cascade = {CascadeType.MERGE,CascadeType.DETACH})
    @JoinTable(name = "user_roles",joinColumns = @JoinColumn(name = "user_id"),inverseJoinColumns = @JoinColumn(name = "role_id"))
    Set<Role>roles = new HashSet<>();

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL,orphanRemoval = true)
    List<ResetToken> resetTokens = new ArrayList<>();



}
