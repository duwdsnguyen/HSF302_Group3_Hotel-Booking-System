package hsf.g3.hotel_booking_system.service.service;

import hsf.g3.hotel_booking_system.entity.service.HotelService;
import hsf.g3.hotel_booking_system.enums.service.ServiceStatus;

import java.util.List;

public interface HotelServiceService {
    List<HotelService> getAllServices();

    List<HotelService> searchServices(String keyword, ServiceStatus status);

    HotelService getServiceById(Long serviceId);

    HotelService createService(HotelService hotelService);

    HotelService updateService(Long serviceId, HotelService hotelService);

    void activateService(Long serviceId);

    void deactivateService(Long serviceId);
}
