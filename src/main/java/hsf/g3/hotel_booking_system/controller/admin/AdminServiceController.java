package hsf.g3.hotel_booking_system.controller.admin;


import hsf.g3.hotel_booking_system.dto.service.ServiceFormDTO;
import hsf.g3.hotel_booking_system.entity.service.HotelService;
import hsf.g3.hotel_booking_system.enums.service.ServiceStatus;
import hsf.g3.hotel_booking_system.service.service.HotelServiceService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

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
    @ResponseBody
//    public String createService(@Valid @ModelAttribute("serviceForm") ServiceFormDTO serviceFormDTO, BindingResult result, Model model) {
    public String createService(
            @RequestParam String serviceName,
            @RequestParam(required = false) String description,
            @RequestParam BigDecimal price,
            @RequestParam ServiceStatus status) {
//        if (result.hasErrors()) {
//            return "pages/admin/services/create";
//        }
        try {
            if (serviceName == null || serviceName.trim().isEmpty()) {
                return "{\"status\":\"error\", \"message\":\"Tên dịch vụ không được để trống\"}";
            }
            HotelService hotelService = new HotelService();
            hotelService.setServiceName(serviceName.trim());
            hotelService.setDescription(description);
            hotelService.setPrice(price);
            hotelService.setStatus(status);

            hotelServiceService.createService(hotelService);
//            return "redirect:/v1/admin/services/list";
            return "{\"status\":\"success\", \"message\":\"Tạo dịch vụ thành công!\", \"redirect\":\"/v1/admin/services/list\"}";
        } catch (RuntimeException e) {
//            model.addAttribute("error", e.getMessage());
//            model.addAttribute("serviceForm", serviceFormDTO);
//            return "pages/admin/services/create";
            String safeMsg = e.getMessage().replace("\"", "'");
            return "{\"status\":\"error\", \"message\":\"" + safeMsg + "\"}";
        }
    }

    @PostMapping("/edit/{id}")
    public String updateService(@PathVariable("id") Long serviceId,
                                @Valid @ModelAttribute("serviceForm") ServiceFormDTO serviceFormDTO,
                                BindingResult result,
                                Model model) {
        if (result.hasErrors()) {
            return "pages/admin/services/edit";
        }
        try {
            HotelService hotelService = new HotelService();
            hotelService.setServiceName(serviceFormDTO.getServiceName());
            hotelService.setDescription(serviceFormDTO.getDescription());
            hotelService.setPrice(serviceFormDTO.getPrice());
            hotelService.setStatus(serviceFormDTO.getStatus());

            hotelServiceService.updateService(serviceId, hotelService);
            return "redirect:/v1/admin/services/list";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("serviceForm", serviceFormDTO);
            model.addAttribute("serviceId", serviceId);

            return "pages/admin/services/edit";
        }
    }

    @PostMapping("/activate/{id}")
    @ResponseBody
    public String activateService(@PathVariable("id") Long serviceId) {
        try {
            hotelServiceService.activateService(serviceId);
//            return "redirect:/v1/admin/services/list";
            return "{\"status\":\"success\", \"message\":\"Đã kích hoạt dịch vụ\"}";
        } catch (Exception e) {
            String safeMsg = e.getMessage().replace("\"", "'");
            return "{\"status\":\"error\", \"message\":\"" + safeMsg + "\"}";
        }
    }

    @PostMapping("/deactivate/{id}")
    @ResponseBody
    public String deactivateService(@PathVariable("id") Long serviceId) {
        try {
            hotelServiceService.deactivateService(serviceId);
//            return "redirect:/v1/admin/services/list";
            return "{\"status\":\"success\", \"message\":\"Đã vô hiệu hóa dịch vụ\"}";
        } catch (Exception e) {
            String safeMsg = e.getMessage().replace("\"", "'");
            return "{\"status\":\"error\", \"message\":\"" + safeMsg + "\"}";
        }
    }

    // Chức năng 4: Endpoint mới phục vụ AJAX Search/Filter, trả về JSON
    @GetMapping("/search")
    @ResponseBody
    public String searchServices(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) ServiceStatus status) {
        List<HotelService> services = hotelServiceService.searchServices(keyword, status);
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < services.size(); i++) {
            HotelService s = services.get(i);
            String desc = s.getDescription() != null ? s.getDescription().replace("\"", "'") : "";
            String name = s.getServiceName() != null ? s.getServiceName().replace("\"", "'") : "";
            sb.append("{")
              .append("\"id\":").append(s.getServiceId()).append(",")
              .append("\"name\":\"").append(name).append("\",")
              .append("\"description\":\"").append(desc).append("\",")
              .append("\"price\":").append(s.getPrice()).append(",")
              .append("\"status\":\"").append(s.getStatus().name()).append("\"")
              .append("}");
            if (i < services.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }
}
