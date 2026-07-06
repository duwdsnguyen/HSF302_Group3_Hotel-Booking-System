package hsf.g3.hotel_booking_system.service.service;

import hsf.g3.hotel_booking_system.entity.service.HotelService;
import hsf.g3.hotel_booking_system.enums.service.ServiceStatus;
import org.springframework.stereotype.Service;
import hsf.g3.hotel_booking_system.repository.service.HotelServiceRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class HotelServiceServiceImpl implements HotelServiceService{
    private final HotelServiceRepository hotelServiceRepository;

    public HotelServiceServiceImpl(HotelServiceRepository hotelServiceRepository) {
        this.hotelServiceRepository = hotelServiceRepository;
    }

    @Override
    public List<HotelService> getAllServices() {
        return hotelServiceRepository.findAll();
    }

    @Override
    public List<HotelService> searchServices(String keyword, ServiceStatus status) {
        String normalizedKeyword = (keyword == null ) ? "" : keyword.trim();

        boolean hasKeyword = !normalizedKeyword.isEmpty();
        boolean hasStatus = status != null;

        if(hasKeyword && hasStatus) {
            return hotelServiceRepository.findByServiceNameContainingIgnoreCaseAndStatus(normalizedKeyword, status);
        }

        if(hasKeyword) {
            return hotelServiceRepository.findByServiceNameContainingIgnoreCase(normalizedKeyword);
        }

        if(hasStatus) {
            return hotelServiceRepository.findByStatus(status);
        }
        return hotelServiceRepository.findAll();
    }

    @Override
    public HotelService getServiceById(Long serviceId) {
        Optional<HotelService> optionalService = hotelServiceRepository.findById(serviceId);

        if(optionalService.isPresent()) {
            return optionalService.get();
        }

        throw new RuntimeException("Service not found with id: " + serviceId);
    }

    @Override
    public HotelService createService(HotelService hotelService) {
        if(hotelService == null){
            throw new RuntimeException("Service data must not be null");
        }

        if(hotelService.getServiceName() == null || hotelService.getServiceName().trim().isEmpty()){
            throw new RuntimeException("Service name must not be blank");
        }

        if(hotelService.getPrice() == null){
            throw new RuntimeException("Service price must not be null");
        }

        if(hotelService.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Service price must not be negative");
        }

        if(hotelService.getStatus() == null) {
            hotelService.setStatus(ServiceStatus.ACTIVE);
        }

        hotelService.setServiceName(hotelService.getServiceName().trim());
        return hotelServiceRepository.save(hotelService);
    }

    @Override
    public HotelService updateService(Long serviceId, HotelService hotelService) {
        if(hotelService == null){
            throw new RuntimeException("Service data must not be null");
        }

        if(hotelService.getServiceName() == null || hotelService.getServiceName().trim().isEmpty()){
            throw new RuntimeException("Service name must not be blank");
        }

        if(hotelService.getPrice() == null){
            throw new RuntimeException("Service price must not be null");
        }

        if(hotelService.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Service price must not be negative");
        }

        HotelService existingService = getServiceById(serviceId);

        existingService.setServiceName(hotelService.getServiceName().trim());
        existingService.setDescription(hotelService.getDescription());
        existingService.setPrice(hotelService.getPrice());

        if (hotelService.getStatus() != null) {
            existingService.setStatus(hotelService.getStatus());
        }

        return hotelServiceRepository.save(existingService);
    }

    @Override
    public void activateService(Long serviceId) {
        HotelService existingService = getServiceById(serviceId);
        existingService.setStatus(ServiceStatus.ACTIVE);
        hotelServiceRepository.save(existingService);
    }

    @Override
    public void deactivateService(Long serviceId) {
        HotelService existingService = getServiceById(serviceId);
        existingService.setStatus(ServiceStatus.INACTIVE);
        hotelServiceRepository.save(existingService);
    }
}
