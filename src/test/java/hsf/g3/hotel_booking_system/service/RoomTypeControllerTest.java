package hsf.g3.hotel_booking_system.service;

import hsf.g3.hotel_booking_system.controller.admin.RoomTypeController;
import hsf.g3.hotel_booking_system.dto.admin.RoomTypeRequestDTO;
import hsf.g3.hotel_booking_system.dto.user.UserInfoDTO;
import hsf.g3.hotel_booking_system.entity.user.Role;
import hsf.g3.hotel_booking_system.enums.user.AppRole;
import hsf.g3.hotel_booking_system.service.admin.AdminRoomTypeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RoomTypeController.class)
@DisplayName("Kiểm thử luồng tạo mới Loại Phòng tại tầng Controller")
public class RoomTypeControllerTest {

    private static final String CREATE_URL = "/v1/admin/room-types/create";
    private static final String SESSION_KEY = "loggedInUser";
    private static final String MODEL_ATTR_NAME = "roomType";
    private static final String FORM_VIEW = "/pages/admin/room/room-type-form";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminRoomTypeService roomTypeService;

    private UserInfoDTO adminUser() {
        Role adminRole = new Role();
        adminRole.setRoleCode(AppRole.ADMIN);

        UserInfoDTO user = new UserInfoDTO();
        user.setUserId(1L);
        user.setEmail("admin@hotel.com");
        user.setRoles(Set.of(adminRole));
        return user;
    }

