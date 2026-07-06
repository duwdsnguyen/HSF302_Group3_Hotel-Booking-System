package hsf.g3.hotel_booking_system.controller.admin;


import hsf.g3.hotel_booking_system.dto.admin.RoomRequestDTO;
import hsf.g3.hotel_booking_system.enums.room.RoomStatus;
import hsf.g3.hotel_booking_system.service.admin.AdminRoomService;
import hsf.g3.hotel_booking_system.service.admin.AdminRoomTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/v1/admin/rooms")
public class RoomController {
    private final AdminRoomService roomService;
    private final AdminRoomTypeService roomTypeService;

    @GetMapping
    public String listRooms(@RequestParam(required = false) String search,
                            @RequestParam(required = false) RoomStatus status,
                            @RequestParam(required = false) Integer roomTypeId,
                            @RequestParam(required = false, defaultValue = "number_asc") String sort,
                            Model model){
        model.addAttribute("rooms", roomService.getRooms(search, status, roomTypeId, sort));
        model.addAttribute("roomTypes", roomTypeService.getAllRoomTypes());
        model.addAttribute("statuses", RoomStatus.values());
        model.addAttribute("search", search);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedRoomTypeId", roomTypeId);
        model.addAttribute("selectedSort", sort);
        return "/pages/admin/room/room-list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("room", new RoomRequestDTO());
        model.addAttribute("roomTypes", roomTypeService.getAllRoomTypes());
        model.addAttribute("formAction", "/v1/admin/rooms/create");
        return "/pages/admin/room/room-form";
    }

    @PostMapping("/create")
    public String createRoom(@Valid @ModelAttribute("room") RoomRequestDTO request, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("roomTypes", roomTypeService.getAllRoomTypes());
            model.addAttribute("formAction", "/v1/admin/rooms/create");
            return "/pages/admin/room/room-form";
        }
        try {
            roomService.createRoom(request);
        } catch (IllegalArgumentException e) {
            result.rejectValue("roomNumber", "duplicate", e.getMessage());
            model.addAttribute("roomTypes", roomTypeService.getAllRoomTypes());
            model.addAttribute("formAction", "/v1/admin/rooms/create");
            return "/pages/admin/room/room-form";
        }
        return "redirect:/v1/admin/rooms";
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable Integer id, Model model) {
        model.addAttribute("room", roomService.getRoomById(id));
        model.addAttribute("roomTypes", roomTypeService.getAllRoomTypes());
        model.addAttribute("formAction", "/v1/admin/rooms/edit/" + id);
        return "/pages/admin/room/room-form";
    }

    @PostMapping("/edit/{id}")
    public String updateRoom(@PathVariable Integer id,
                             @Valid @ModelAttribute("room") RoomRequestDTO request,
                             BindingResult result,
                             Model model) {
        if (result.hasErrors()) {
            model.addAttribute("roomTypes", roomTypeService.getAllRoomTypes());
            model.addAttribute("formAction", "/v1/admin/rooms/edit/" + id);
            return "/pages/admin/room/room-form";
        }
        try {
            roomService.updateRoom(id, request);
        } catch (IllegalArgumentException e) {
            result.rejectValue("roomNumber", "duplicate", e.getMessage());
            model.addAttribute("roomTypes", roomTypeService.getAllRoomTypes());
            model.addAttribute("formAction", "/v1/admin/rooms/edit/" + id);
            return "/pages/admin/room/room-form";
        }
        return "redirect:/v1/admin/rooms";
    }

    @GetMapping("/delete/{id}")
    public String deleteRoom(@PathVariable Integer id) {
        roomService.deleteRoom(id);
        return "redirect:/v1/admin/rooms";
    }
}
