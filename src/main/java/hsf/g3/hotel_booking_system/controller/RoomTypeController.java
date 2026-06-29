package hsf.g3.hotel_booking_system.controller;

import hsf.g3.hotel_booking_system.entity.RoomType;
import hsf.g3.hotel_booking_system.service.RoomTypeService;
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
    public String listRoomType(Model model){
        model.addAttribute("roomTypes", roomTypeService.getAllRoomTypes());
        return "/admin/room-type-list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model){
        model.addAttribute("roomType", new RoomType());
        model.addAttribute("formAction", "/admin/room-types/create");
        return "admin/room-type-form";
    }

    @PostMapping("/create")
    public String createRoomType(@ModelAttribute RoomType roomType){
        roomTypeService.createRoomType(roomType);
        return "redirect:/admin/room-types";
    }

    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable Integer id, Model model){
        model.addAttribute("roomType", roomTypeService.getRoomTypeById(id));
        model.addAttribute("formAction", "/admin/room-types/edit/" + id);
        return "admin/room-type-form";
    }

    @PostMapping("/edit/{id}")
    public String updateRoomType(@PathVariable Integer id, @ModelAttribute RoomType roomType){
        roomTypeService.updateRoomType(id, roomType);
        return "redirect:/admin/room-types";
    }

    @GetMapping("/delete/{id}")
    public String deleteRoomType(@PathVariable Integer id){
        roomTypeService.deleteRoomType(id);
        return "redirect:/admin/room-types";
    }

}
