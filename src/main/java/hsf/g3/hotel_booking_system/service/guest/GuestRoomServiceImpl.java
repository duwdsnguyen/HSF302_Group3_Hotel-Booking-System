package hsf.g3.hotel_booking_system.service.guest;

import hsf.g3.hotel_booking_system.dto.guest.room.request.RoomChangeRequest;
import hsf.g3.hotel_booking_system.dto.guest.room.response.RoomChangeHistoryDTO;
import hsf.g3.hotel_booking_system.dto.guest.room.response.RoomDTO;
import hsf.g3.hotel_booking_system.dto.guest.room.response.RoomResponse;
import hsf.g3.hotel_booking_system.dto.user.UserInfoDTO;
import hsf.g3.hotel_booking_system.entity.guest.Booking;
import hsf.g3.hotel_booking_system.entity.guest.BookingHistory;
import hsf.g3.hotel_booking_system.entity.room.Room;
import hsf.g3.hotel_booking_system.entity.room.RoomType;
import hsf.g3.hotel_booking_system.entity.user.User;
import hsf.g3.hotel_booking_system.enums.room.BookingAction;
import hsf.g3.hotel_booking_system.enums.room.BookingStatus;
import hsf.g3.hotel_booking_system.exception.ResourceNotFoundException;
import hsf.g3.hotel_booking_system.enums.room.RoomStatus;
import hsf.g3.hotel_booking_system.repository.admin.RoomRepository;
import hsf.g3.hotel_booking_system.repository.admin.RoomTypeRepository;
import hsf.g3.hotel_booking_system.repository.guest.BookingHistoryRepository;
import hsf.g3.hotel_booking_system.repository.guest.BookingRepository;
import hsf.g3.hotel_booking_system.repository.user.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class GuestRoomServiceImpl implements GuestRoomService {

    private final RoomRepository roomRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final BookingHistoryRepository bookingHistoryRepository;

    public GuestRoomServiceImpl(RoomRepository roomRepository,RoomTypeRepository roomTypeRepository,BookingRepository bookingRepository,UserRepository userRepository,ModelMapper modelMapper,BookingHistoryRepository bookingHistoryRepository) {
        this.roomRepository = roomRepository;
        this.roomTypeRepository = roomTypeRepository;
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.bookingHistoryRepository = bookingHistoryRepository;
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
    @Transactional(readOnly = true)
    public RoomResponse getAllAvailableRoom(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        String sortPath = "typeName".equals(sortBy) ? "roomType.typeName" : sortBy;
        Sort sortByAnyOrder = sortOrder.equalsIgnoreCase("asc")?Sort.by(sortPath).ascending():Sort.by(sortPath).descending();

        Pageable pageDetails = PageRequest.of(pageNumber,pageSize,sortByAnyOrder);

        Page<Room> roomPages = roomRepository.getAllAvailableRoom(pageDetails);

        List<Room> rooms = roomPages.getContent();

        List<RoomDTO> roomDTOs = rooms.stream().map(this::toRoomDTO).toList();
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
    @Transactional(readOnly = true)
    public List<RoomDTO> getCheckedInRooms(UserInfoDTO userInfoDTO) {
        if (userInfoDTO == null || userInfoDTO.getUserId() == null) {
            return List.of();
        }

        return bookingRepository.getCheckedInRooms(userInfoDTO.getUserId())
                .stream()
                .map(this::toRoomDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomChangeHistoryDTO> getRoomChangeHistory(UserInfoDTO userInfoDTO) {
        if (userInfoDTO == null || userInfoDTO.getUserId() == null) {
            return List.of();
        }

        return bookingHistoryRepository
                .findRoomChangeHistoryPerformedByUser(userInfoDTO.getUserId(), BookingAction.CHANGE_ROOM)
                .stream()
                .map(history -> RoomChangeHistoryDTO.builder()
                        .bookingHistoryId(history.getBookingHistoryId())
                        .bookingId(history.getBooking().getId())
                        .oldRoomNumber(history.getOldRoom().getRoomNumber())
                        .newRoomNumber(history.getNewRoom().getRoomNumber())
                        .changedAt(history.getChangedAt())
                        .description(history.getDescription())
                        .build())
                .toList();
    }

    @Transactional
    @Override
    public boolean changeRoom(RoomChangeRequest roomChangeRequest, UserInfoDTO userInfoDTO) {
        if (userInfoDTO == null || userInfoDTO.getUserId() == null) {
            return false;
        }

        if (roomChangeRequest == null
                || roomChangeRequest.getOldRoomId() == null
                || roomChangeRequest.getNewRoomId() == null) {
            throw new IllegalArgumentException("Please select your current room and the new room.");
        }

        if (roomChangeRequest.getOldRoomId().equals(roomChangeRequest.getNewRoomId())) {
            throw new IllegalArgumentException("The new room must be different from your current room.");
        }

        Booking booking = bookingRepository
                .getCheckedInBooking(userInfoDTO.getUserId(), roomChangeRequest.getOldRoomId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "The selected current room is not checked in under your account."));

        Room oldRoom = booking.getRoom();
        Room newRoom = roomRepository.findRoomByRoomIdForUpdate(roomChangeRequest.getNewRoomId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Room", "roomId", roomChangeRequest.getNewRoomId()));

        if (newRoom.getStatus() != RoomStatus.AVAILABLE) {
            throw new IllegalArgumentException("This room is no longer available. Please choose another room.");
        }

        if (newRoom.getRoomType().getMaxGuests() < booking.getNumberOfGuests()) {
            throw new IllegalArgumentException("This room does not have enough capacity for your booking.");
        }

        LocalDate changeDate = LocalDate.now();
        if (!booking.getCheckOutDate().isAfter(changeDate)) {
            throw new IllegalArgumentException("This booking is no longer eligible for a room change.");
        }

        boolean hasBlockingBooking = bookingRepository.existsBlockingBooking(
                newRoom.getRoomId(),
                changeDate,
                booking.getCheckOutDate(),
                Set.of(BookingStatus.PENDING, BookingStatus.CONFIRMED, BookingStatus.CHECKED_IN));
        if (hasBlockingBooking) {
            throw new IllegalArgumentException("This room is reserved during the remainder of your stay.");
        }

        BookingStatus bookingStatus = booking.getStatus();
        oldRoom.setStatus(RoomStatus.AVAILABLE);
        newRoom.setStatus(RoomStatus.OCCUPIED);
        booking.setRoom(newRoom);

        BookingHistory bookingHistory = BookingHistory.builder()
                .booking(booking)
                .changedBy(booking.getCustomer())
                .oldStatus(bookingStatus)
                .newStatus(bookingStatus)
                .action(BookingAction.CHANGE_ROOM)
                .oldRoom(oldRoom)
                .newRoom(newRoom)
                .description("Room " + oldRoom.getRoomNumber() + " changed to room " + newRoom.getRoomNumber() + ".")
                .changedAt(LocalDateTime.now())
                .build();

        roomRepository.save(oldRoom);
        roomRepository.save(newRoom);
        bookingRepository.save(booking);
        bookingHistoryRepository.save(bookingHistory);
        return true;
    }

    private RoomDTO toRoomDTO(Room room) {
        RoomDTO roomDTO = modelMapper.map(room, RoomDTO.class);
        if (room.getRoomType() != null) {
            roomDTO.setTypeName(room.getRoomType().getTypeName());
        }
        return roomDTO;
    }
}
