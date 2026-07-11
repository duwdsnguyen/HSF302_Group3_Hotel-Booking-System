package hsf.g3.hotel_booking_system.service.guest;

import hsf.g3.hotel_booking_system.dto.guest.room.request.RoomChangeRequest;
import hsf.g3.hotel_booking_system.dto.guest.room.response.RoomDTO;
import hsf.g3.hotel_booking_system.dto.guest.room.response.RoomResponse;
import hsf.g3.hotel_booking_system.entity.room.Room;
import hsf.g3.hotel_booking_system.enums.user.RoomStatus;
import hsf.g3.hotel_booking_system.exception.ResourceNotFoundException;
import hsf.g3.hotel_booking_system.repository.admin.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class GuestRoomServiceImpl implements GuestRoomService {
    private final RoomRepository roomRepository;

    public GuestRoomServiceImpl(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @Override
    public List<Room> searchAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate, Integer numberOfGuests) {
        if (checkInDate == null || checkOutDate == null) {
            throw new IllegalArgumentException("Please select both check-in and check-out dates.");
        }

        if (checkInDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Check-in date cannot be in the past.");
        }

        if (!checkOutDate.isAfter(checkInDate)) {
            throw new IllegalArgumentException("Check-out date must be after check-in date.");
        }

        if (numberOfGuests == null || numberOfGuests <= 0) {
            throw new IllegalArgumentException("Number of guests must be greater than 0.");
        }

        return roomRepository.findAvailableRooms(
                checkInDate,
                checkOutDate,
                numberOfGuests,
                RoomStatus.AVAILABLE,
                List.of("PENDING", "CONFIRMED"));
    }

    @Override
    public RoomResponse getAllAvailableRoom(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        String sortPath = "typeName".equals(sortBy) ? "roomType.typeName" : sortBy;
        Sort sortByAnyOrder = sortOrder.equalsIgnoreCase("asc")?Sort.by(sortPath).ascending():Sort.by(sortPath).descending();

        Pageable pageDetails = PageRequest.of(pageNumber,pageSize,sortByAnyOrder);

        Page<Room> roomPages = roomRepository.getAllAvailableRoom(pageDetails);

        List<Room> rooms = roomPages.getContent();

        List<RoomDTO> roomDTOs = rooms.stream().map(room ->{
            RoomDTO roomDTO = new RoomDTO();
            roomDTO.setRoomId(room.getRoomId());
            roomDTO.setRoomNumber(room.getRoomNumber());
            roomDTO.setFloorNumber(room.getFloorNumber());
            roomDTO.setStatus(room.getStatus());
            roomDTO.setDescription(room.getDescription());
            roomDTO.setTypeName(room.getRoomType().getTypeName());
            return roomDTO;
        }).toList();

        RoomResponse roomResponse = new RoomResponse();
        roomResponse.setContent(roomDTOs);
        roomResponse.setPageNumber(roomPages.getNumber());
        roomResponse.setPageSize(roomPages.getSize());
        roomResponse.setTotalPages(roomPages.getTotalPages());
        roomResponse.setTotalElements(roomPages.getTotalElements());
        roomResponse.setLastPage(roomPages.isLast());
        return roomResponse;
    }

    @Override
    public boolean changeRoom(RoomChangeRequest roomChangeRequest) {
        Room room = roomRepository.findRoomByRoomId(roomChangeRequest.getRoomId()).orElseThrow(() -> new ResourceNotFoundException("Room","roomId",roomChangeRequest.getRoomId()));

        roomRepository.save(room);
        return true;
    }
}
