package hsf.g3.hotel_booking_system.repository.admin;

import hsf.g3.hotel_booking_system.entity.service.HotelService;
import hsf.g3.hotel_booking_system.enums.service.ServiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("adminHotelServiceRepository")
public interface HotelServiceRepository extends JpaRepository<HotelService, Long> {
    List<HotelService> findByStatus(ServiceStatus status);
}
