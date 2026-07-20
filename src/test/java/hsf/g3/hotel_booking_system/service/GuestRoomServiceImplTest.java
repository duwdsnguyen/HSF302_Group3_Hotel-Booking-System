package hsf.g3.hotel_booking_system.service;

import hsf.g3.hotel_booking_system.dto.guest.room.request.RoomChangeRequest;
import hsf.g3.hotel_booking_system.dto.user.UserInfoDTO;
import hsf.g3.hotel_booking_system.entity.guest.Booking;
import hsf.g3.hotel_booking_system.entity.guest.BookingHistory;
import hsf.g3.hotel_booking_system.entity.room.Room;
import hsf.g3.hotel_booking_system.entity.room.RoomType;
import hsf.g3.hotel_booking_system.entity.user.User;
import hsf.g3.hotel_booking_system.enums.room.BookingAction;
import hsf.g3.hotel_booking_system.enums.room.BookingStatus;
import hsf.g3.hotel_booking_system.enums.room.RoomStatus;
import hsf.g3.hotel_booking_system.repository.admin.RoomRepository;
import hsf.g3.hotel_booking_system.repository.admin.RoomTypeRepository;
import hsf.g3.hotel_booking_system.repository.guest.BookingHistoryRepository;
import hsf.g3.hotel_booking_system.repository.guest.BookingRepository;
import hsf.g3.hotel_booking_system.repository.user.UserRepository;
import hsf.g3.hotel_booking_system.service.guest.GuestRoomServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Guest room change service")
class GuestRoomServiceImplTest {

    private static final Long USER_ID = 7L;
    private static final Integer OLD_ROOM_ID = 101;
    private static final Integer NEW_ROOM_ID = 202;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private RoomTypeRepository roomTypeRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private BookingHistoryRepository bookingHistoryRepository;

    @InjectMocks
    private GuestRoomServiceImpl guestRoomService;

    private UserInfoDTO loggedInUser;
    private User user;
    private Room oldRoom;
    private Room newRoom;
    private Booking booking;
    private RoomChangeRequest request;

    @BeforeEach
    void setUp() {
        loggedInUser = new UserInfoDTO();
        loggedInUser.setUserId(USER_ID);

        user = new User();
        user.setUserId(USER_ID);

        oldRoom = room(OLD_ROOM_ID, "101", RoomStatus.OCCUPIED);
        newRoom = room(NEW_ROOM_ID, "202", RoomStatus.AVAILABLE);
        RoomType targetRoomType = new RoomType();
        targetRoomType.setMaxGuests(4);
        newRoom.setRoomType(targetRoomType);

        booking = new Booking();
        booking.setId(15);
        booking.setCustomer(user);
        booking.setRoom(oldRoom);
        booking.setStatus(BookingStatus.CHECKED_IN);
        booking.setNumberOfGuests(2);
        booking.setCheckOutDate(LocalDate.now().plusDays(2));

        request = new RoomChangeRequest();
        request.setOldRoomId(OLD_ROOM_ID);
        request.setNewRoomId(NEW_ROOM_ID);
    }

    @Test
    @DisplayName("Changes an active booking and records the exact room-change history")
    void changeRoom_success_keepsBookingStatusAndRecordsHistory() {
        stubAuthenticatedActiveBooking();
        when(roomRepository.findRoomByRoomIdForUpdate(NEW_ROOM_ID)).thenReturn(Optional.of(newRoom));

        boolean changed = guestRoomService.changeRoom(request, loggedInUser);

        assertTrue(changed);
        assertEquals(RoomStatus.AVAILABLE, oldRoom.getStatus());
        assertEquals(RoomStatus.OCCUPIED, newRoom.getStatus());
        assertSame(newRoom, booking.getRoom());
        assertEquals(BookingStatus.CHECKED_IN, booking.getStatus());

        ArgumentCaptor<BookingHistory> historyCaptor = ArgumentCaptor.forClass(BookingHistory.class);
        verify(bookingHistoryRepository).save(historyCaptor.capture());

        BookingHistory history = historyCaptor.getValue();
        assertSame(booking, history.getBooking());
        assertSame(user, history.getChangedBy());
        assertSame(oldRoom, history.getOldRoom());
        assertSame(newRoom, history.getNewRoom());
        assertEquals(BookingAction.CHANGE_ROOM, history.getAction());
        assertNotNull(history.getChangedAt());

        verify(bookingRepository).getCheckedInBooking(USER_ID, OLD_ROOM_ID);
        verify(roomRepository).findRoomByRoomIdForUpdate(NEW_ROOM_ID);
    }

    @Test
    @DisplayName("Rejects a target room that is no longer available")
    void changeRoom_targetNotAvailable_rejectsWithoutMutationOrHistory() {
        stubAuthenticatedActiveBooking();
        newRoom.setStatus(RoomStatus.OCCUPIED);
        when(roomRepository.findRoomByRoomIdForUpdate(NEW_ROOM_ID)).thenReturn(Optional.of(newRoom));

        assertThrows(IllegalArgumentException.class,
                () -> guestRoomService.changeRoom(request, loggedInUser));

        assertEquals(RoomStatus.OCCUPIED, oldRoom.getStatus());
        assertEquals(RoomStatus.OCCUPIED, newRoom.getStatus());
        assertSame(oldRoom, booking.getRoom());
        assertEquals(BookingStatus.CHECKED_IN, booking.getStatus());
        verify(bookingHistoryRepository, never()).save(any(BookingHistory.class));
        verify(roomRepository, never()).save(any(Room.class));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    @DisplayName("Rejects changing a booking to its current room")
    void changeRoom_sameRoom_rejectsWithoutRepositoryWrites() {
        request.setNewRoomId(OLD_ROOM_ID);

        assertThrows(IllegalArgumentException.class,
                () -> guestRoomService.changeRoom(request, loggedInUser));

        assertEquals(RoomStatus.OCCUPIED, oldRoom.getStatus());
        assertSame(oldRoom, booking.getRoom());
        assertEquals(BookingStatus.CHECKED_IN, booking.getStatus());
        verify(userRepository, never()).findById(any());
        verify(bookingRepository, never()).getCheckedInBooking(any(), anyInt());
        verify(roomRepository, never()).findRoomByRoomIdForUpdate(anyInt());
        verify(bookingHistoryRepository, never()).save(any(BookingHistory.class));
        verify(roomRepository, never()).save(any(Room.class));
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    @DisplayName("Returns false for a missing session and performs no persistence work")
    void changeRoom_nullSession_returnsFalseWithoutSaving() {
        boolean changed = guestRoomService.changeRoom(request, null);

        assertFalse(changed);
        verifyNoInteractions(
                roomRepository,
                roomTypeRepository,
                bookingRepository,
                userRepository,
                modelMapper,
                bookingHistoryRepository
        );
    }

    private void stubAuthenticatedActiveBooking() {
        when(bookingRepository.getCheckedInBooking(USER_ID, OLD_ROOM_ID)).thenReturn(Optional.of(booking));
    }

    private Room room(Integer id, String number, RoomStatus status) {
        Room room = new Room();
        room.setRoomId(id);
        room.setRoomNumber(number);
        room.setStatus(status);
        return room;
    }
}
