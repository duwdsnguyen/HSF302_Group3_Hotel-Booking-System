package hsf.g3.hotel_booking_system.controller.admin;


import hsf.g3.hotel_booking_system.dto.service.ServiceFormDTO;
import hsf.g3.hotel_booking_system.entity.service.HotelService;
import hsf.g3.hotel_booking_system.enums.service.ServiceStatus;
import hsf.g3.hotel_booking_system.service.service.HotelServiceService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller

@RequestMapping("/v1/admin/services")
public class AdminServiceController {
    private final HotelServiceService hotelServiceService;

    public AdminServiceController(HotelServiceService hotelServiceService) {
        this.hotelServiceService = hotelServiceService;
    }

    @GetMapping("/list")
    public String viewServiceList(@RequestParam(required = false) String keyword,
                                  @RequestParam(required = false) ServiceStatus status,
                                  Model model) {
        model.addAttribute("services", hotelServiceService.searchServices(keyword, status));
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedStatus", status);

        return "pages/admin/services/list";
    }

    @GetMapping("/add")
    public String showCreateServiceForm(Model model) {
        model.addAttribute("serviceForm", new ServiceFormDTO());
        return "pages/admin/services/create";
    }

    @GetMapping("/edit/{id}")
    public String showEditServiceForm(@PathVariable("id") Long serviceId, Model model) {
        HotelService hotelService = hotelServiceService.getServiceById(serviceId);

        ServiceFormDTO serviceFormDTO = new ServiceFormDTO();
        serviceFormDTO.setServiceName(hotelService.getServiceName());
        serviceFormDTO.setDescription(hotelService.getDescription());
        serviceFormDTO.setPrice(hotelService.getPrice());
        serviceFormDTO.setStatus(hotelService.getStatus());

        model.addAttribute("serviceForm", serviceFormDTO);
        model.addAttribute("serviceId", serviceId);

        return "pages/admin/services/edit";
    }

    @PostMapping("/add")
    public String createService(@ModelAttribute("serviceForm") ServiceFormDTO serviceFormDTO, Model model) {
        try {
            HotelService hotelService = new HotelService();
            hotelService.setServiceName(serviceFormDTO.getServiceName());
            hotelService.setDescription(serviceFormDTO.getDescription());
            hotelService.setPrice(serviceFormDTO.getPrice());
            hotelService.setStatus(serviceFormDTO.getStatus());

            hotelServiceService.createService(hotelService);
            return "redirect:/v1/admin/services/list";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("serviceForm", serviceFormDTO);
            return "pages/admin/services/create";
        }
    }

    @PostMapping("/edit/{id}")
    public String updateService(@PathVariable("id") Long serviceId,
                                @ModelAttribute("serviceForm") ServiceFormDTO serviceFormDTO,
                                Model model) {
        try {
            HotelService hotelService = new HotelService();
            hotelService.setServiceName(serviceFormDTO.getServiceName());
            hotelService.setDescription(serviceFormDTO.getDescription());
            hotelService.setPrice(serviceFormDTO.getPrice());
            hotelService.setStatus(serviceFormDTO.getStatus());

            hotelServiceService.updateService(serviceId, hotelService);
            return "redirect:/v1/admin/services/list";
        }catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("serviceForm", serviceFormDTO);
            model.addAttribute("serviceId", serviceId);

            return "pages/admin/services/edit";
        }
    }

    @PostMapping("/activate/{id}")
    public String activateService(@PathVariable("id") Long serviceId) {
        hotelServiceService.activateService(serviceId);
        return "redirect:/v1/admin/services/list";
    }

    @PostMapping("/deactivate/{id}")
    public String deactivateService(@PathVariable("id") Long serviceId) {
        hotelServiceService.deactivateService(serviceId);
        return "redirect:/v1/admin/services/list";
    }
}
