package hsf.g3.hotel_booking_system.service.room;

import hsf.g3.hotel_booking_system.dto.room.RoomTypeRequestDTO;
import hsf.g3.hotel_booking_system.dto.room.RoomTypeResponseDTO;
import hsf.g3.hotel_booking_system.entity.room.RoomType;
import hsf.g3.hotel_booking_system.enums.user.RoomTypeStatus;
import hsf.g3.hotel_booking_system.repository.room.RoomTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class RoomTypeServiceImpl implements RoomTypeService {
    private final RoomTypeRepository roomTypeRepository;

    @Override
    public List<RoomTypeResponseDTO> getAllRoomTypes() {
        return roomTypeRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    public List<RoomTypeResponseDTO> getRoomTypes(String search, RoomTypeStatus status, String sort) {
        return roomTypeRepository.findAll()
                .stream()
                .filter(roomType -> matchesSearch(roomType, search))
                .filter(roomType -> status == null || roomType.getStatus() == status)
                .sorted(getComparator(sort))
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    public RoomTypeResponseDTO getRoomTypeById(Integer roomTypeId) {
        RoomType roomType = findEntityById(roomTypeId);
        return toResponseDTO(roomType);
    }

    @Override
    public RoomTypeResponseDTO createRoomType(RoomTypeRequestDTO request) {
        if (roomTypeRepository.existsByTypeName(request.getTypeName())) {
            throw new RuntimeException("RoomType with name " + request.getTypeName() + " already exists");
        }

        RoomType roomType = new RoomType();
        roomType.setTypeName(request.getTypeName());
        roomType.setDescription(request.getDescription());
        roomType.setBasePrice(request.getBasePrice());
        roomType.setMaxGuests(request.getMaxGuests());
        roomType.setStatus(toStatus(request.getStatus()));

        RoomType savedRoomType = roomTypeRepository.save(roomType);
        return toResponseDTO(savedRoomType);
    }

    @Override
    public RoomTypeResponseDTO updateRoomType(Integer id, RoomTypeRequestDTO request) {
        RoomType roomType = findEntityById(id);
        roomType.setTypeName(request.getTypeName());
        roomType.setDescription(request.getDescription());
        roomType.setBasePrice(request.getBasePrice());
        roomType.setMaxGuests(request.getMaxGuests());
        roomType.setStatus(toStatus(request.getStatus()));

        RoomType savedRoomType = roomTypeRepository.save(roomType);
        return toResponseDTO(savedRoomType);
    }

    @Override
    public void deleteRoomType(int roomTypeId) {
        roomTypeRepository.deleteById(roomTypeId);
    }

    private RoomType findEntityById(Integer roomTypeId) {
        return roomTypeRepository.findById(roomTypeId)
                .orElseThrow(() -> new RuntimeException("RoomType with id " + roomTypeId + " does not exist"));
    }

    private RoomTypeResponseDTO toResponseDTO(RoomType roomType) {
        RoomTypeResponseDTO response = new RoomTypeResponseDTO();
        response.setRoomTypeId(roomType.getRoomTypeId());
        response.setTypeName(roomType.getTypeName());
        response.setDescription(roomType.getDescription());
        response.setBasePrice(roomType.getBasePrice());
        response.setMaxGuests(roomType.getMaxGuests());
        response.setStatus(roomType.getStatus());

        return response;
    }

    private RoomTypeStatus toStatus(RoomTypeStatus status) {
        if (status == null) {
            return RoomTypeStatus.ACTIVE;
        }

        return status;
    }

    private boolean matchesSearch(RoomType roomType, String search) {
        if (search == null || search.isBlank()) {
            return true;
        }

        String keyword = search.toLowerCase(Locale.ROOT);
        return containsIgnoreCase(roomType.getTypeName(), keyword)
                || containsIgnoreCase(roomType.getDescription(), keyword);
    }

    private boolean containsIgnoreCase(String value, String keyword) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(keyword);
    }

    private Comparator<RoomType> getComparator(String sort) {
        if ("name_desc".equals(sort)) {
            return Comparator.comparing(RoomType::getTypeName, String.CASE_INSENSITIVE_ORDER).reversed();
        }

        if ("price_asc".equals(sort)) {
            return Comparator.comparing(RoomType::getBasePrice);
        }

        if ("price_desc".equals(sort)) {
            return Comparator.comparing(RoomType::getBasePrice).reversed();
        }

        if ("guests_asc".equals(sort)) {
            return Comparator.comparing(RoomType::getMaxGuests);
        }

        if ("guests_desc".equals(sort)) {
            return Comparator.comparing(RoomType::getMaxGuests).reversed();
        }

        return Comparator.comparing(RoomType::getTypeName, String.CASE_INSENSITIVE_ORDER);
    }
}
