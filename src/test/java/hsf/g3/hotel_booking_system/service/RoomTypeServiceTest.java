package hsf.g3.hotel_booking_system.service;

import hsf.g3.hotel_booking_system.dto.admin.RoomTypeRequestDTO;
import hsf.g3.hotel_booking_system.dto.admin.RoomTypeResponseDTO;
import hsf.g3.hotel_booking_system.entity.room.RoomType;
import hsf.g3.hotel_booking_system.enums.user.RoomTypeStatus;
import hsf.g3.hotel_booking_system.repository.admin.RoomTypeRepository;
import hsf.g3.hotel_booking_system.service.admin.AdminRoomTypeServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Test Tầng Service - Nghiệp Vụ Tạo Loại Phòng")
public class RoomTypeServiceTest {

    @Mock
    private RoomTypeRepository roomTypeRepository;

    @InjectMocks
    private AdminRoomTypeServiceImpl roomTypeService;



    @Test
    @DisplayName("Service_TC01 - Tạo mới thành công khi toàn bộ dữ liệu hợp lệ")
    void createRoomType_Success() {
        // Arrange
        RoomTypeRequestDTO request = new RoomTypeRequestDTO("Luxury", "Phòng sang trọng", 5, new BigDecimal(2000000), RoomTypeStatus.ACTIVE);
        RoomType savedEntity = new RoomType(1, "Luxury", "Phòng sang trọng", 5, new BigDecimal(2000000), RoomTypeStatus.ACTIVE);

        when(roomTypeRepository.existsByTypeName("Luxury")).thenReturn(false);
        when(roomTypeRepository.save(any(RoomType.class))).thenReturn(savedEntity);
        RoomTypeResponseDTO response = roomTypeService.createRoomType(request);

        assertNotNull(response);
        assertEquals("Luxury", response.getTypeName());
        verify(roomTypeRepository, times(1)).save(any(RoomType.class));
    }

    @Test
    @DisplayName("Service_TC02 - Thất bại khi Tên loại phòng bị trống")
    void createRoomType_Fail_WhenTypeNameIsEmpty() {
        RoomTypeRequestDTO request = new RoomTypeRequestDTO("", "Mô tả", 5, new BigDecimal(2000000), RoomTypeStatus.ACTIVE);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            roomTypeService.createRoomType(request);
        });

        assertEquals("Tên loại phòng không được để trống", exception.getMessage());
        verify(roomTypeRepository, never()).save(any(RoomType.class));
    }

    @Test
    @DisplayName("Service_TC03 - Thất bại khi Số khách bị null")
    void createRoomType_Fail_WhenMaxGuestsIsNull() {
        RoomTypeRequestDTO requestNull = new RoomTypeRequestDTO("Luxury", "Mô tả", null, new BigDecimal(2000000), RoomTypeStatus.ACTIVE);
        IllegalArgumentException exNull = assertThrows(IllegalArgumentException.class, () -> roomTypeService.createRoomType(requestNull));
        assertEquals("Số khách phải lớn hơn 0", exNull.getMessage());
        verify(roomTypeRepository, never()).save(any(RoomType.class));
    }

    @Test
    @DisplayName("Service_TC04 - Thất bại khi Số khách < 1")
    void createRoomType_Fail_WhenMaxGuestsIsSmallerThan1() {
        RoomTypeRequestDTO requestZero = new RoomTypeRequestDTO("Luxury", "Mô tả", 0, new BigDecimal(2000000), RoomTypeStatus.ACTIVE);
        IllegalArgumentException exZero = assertThrows(IllegalArgumentException.class, () -> roomTypeService.createRoomType(requestZero));
        assertEquals("Số khách phải lớn hơn 0", exZero.getMessage());
        verify(roomTypeRepository, never()).save(any(RoomType.class));
    }

    @Test
    @DisplayName("Service_TC05 - Thất bại khi Giá phòng bị null")
    void createRoomType_Fail_WhenBasePriceIsNull() {
        RoomTypeRequestDTO request = new RoomTypeRequestDTO("Luxury", "Mô tả", 5, null, RoomTypeStatus.ACTIVE);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            roomTypeService.createRoomType(request);
        });

        assertEquals("Giá phòng không được để trống", exception.getMessage());
        verify(roomTypeRepository, never()).save(any(RoomType.class));
    }

    @Test
    @DisplayName("Service_TC06 - Thất bại khi Giá phòng nhỏ hơn hoặc bằng 0")
    void createRoomType_Fail_WhenBasePriceIsNegativeOrZero() {
        RoomTypeRequestDTO requestZero = new RoomTypeRequestDTO("Luxury", "Mô tả", 5, BigDecimal.ZERO, RoomTypeStatus.ACTIVE);
        IllegalArgumentException exZero = assertThrows(IllegalArgumentException.class, () -> roomTypeService.createRoomType(requestZero));
        assertEquals("Giá phòng phải lớn hơn 0", exZero.getMessage());

        RoomTypeRequestDTO requestNegative = new RoomTypeRequestDTO("Luxury", "Mô tả", 5, new BigDecimal(-50000), RoomTypeStatus.ACTIVE);
        IllegalArgumentException exNegative = assertThrows(IllegalArgumentException.class, () -> roomTypeService.createRoomType(requestNegative));
        assertEquals("Giá phòng phải lớn hơn 0", exNegative.getMessage());

        verify(roomTypeRepository, never()).save(any(RoomType.class));
    }


    @Test
    @DisplayName("Service_TC07 - Thất bại khi Tên loại phòng đã tồn tại trong DB")
    void createRoomType_Fail_WhenNameAlreadyExists() {
        // Arrange
        RoomTypeRequestDTO request = new RoomTypeRequestDTO("Luxury", "Mô tả", 5, new BigDecimal(2000000), RoomTypeStatus.ACTIVE);

        when(roomTypeRepository.existsByTypeName("Luxury")).thenReturn(true);
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            roomTypeService.createRoomType(request);
        });

        assertEquals("Loại phòng với tên Luxury đã tồn tại", exception.getMessage());
        verify(roomTypeRepository, never()).save(any(RoomType.class));
    }
}