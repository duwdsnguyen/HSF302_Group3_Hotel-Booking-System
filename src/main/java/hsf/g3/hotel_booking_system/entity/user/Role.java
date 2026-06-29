package hsf.g3.hotel_booking_system.entity.user;

import hsf.g3.hotel_booking_system.enums.user.AppRole;
import hsf.g3.hotel_booking_system.enums.user.RoleStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Nationalized;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "roles")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    Long roleId;

    @Enumerated(EnumType.STRING)
    @Column(name = "role_code",unique = true,nullable = false,columnDefinition = "VARCHAR(50)")
    AppRole roleCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "status",columnDefinition = "VARCHAR(50)")
    RoleStatus status = RoleStatus.ACTIVE;

    @Column(name = "created_at")
    @CreationTimestamp
    LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    LocalDateTime updatedAt;

    @ToString.Exclude
    @ManyToMany(mappedBy = "roles")
    Set<User> users = new HashSet<>();


    public Role(AppRole roleCode) {
        this.roleCode = roleCode;
    }
}
