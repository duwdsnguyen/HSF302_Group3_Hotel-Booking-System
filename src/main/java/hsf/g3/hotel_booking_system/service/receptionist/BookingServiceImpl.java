package hsf.g3.hotel_booking_system.service.receptionist;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import hsf.g3.hotel_booking_system.dto.receptionist.BookingDetailDTO;
import hsf.g3.hotel_booking_system.entity.guest.Booking;
import hsf.g3.hotel_booking_system.entity.room.Room;
import hsf.g3.hotel_booking_system.enums.user.BookingStatus;
import hsf.g3.hotel_booking_system.enums.user.RoomStatus;
import hsf.g3.hotel_booking_system.repository.admin.RoomRepository;
import hsf.g3.hotel_booking_system.repository.receptionist.BookingRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    
    @Override
    public List<BookingDetailDTO> getBookingsForCheckIn(String search, Double minPrice, Double maxPrice, Integer roomTypeId) {
        List<Booking> bookings = bookingRepository.findAllConfirmedBookings();
        return filterAndMapToDTO(bookings, search, minPrice, maxPrice, roomTypeId);
    }

    @Override
    public List<BookingDetailDTO> getBookingForCheckOut(String search, Double minPrice, Double maxPrice, Integer roomTypeId) {
        List<Booking> bookings = bookingRepository.findAllCheckedInBookings();
        return filterAndMapToDTO(bookings, search, minPrice, maxPrice, roomTypeId);
    }

    private List<BookingDetailDTO> filterAndMapToDTO(List<Booking> bookings, String search, Double minPrice, Double maxPrice, Integer roomTypeId) {
        List<BookingDetailDTO> dtoList = new ArrayList<>();
        
        for (Booking b : bookings) {
            Room room = b.getRoom();
            
            BookingDetailDTO dto = BookingDetailDTO.builder()
                    .bookingId(b.getId())
                    .customerName(b.getCustomer() != null ? b.getCustomer().getFullName() : "Anonymous")
                    .roomNumber(room != null ? room.getRoomNumber() : "N/A")
                    .roomTypeName(room != null && room.getRoomType() != null ? room.getRoomType().getTypeName() : "N/A")
                    .checkInDate(b.getCheckInDate())
                    .checkOutDate(b.getCheckOutDate())
                    .actualCheckIn(b.getActualCheckIn())
                    .numberOfGuest(b.getNumberOfGuests())
                    .totalAmount(b.getTotalAmount() != null ? b.getTotalAmount().doubleValue() : 0.0)
                    .status(b.getStatus())
                    .build();
            
            boolean matchesSearch = search == null || search.trim().isEmpty() || 
                dto.getCustomerName().toLowerCase().contains(search.toLowerCase()) || 
                dto.getRoomNumber().toLowerCase().contains(search.toLowerCase());
                
            boolean matchesMinPrice = minPrice == null || dto.getTotalAmount() >= minPrice;
            boolean matchesMaxPrice = maxPrice == null || dto.getTotalAmount() <= maxPrice;
            boolean matchesRoomType = roomTypeId == null || (room != null && room.getRoomType() != null && room.getRoomType().getRoomTypeId().equals(roomTypeId));

            if (matchesSearch && matchesMinPrice && matchesMaxPrice && matchesRoomType) {
                dtoList.add(dto);
            }
        }
        
        return dtoList;
    }

    @Override
    public void checkIn(int bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy booking ID: " + bookingId));
            
        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new RuntimeException("Chỉ có thể check-in cho những phòng đã CONFIRMED.");
        }
        
        booking.setStatus(BookingStatus.CHECKED_IN);
        booking.setActualCheckIn(LocalDateTime.now());
        
        Room room = booking.getRoom();
        if (room == null) throw new RuntimeException("Không tìm thấy phòng");
        
        room.setStatus(RoomStatus.OCCUPIED);
        
        bookingRepository.save(booking);
        roomRepository.save(room);      
    }

    @Override
    public void checkOut(int bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy booking ID: " + bookingId));
            
        if (booking.getStatus() != BookingStatus.CHECKED_IN) {
            throw new RuntimeException("Chỉ có thể check-out cho những phòng đang CHECKED_IN.");
        }
        
        booking.setStatus(BookingStatus.CHECKED_OUT);
        booking.setActualCheckOut(LocalDateTime.now());

        Room room = booking.getRoom();
        if (room == null) throw new RuntimeException("Không tìm thấy phòng");
        
        room.setStatus(RoomStatus.AVAILABLE);

        bookingRepository.save(booking);
        roomRepository.save(room);
    }
}
