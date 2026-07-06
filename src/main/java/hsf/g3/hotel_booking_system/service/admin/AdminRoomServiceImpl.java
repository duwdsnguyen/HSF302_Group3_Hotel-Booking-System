package hsf.g3.hotel_booking_system.service.admin;

import hsf.g3.hotel_booking_system.dto.admin.RoomRequestDTO;
import hsf.g3.hotel_booking_system.dto.admin.RoomResponseDTO;
import hsf.g3.hotel_booking_system.entity.room.Room;
import hsf.g3.hotel_booking_system.entity.room.RoomType;
import hsf.g3.hotel_booking_system.enums.room.RoomStatus;
import hsf.g3.hotel_booking_system.repository.admin.RoomRepository;
import hsf.g3.hotel_booking_system.repository.admin.RoomTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AdminRoomServiceImpl implements AdminRoomService {
    private final RoomRepository roomRepository;
    private final RoomTypeRepository roomTypeRepository;

    @Override
    public List<RoomResponseDTO> getAllRooms() {
        return roomRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    public List<RoomResponseDTO> getRooms(String search, RoomStatus status, Integer roomTypeId, String sort) {
        return roomRepository.findAll()
                .stream()
                .filter(room -> matchesSearch(room, search))
                .filter(room -> status == null || room.getStatus() == status)
                .filter(room -> roomTypeId == null || room.getRoomType().getRoomTypeId().equals(roomTypeId))
                .sorted(getComparator(sort))
                .map(this::toResponseDTO)
                .toList();
    }

    @Override
    public RoomResponseDTO getRoomById(Integer roomId) {
        Room room = findEntityById(roomId);
        return toResponseDTO(room);
    }

    @Override
    public RoomResponseDTO createRoom(RoomRequestDTO request) {
        rejectDuplicateRoomNumber(request.getRoomNumber(), null);

        Room room = new Room();
        room.setRoomNumber(request.getRoomNumber());
        room.setRoomType(findRoomTypeById(request.getRoomTypeId()));
        room.setFloorNumber(request.getFloorNumber());
        room.setStatus(toStatus(request.getStatus()));
        room.setDescription(request.getDescription());

        Room savedRoom = roomRepository.save(room);
        return toResponseDTO(savedRoom);
    }

    @Override
    public RoomResponseDTO updateRoom(Integer id, RoomRequestDTO request) {
        Room room = findEntityById(id);
        rejectDuplicateRoomNumber(request.getRoomNumber(), id);

        room.setRoomNumber(request.getRoomNumber());
        room.setRoomType(findRoomTypeById(request.getRoomTypeId()));
        room.setFloorNumber(request.getFloorNumber());
        room.setStatus(toStatus(request.getStatus()));
        room.setDescription(request.getDescription());

        Room savedRoom = roomRepository.save(room);
        return toResponseDTO(savedRoom);
    }

    @Override
    public void deleteRoom(Integer roomId) {
        roomRepository.deleteById(roomId);
    }

    private Room findEntityById(Integer roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room with id " + roomId + " does not exist"));
    }

    private RoomType findRoomTypeById(Integer roomTypeId) {
        return roomTypeRepository.findById(roomTypeId)
                .orElseThrow(() -> new RuntimeException("RoomType with id " + roomTypeId + " does not exist"));
    }

    private void rejectDuplicateRoomNumber(String roomNumber, Integer currentRoomId) {
        boolean duplicate = currentRoomId == null
                ? roomRepository.existsByRoomNumber(roomNumber)
                : roomRepository.existsByRoomNumberAndRoomIdNot(roomNumber, currentRoomId);
        if (duplicate) {
            throw new IllegalArgumentException("Room with number " + roomNumber + " already exists");
        }
    }

    private RoomResponseDTO toResponseDTO(Room room) {
        RoomResponseDTO response = new RoomResponseDTO();
        response.setRoomId(room.getRoomId());
        response.setRoomNumber(room.getRoomNumber());
        response.setRoomTypeId(room.getRoomType().getRoomTypeId());
        response.setRoomTypeName(room.getRoomType().getTypeName());
        response.setBasePrice(room.getRoomType().getBasePrice());
        response.setFloorNumber(room.getFloorNumber());
        response.setStatus(room.getStatus());
        response.setDescription(room.getDescription());

        return response;
    }

    private RoomStatus toStatus(RoomStatus status) {
        if (status == null) {
            return RoomStatus.AVAILABLE;
        }

        return status;
    }

    private boolean matchesSearch(Room room, String search) {
        if (search == null || search.isBlank()) {
            return true;
        }

        String keyword = search.toLowerCase(Locale.ROOT);
        return containsIgnoreCase(room.getRoomNumber(), keyword)
                || containsIgnoreCase(room.getDescription(), keyword)
                || containsIgnoreCase(room.getRoomType().getTypeName(), keyword);
    }

    private boolean containsIgnoreCase(String value, String keyword) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(keyword);
    }

    private Comparator<Room> getComparator(String sort) {
        if ("number_desc".equals(sort)) {
            return Comparator.comparing(Room::getRoomNumber, String.CASE_INSENSITIVE_ORDER).reversed();
        }

        if ("floor_asc".equals(sort)) {
            return Comparator.comparing(Room::getFloorNumber);
        }

        if ("floor_desc".equals(sort)) {
            return Comparator.comparing(Room::getFloorNumber).reversed();
        }

        if ("type_asc".equals(sort)) {
            return Comparator.comparing(room -> room.getRoomType().getTypeName(), String.CASE_INSENSITIVE_ORDER);
        }

        if ("type_desc".equals(sort)) {
            return Comparator.comparing((Room room) -> room.getRoomType().getTypeName(), String.CASE_INSENSITIVE_ORDER).reversed();
        }

        return Comparator.comparing(Room::getRoomNumber, String.CASE_INSENSITIVE_ORDER);
    }
}
