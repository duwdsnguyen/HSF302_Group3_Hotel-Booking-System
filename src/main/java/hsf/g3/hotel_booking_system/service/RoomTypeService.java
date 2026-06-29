package hsf.g3.hotel_booking_system.service;


import hsf.g3.hotel_booking_system.entity.RoomType;
import hsf.g3.hotel_booking_system.repository.RoomTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomTypeService {
    private final RoomTypeRepository roomTypeRepository;

    public List<RoomType> getAllRoomTypes() {
        return roomTypeRepository.findAll();
    }

    public RoomType getRoomTypeById(int roomTypeId) {
        return roomTypeRepository.findById(roomTypeId).orElseThrow(() -> new RuntimeException("RoomType with id " + roomTypeId + " does not exist"));
    }

    public RoomType createRoomType(RoomType roomType) {
        if(roomTypeRepository.existsByTypeName(roomType.getTypeName())) {
            throw new RuntimeException("RoomType with name " + roomType.getTypeName() + " already exists");
        }

        roomType.setStatus("ACTIVE");
        return roomTypeRepository.save(roomType);
    }

    public RoomType updateRoomType(Integer id, RoomType updatedRoomType) {
        RoomType roomType = getRoomTypeById(id);
        roomType.setTypeName(updatedRoomType.getTypeName());
        roomType.setDescription(updatedRoomType.getDescription());
        roomType.setBasePrice(updatedRoomType.getBasePrice());
        roomType.setMaxGuests(updatedRoomType.getMaxGuests());
        roomType.setStatus(updatedRoomType.getStatus());

        return roomTypeRepository.save(roomType);
    }

    public void deleteRoomType(int roomTypeId) {
        roomTypeRepository.deleteById(roomTypeId);
    }





}
