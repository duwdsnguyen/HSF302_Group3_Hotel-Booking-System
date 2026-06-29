package hsf.g3.hotel_booking_system.controller.admin;

import hsf.g3.hotel_booking_system.dto.room.RoomTypeRequestDTO;
import hsf.g3.hotel_booking_system.enums.user.RoomTypeStatus;
import hsf.g3.hotel_booking_system.service.room.RoomTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/room-types")
public class RoomTypeController {
    private final RoomTypeService roomTypeService;

    @GetMapping
    public String listRoomType(@RequestParam(required = false) String search,
                               @RequestParam(required = false) RoomTypeStatus status,
                               @RequestParam(required = false, defaultValue = "name_asc") String sort,
                               Model model){
        model.addAttribute("roomTypes", roomTypeService.getRoomTypes(search, status, sort));
        model.addAttribute("statuses", RoomTypeStatus.values());
        model.addAttribute("search", search);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedSort", sort);
        return "/pages/admin/room/room-type-list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model){
        model.addAttribute("roomType", new RoomTypeRequestDTO());
        model.addAttribute("formAction", "/admin/room-types/create");
        return "/pages/admin/room/room-type-form";
    }

    @PostMapping("/create")
    public String createRoomType(@ModelAttribute RoomTypeRequestDTO request){
        roomTypeService.createRoomType(request);
        return "redirect:/admin/room-types";
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable Integer id, Model model){
        model.addAttribute("roomType", roomTypeService.getRoomTypeById(id));
        model.addAttribute("formAction", "/admin/room-types/edit/" + id);
        return "/pages/admin/room/room-type-form";
    }

    @PostMapping("/edit/{id}")
    public String updateRoomType(@PathVariable Integer id, @ModelAttribute RoomTypeRequestDTO request){
        roomTypeService.updateRoomType(id, request);
        return "redirect:/admin/room-types";
    }

    @GetMapping("/delete/{id}")
    public String deleteRoomType(@PathVariable Integer id){
        roomTypeService.deleteRoomType(id);
        return "redirect:/admin/room-types";
    }

}
