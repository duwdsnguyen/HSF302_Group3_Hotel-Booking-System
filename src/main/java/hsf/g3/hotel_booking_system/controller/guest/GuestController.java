package hsf.g3.hotel_booking_system.controller.guest;

import hsf.g3.hotel_booking_system.config.AppConstants;
import hsf.g3.hotel_booking_system.dto.guest.room.request.RoomChangeRequest;
import hsf.g3.hotel_booking_system.dto.guest.room.response.RoomDTO;
import hsf.g3.hotel_booking_system.dto.guest.room.response.RoomResponse;
import hsf.g3.hotel_booking_system.dto.user.UserInfoDTO;
import hsf.g3.hotel_booking_system.entity.room.Room;
import hsf.g3.hotel_booking_system.enums.room.RoomStatus;
import hsf.g3.hotel_booking_system.exception.ResourceNotFoundException;
import hsf.g3.hotel_booking_system.service.guest.GuestRoomService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/v1/guest")
@RequiredArgsConstructor
public class GuestController {
    private final GuestRoomService guestRoomService;

    @GetMapping("/search-rooms")
    public String searchAvailableRooms(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate,
            @RequestParam Integer numberOfGuests,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Integer roomTypeId,
            Model model) {
        try {
            List<Room> availableRooms = guestRoomService.searchAvailableRooms(
                    checkInDate, checkOutDate, numberOfGuests, minPrice, maxPrice, roomTypeId);

            model.addAttribute("rooms", availableRooms);
            model.addAttribute("roomTypes", guestRoomService.getAllRoomTypes());
            model.addAttribute("checkIn", checkInDate);
            model.addAttribute("checkOut", checkOutDate);
            model.addAttribute("guests", numberOfGuests);
            model.addAttribute("minPrice", minPrice);
            model.addAttribute("maxPrice", maxPrice);
            model.addAttribute("roomTypeId", roomTypeId);

            return "pages/guest/search_results";
        } catch (IllegalArgumentException exception) {
            model.addAttribute("error", exception.getMessage());
            return "pages/guest/dashboard";
        }
    }

    @GetMapping("/dashboard")
    public String showDashboard() {
        return "pages/guest/dashboard";
    }

    @GetMapping("/room/{id}")
    public String viewRoomDetail(@PathVariable Integer id, Model model) {
        try {
            Room room = guestRoomService.getRoomById(id);
            model.addAttribute("room", room);
            return "pages/guest/room_detail";
        } catch (IllegalArgumentException exception) {
            model.addAttribute("error", exception.getMessage());
            return "pages/guest/search_results";
        }
    }

    @GetMapping("/room/change")
    public String requestRoomChange(
            Model model,
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_ROOM_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder) {
        RoomResponse roomResponse = guestRoomService.getAllAvailableRoom(pageNumber, pageSize, sortBy, sortOrder);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("roomResponse", roomResponse);
        return "pages/guest/room/room_change";
    }

    @GetMapping("/room/change/{roomId}")
    public String viewRoomChangeDetail(
            @PathVariable Integer roomId,
            @RequestParam(name = "pageNumber", defaultValue = AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
            @RequestParam(name = "pageSize", defaultValue = AppConstants.PAGE_SIZE, required = false) Integer pageSize,
            @RequestParam(name = "sortBy", defaultValue = AppConstants.SORT_ROOM_BY, required = false) String sortBy,
            @RequestParam(name = "sortOrder", defaultValue = AppConstants.SORT_DIR, required = false) String sortOrder,
            HttpServletRequest request,
            Model model) {
        Room room = guestRoomService.getRoomById(roomId);
        UserInfoDTO userInfoDTO = getLoggedInUser(request);
        List<RoomDTO> checkedInRooms = guestRoomService.getCheckedInRooms(userInfoDTO);

        model.addAttribute("room", room);
        model.addAttribute("bookedRooms", checkedInRooms);
        model.addAttribute("changeHistory", guestRoomService.getRoomChangeHistory(userInfoDTO));
        model.addAttribute("canChange", room.getStatus() == RoomStatus.AVAILABLE && !checkedInRooms.isEmpty());
        model.addAttribute("pageNumber", pageNumber);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortOrder", sortOrder);
        return "pages/guest/room/room_change_detail";
    }

    @PostMapping("/room/change")
    public String requestRoomChange(
            RedirectAttributes redirectAttributes,
            @ModelAttribute("roomChangeRequest") RoomChangeRequest roomChangeRequest,
            HttpServletRequest request) {
        UserInfoDTO userInfoDTO = getLoggedInUser(request);
        try {
            boolean isChangedSuccess = guestRoomService.changeRoom(roomChangeRequest, userInfoDTO);
            if (isChangedSuccess) {
                redirectAttributes.addFlashAttribute("success", "Your room was changed successfully.");
            } else {
                redirectAttributes.addFlashAttribute("error", "You need to sign in before changing rooms.");
            }
        } catch (IllegalArgumentException | ResourceNotFoundException exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
        }

        addListStateToRedirect(redirectAttributes, roomChangeRequest);
        Integer targetRoomId = roomChangeRequest.getNewRoomId();
        if (targetRoomId == null) {
            return "redirect:/v1/guest/room/change";
        }
        return "redirect:/v1/guest/room/change/" + targetRoomId;
    }

    private UserInfoDTO getLoggedInUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null ? (UserInfoDTO) session.getAttribute("loggedInUser") : null;
    }

    private void addListStateToRedirect(RedirectAttributes redirectAttributes,
                                        RoomChangeRequest roomChangeRequest) {
        if (roomChangeRequest.getPageNumber() != null) {
            redirectAttributes.addAttribute("pageNumber", roomChangeRequest.getPageNumber());
        }
        if (roomChangeRequest.getPageSize() != null) {
            redirectAttributes.addAttribute("pageSize", roomChangeRequest.getPageSize());
        }
        if (roomChangeRequest.getSortBy() != null) {
            redirectAttributes.addAttribute("sortBy", roomChangeRequest.getSortBy());
        }
        if (roomChangeRequest.getSortOrder() != null) {
            redirectAttributes.addAttribute("sortOrder", roomChangeRequest.getSortOrder());
        }
    }
}
