package hsf.g3.hotel_booking_system.service.guest;

import hsf.g3.hotel_booking_system.dto.guest.room.request.RoomChangeRequest;
import hsf.g3.hotel_booking_system.dto.guest.room.response.RoomDTO;
import hsf.g3.hotel_booking_system.dto.guest.room.response.RoomResponse;
import hsf.g3.hotel_booking_system.dto.user.UserInfoDTO;
import hsf.g3.hotel_booking_system.entity.guest.Booking;
import hsf.g3.hotel_booking_system.entity.room.Room;
import hsf.g3.hotel_booking_system.entity.room.RoomType;
import hsf.g3.hotel_booking_system.entity.user.User;
import hsf.g3.hotel_booking_system.enums.room.BookingStatus;
import hsf.g3.hotel_booking_system.exception.ResourceNotFoundException;
import hsf.g3.hotel_booking_system.enums.room.RoomStatus;
import hsf.g3.hotel_booking_system.repository.admin.RoomRepository;
import hsf.g3.hotel_booking_system.repository.admin.RoomTypeRepository;
import hsf.g3.hotel_booking_system.repository.guest.BookingRepository;
import hsf.g3.hotel_booking_system.repository.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class GuestRoomServiceImpl implements GuestRoomService {

    private final RoomRepository roomRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;

    public GuestRoomServiceImpl(RoomRepository roomRepository,RoomTypeRepository roomTypeRepository,BookingRepository bookingRepository,UserRepository userRepository) {
        this.roomRepository = roomRepository;
        this.roomTypeRepository = roomTypeRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Room> searchAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate, Integer numberOfGuests, BigDecimal minPrice, BigDecimal maxPrice, Integer roomTypeId) {
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

        List<Room> availableRooms = roomRepository.findAvailableRooms(
                checkInDate,
                checkOutDate,
                numberOfGuests,
                RoomStatus.AVAILABLE,
                List.of("PENDING", "CONFIRMED")
        );

        if (minPrice != null) {
            availableRooms = availableRooms.stream()
                    .filter(room -> room.getRoomType().getBasePrice().compareTo(minPrice) >= 0)
                    .toList();
        }

        if (maxPrice != null) {
            availableRooms = availableRooms.stream()
                    .filter(room -> room.getRoomType().getBasePrice().compareTo(maxPrice) <= 0)
                    .toList();
        }

        if (roomTypeId != null) {
            availableRooms = availableRooms.stream()
                    .filter(room -> room.getRoomType().getRoomTypeId().equals(roomTypeId))
                    .toList();
        }

        return availableRooms;
    }

    @Override
    public Room getRoomById(Integer roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found with ID: " + roomId));
    }

    @Override
    public List<RoomType> getAllRoomTypes() {
        return roomTypeRepository.findAll();
    }


    @Override
    public List<Room> searchAvailableRooms(LocalDate checkInDate, LocalDate checkOutDate, Integer numberOfGuests) {
        return List.of();
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
    public boolean changeRoom(RoomChangeRequest roomChangeRequest, UserInfoDTO userInfoDTO) {
        if(userInfoDTO == null){
            return false;
        }
        User user = userRepository.findUserByEmail(userInfoDTO.getEmail()).orElseThrow(() -> new ResourceNotFoundException("User","email",userInfoDTO.getEmail()));

        Room oldRoom = user.getBookings().stream().map(Booking::getRoom).findFirst().orElseThrow(() -> new ResourceNotFoundException("Room","userId", user.getUserId()));
        Booking booking = bookingRepository.getCheckedInBooking(userInfoDTO.getUserId(), oldRoom.getRoomId()).orElseThrow(() -> new ResourceNotFoundException("Booking","userId and roomId", userInfoDTO.getUserId()+" "+oldRoom.getRoomId()));
        Room newRoom = roomRepository.findRoomByRoomId(roomChangeRequest.getNewRoomId()).orElseThrow(() -> new ResourceNotFoundException("Room","roomId",roomChangeRequest.getNewRoomId()));
        oldRoom.setStatus(RoomStatus.AVAILABLE);
        newRoom.setStatus(RoomStatus.OCCUPIED);
        booking.setRoom(newRoom);
        booking.setStatus(BookingStatus.PENDING);
        roomRepository.save(oldRoom);
        roomRepository.save(newRoom);
        bookingRepository.save(booking);
        return true;
    }
}
