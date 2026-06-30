package hsf.g3.hotel_booking_system.controller.admin;

import hsf.g3.hotel_booking_system.dto.admin.RoomTypeRequestDTO;
import hsf.g3.hotel_booking_system.enums.user.RoomTypeStatus;
import hsf.g3.hotel_booking_system.service.admin.AdminRoomTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/v1/admin/room-types")
public class RoomTypeController {
    private final AdminRoomTypeService roomTypeService;

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
        model.addAttribute("formAction", "/v1/admin/room-types/create");
        return "/pages/admin/room/room-type-form";
    }

    @PostMapping("/create")
    public String createRoomType(@Valid @ModelAttribute RoomTypeRequestDTO request, BindingResult result, Model model){
        roomTypeService.createRoomType(request);
        return "redirect:/v1/admin/room-types";
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable Integer id, Model model){
        model.addAttribute("roomType", roomTypeService.getRoomTypeById(id));
        model.addAttribute("formAction", "/v1/admin/room-types/edit/" + id);
        return "/pages/admin/room/room-type-form";
    }

    @PostMapping("/edit/{id}")
    public String updateRoomType(@PathVariable Integer id, @ModelAttribute RoomTypeRequestDTO request){
        roomTypeService.updateRoomType(id, request);
        return "redirect:/v1/admin/room-types";
    }

    @GetMapping("/delete/{id}")
    public String deleteRoomType(@PathVariable Integer id){
        roomTypeService.deleteRoomType(id);
        return "redirect:/v1/admin/room-types";
    }

}
