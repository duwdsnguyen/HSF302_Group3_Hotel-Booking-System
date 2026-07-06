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
    public String createRoomType(@Valid @ModelAttribute("roomType") RoomTypeRequestDTO request, BindingResult result, Model model){
        if (result.hasErrors()) {
            return roomTypeForm(model, "/v1/admin/room-types/create");
        }

        try {
            roomTypeService.createRoomType(request);
        } catch (IllegalArgumentException e) {
            result.rejectValue("typeName", "duplicate", e.getMessage());
            return roomTypeForm(model, "/v1/admin/room-types/create");
        }

        return "redirect:/v1/admin/room-types";
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable Integer id, Model model){
        model.addAttribute("roomType", roomTypeService.getRoomTypeById(id));
        model.addAttribute("formAction", "/v1/admin/room-types/edit/" + id);
        return "/pages/admin/room/room-type-form";
    }

    @PostMapping("/edit/{id}")
    public String updateRoomType(@PathVariable Integer id, @Valid @ModelAttribute("roomType") RoomTypeRequestDTO request, BindingResult result, Model model){
        String formAction = "/v1/admin/room-types/edit/" + id;
        if (result.hasErrors()) {
            return roomTypeForm(model, formAction);
        }

        try {
            roomTypeService.updateRoomType(id, request);
        } catch (IllegalArgumentException e) {
            result.rejectValue("typeName", "duplicate", e.getMessage());
            return roomTypeForm(model, formAction);
        }

        return "redirect:/v1/admin/room-types";
    }

    @GetMapping("/delete/{id}")
    public String deleteRoomType(@PathVariable Integer id){
        roomTypeService.deleteRoomType(id);
        return "redirect:/v1/admin/room-types";
    }

    private String roomTypeForm(Model model, String formAction) {
        model.addAttribute("formAction", formAction);
        return "/pages/admin/room/room-type-form";
    }

}
