package hsf.g3.hotel_booking_system.service.services;

import hsf.g3.hotel_booking_system.entity.service.HotelService;
import hsf.g3.hotel_booking_system.enums.service.ServiceStatus;
import hsf.g3.hotel_booking_system.exception.AppException;
import hsf.g3.hotel_booking_system.exception.ErrorCode;
import org.springframework.stereotype.Service;
import hsf.g3.hotel_booking_system.repository.service.HotelServiceRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
public class HotelServiceServiceImpl implements HotelServiceService {
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
        String normalizedKeyword = (keyword == null) ? "" : keyword.trim();

        boolean hasKeyword = !normalizedKeyword.isEmpty();
        boolean hasStatus = status != null;

        if (hasKeyword && hasStatus) {
            return hotelServiceRepository.findByServiceNameContainingIgnoreCaseAndStatus(normalizedKeyword, status);
        }

        if (hasKeyword) {
            return hotelServiceRepository.findByServiceNameContainingIgnoreCase(normalizedKeyword);
        }

        if (hasStatus) {
            return hotelServiceRepository.findByStatus(status);
        }
        return hotelServiceRepository.findAll();
    }

    @Override
    public HotelService getServiceById(Long serviceId) {
        return hotelServiceRepository.findById(serviceId)
                .orElseThrow(() -> new AppException(ErrorCode.SERVICE_NOT_FOUND));
    }

    @Override
    public HotelService createService(HotelService hotelService) {
        if (hotelService == null) {
            throw new AppException(ErrorCode.SERVICE_DATA_NULL);
        }

        if (hotelService.getServiceName() == null || hotelService.getServiceName().trim().isEmpty()) {
            throw new AppException(ErrorCode.SERVICE_NAME_REQUIRED);
        }

        if (hotelService.getPrice() == null) {
            throw new AppException(ErrorCode.SERVICE_PRICE_REQUIRED);
        }

        if (hotelService.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new AppException(ErrorCode.SERVICE_PRICE_INVALID);
        }

        if (hotelService.getStatus() == null) {
            hotelService.setStatus(ServiceStatus.ACTIVE);
        }

        String normalized = hotelService.getServiceName().trim();
        hotelService.setServiceName(normalized);

        if (hotelServiceRepository.existsByServiceNameIgnoreCase(normalized)) {
            throw new AppException(ErrorCode.SERVICE_NAME_DUPLICATE);
        }

        return hotelServiceRepository.save(hotelService);
    }

    @Override
    public HotelService updateService(Long serviceId, HotelService hotelService) {
        if (hotelService == null) {
            throw new AppException(ErrorCode.SERVICE_DATA_NULL);
        }

        if (hotelService.getServiceName() == null || hotelService.getServiceName().trim().isEmpty()) {
            throw new AppException(ErrorCode.SERVICE_NAME_REQUIRED);
        }

        if (hotelService.getPrice() == null) {
            throw new AppException(ErrorCode.SERVICE_PRICE_REQUIRED);
        }

        if (hotelService.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new AppException(ErrorCode.SERVICE_PRICE_INVALID);
        }

        HotelService existingService = getServiceById(serviceId);

        String normalized = hotelService.getServiceName().trim();

        if (hotelServiceRepository.existsByServiceNameIgnoreCaseAndServiceIdNot(normalized, serviceId)) {
            throw new AppException(ErrorCode.SERVICE_NAME_DUPLICATE);
        }

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
