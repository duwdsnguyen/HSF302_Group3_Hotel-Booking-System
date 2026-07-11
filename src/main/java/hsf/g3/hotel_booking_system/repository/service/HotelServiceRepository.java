package hsf.g3.hotel_booking_system.repository.service;

import hsf.g3.hotel_booking_system.entity.service.HotelService;
import hsf.g3.hotel_booking_system.enums.service.ServiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("serviceHotelServiceRepository")
public interface HotelServiceRepository extends JpaRepository<HotelService, Long> {

    List<HotelService> findByServiceNameContainingIgnoreCase(String keyword);
    List<HotelService> findByStatus(ServiceStatus status);
    List<HotelService> findByServiceNameContainingIgnoreCaseAndStatus(String keyword, ServiceStatus status);
}