    private MockHttpServletRequestBuilder createBaseRequest() {
        return post(CREATE_URL)
                .sessionAttr(SESSION_KEY, adminUser())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED);
    }

    private ResultMatcher fieldErrorMessage(String field, String expectedMessage) {
        return result -> {
            assertNotNull(result.getModelAndView(), "Mong đợi controller render lại view form nhưng không có ModelAndView!");
            BindingResult bindingResult = (BindingResult) result.getModelAndView()
                    .getModel().get(BindingResult.MODEL_KEY_PREFIX + MODEL_ATTR_NAME);
            assertNotNull(bindingResult, "Không tìm thấy BindingResult cho '" + MODEL_ATTR_NAME + "'");

            FieldError fieldError = bindingResult.getFieldError(field);
            assertNotNull(fieldError, "Không tìm thấy lỗi validation cho trường: " + field);
            assertEquals(expectedMessage, fieldError.getDefaultMessage(),
                    "Message hiển thị cho trường '" + field + "' không đúng!");
        };
    }





    @Test
    @DisplayName("TC01 - Chặn tạo mới khi trường Tên Loại Phòng (typeName) để trống")
    void createRoomType_TC03_Fail_WhenTypeNameMissing() throws Exception {
        mockMvc.perform(createBaseRequest()
                        .param("typeName", "")
                        .param("maxGuests", "5")
                        .param("basePrice", "2000000")
                        .param("status", "ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(view().name(FORM_VIEW))
                .andExpect(model().attributeHasFieldErrors(MODEL_ATTR_NAME, "typeName"))
                .andExpect(fieldErrorMessage("typeName", "Tên loại phòng không được để trống"));

        verify(roomTypeService, never()).createRoomType(any(RoomTypeRequestDTO.class));
    }

    @Test
    @DisplayName("TC02 - Chặn tạo mới khi trường Số khách (maxGuests) bị thiếu")
    void createRoomType_TC04_Fail_WhenMaxGuestsMissing() throws Exception {
        mockMvc.perform(createBaseRequest()
                        .param("typeName", "Luxury")
                        .param("basePrice", "2000000")
                        .param("status", "ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(view().name(FORM_VIEW))
                .andExpect(model().attributeHasFieldErrors(MODEL_ATTR_NAME, "maxGuests"))
                .andExpect(fieldErrorMessage("maxGuests", "Số khách không được để trống"));

        verify(roomTypeService, never()).createRoomType(any(RoomTypeRequestDTO.class));
    }

    @Test
    @DisplayName("TC03 - Chặn tạo mới khi trường Giá phòng (basePrice) bị thiếu")
    void createRoomType_TC05_Fail_WhenBasePriceMissing() throws Exception {
        mockMvc.perform(createBaseRequest()
                        .param("typeName", "Luxury")
                        .param("maxGuests", "5")
                        .param("status", "ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(view().name(FORM_VIEW))
                .andExpect(model().attributeHasFieldErrors(MODEL_ATTR_NAME, "basePrice"))
                .andExpect(fieldErrorMessage("basePrice", "Giá phòng không được để trống"));

        verify(roomTypeService, never()).createRoomType(any(RoomTypeRequestDTO.class));
    }

    @Test
    @DisplayName("TC04 - Chặn tạo mới khi trường Trạng thái (status) không được chọn")
    void createRoomType_TC06_Fail_WhenStatusMissing() throws Exception {
        mockMvc.perform(createBaseRequest()
                                .param("typeName", "Luxury")
                                .param("maxGuests", "5")
                                .param("basePrice", "2000000"))
                .andExpect(status().isOk())
                .andExpect(view().name(FORM_VIEW))
                .andExpect(model().attributeHasFieldErrors(MODEL_ATTR_NAME, "status"))
                .andExpect(fieldErrorMessage("status", "Vui lòng chọn trạng thái"));

        verify(roomTypeService, never()).createRoomType(any(RoomTypeRequestDTO.class));
    }

    @Test
    @DisplayName("TC05 - Chặn tạo mới khi trường Số khách (maxGuests) nhỏ hơn 1")
    void createRoomType_TC07_Fail_WhenMaxGuestsBelowMinimum() throws Exception {
        mockMvc.perform(createBaseRequest()
                        .param("typeName", "Luxury")
                        .param("maxGuests", "0")
                        .param("basePrice", "2000000")
                        .param("status", "ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(view().name(FORM_VIEW))
                .andExpect(model().attributeHasFieldErrors(MODEL_ATTR_NAME, "maxGuests"))
                .andExpect(fieldErrorMessage("maxGuests", "Số khách phải lớn hơn 0"));

        verify(roomTypeService, never()).createRoomType(any(RoomTypeRequestDTO.class));
    }

    @Test
    @DisplayName("TC06 - Chặn tạo mới khi trường Giá phòng (basePrice) nhỏ hơn 1")
    void createRoomType_TC08_Fail_WhenBasePriceBelowMinimum() throws Exception {
        mockMvc.perform(createBaseRequest()
                        .param("typeName", "Luxury")
                        .param("maxGuests", "5")
                        .param("basePrice", "0")
                        .param("status", "ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(view().name(FORM_VIEW))
                .andExpect(model().attributeHasFieldErrors(MODEL_ATTR_NAME, "basePrice"))
                .andExpect(fieldErrorMessage("basePrice", "Giá phòng phải lớn hơn 0"));

        verify(roomTypeService, never()).createRoomType(any(RoomTypeRequestDTO.class));
    }

    @Test
    @DisplayName("TC07 - Chặn tạo mới khi gửi một Request trống hoàn toàn không có dữ liệu")
    void createRoomType_TC09_Fail_WhenAllRequiredFieldsMissing() throws Exception {
        mockMvc.perform(createBaseRequest())
                .andExpect(status().isOk())
                .andExpect(view().name(FORM_VIEW))
                .andExpect(model().attributeHasErrors(MODEL_ATTR_NAME))
                .andExpect(fieldErrorMessage("typeName", "Tên loại phòng không được để trống"))
                .andExpect(fieldErrorMessage("maxGuests", "Số khách không được để trống"))
                .andExpect(fieldErrorMessage("basePrice", "Giá phòng không được để trống"))
                .andExpect(fieldErrorMessage("status", "Vui lòng chọn trạng thái"));

        verify(roomTypeService, never()).createRoomType(any(RoomTypeRequestDTO.class));

    }
}
