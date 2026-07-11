package hsf.g3.hotel_booking_system.controller.guest;

import hsf.g3.hotel_booking_system.config.AppConstants;
import hsf.g3.hotel_booking_system.dto.guest.room.request.RoomChangeRequest;
import hsf.g3.hotel_booking_system.dto.guest.room.response.RoomResponse;
import org.springframework.ui.Model;
import hsf.g3.hotel_booking_system.entity.room.Room;
import hsf.g3.hotel_booking_system.service.guest.GuestRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
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
            List<Room> availableRooms = guestRoomService.searchAvailableRooms(checkInDate, checkOutDate, numberOfGuests, minPrice, maxPrice, roomTypeId);

            model.addAttribute("rooms", availableRooms);
            model.addAttribute("roomTypes", guestRoomService.getAllRoomTypes());
            model.addAttribute("checkIn", checkInDate);
            model.addAttribute("checkOut", checkOutDate);
            model.addAttribute("guests", numberOfGuests);
            model.addAttribute("minPrice", minPrice);
            model.addAttribute("maxPrice", maxPrice);
            model.addAttribute("roomTypeId", roomTypeId);

            return "pages/guest/search_results";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "pages/guest/dashboard";
        }
    }

    @GetMapping("/dashboard")
    public String showDashboard() {
        return "pages/guest/dashboard";
    }

    @GetMapping("/room/{id}")
    public String viewRoomDetail(@org.springframework.web.bind.annotation.PathVariable Integer id, Model model) {
        try {
            Room room = guestRoomService.getRoomById(id);
            model.addAttribute("room", room);
            return "pages/guest/room_detail";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "pages/guest/search_results";
        }
    }

    @GetMapping("/room/change")
    public String requestRoomChange(Model model,
                                    @RequestParam(name = "pageNumber",defaultValue = AppConstants.PAGE_NUMBER,required = false) Integer pageNumber,
                                    @RequestParam(name = "pageSize",defaultValue = AppConstants.PAGE_SIZE,required = false) Integer pageSize,
                                    @RequestParam(name = "sortBy",defaultValue = AppConstants.SORT_ROOM_BY,required = false) String sortBy,
                                    @RequestParam(name = "sortOrder",defaultValue = AppConstants.SORT_DIR,required = false) String sortOrder){
        RoomResponse roomResponse = guestRoomService.getAllAvailableRoom(pageNumber,pageSize,sortBy,sortOrder);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortOrder", sortOrder);
        model.addAttribute("roomResponse", roomResponse);
        return "pages/guest/room/room_change";
    }

    @PostMapping("/room/change")
    public String requestRoomChange(RedirectAttributes redirectAttributes, @ModelAttribute("roomChangeRequest") RoomChangeRequest roomChangeRequest){
        boolean isChangedSuccess = guestRoomService.changeRoom(roomChangeRequest);
        if(!isChangedSuccess){
            redirectAttributes.addFlashAttribute("error","Không thể thay đổi phòng khác");
        }else{
            redirectAttributes.addFlashAttribute("success","Đã thay đổi phòng thành công");
        }
        return "redirect:/v1/guest/room/change?sortBy="+roomChangeRequest.getSortBy()+"&sortOrder="+roomChangeRequest.getSortOrder()+"&pageSize="+roomChangeRequest.getPageSize();
    }


}
