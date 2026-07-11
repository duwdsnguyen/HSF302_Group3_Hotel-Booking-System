package hsf.g3.hotel_booking_system.filter;

import hsf.g3.hotel_booking_system.dto.user.UserInfoDTO;
import hsf.g3.hotel_booking_system.enums.user.AppRole;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

public class AuthInterceptor implements HandlerInterceptor {

    private final Logger LOGGER = LoggerFactory.getLogger(AuthInterceptor.class);
    private final String HTTP_SESSION = "loggedInUser";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI().substring(request.getContextPath().length());
        AppRole requiredRole = convertPathToRole(path);
        if(requiredRole == null){
            return true;
        }
        HttpSession session = request.getSession(false);
        UserInfoDTO loggedInUser = (session != null) ? (UserInfoDTO)session.getAttribute(HTTP_SESSION) : null;

        if(loggedInUser == null){
            LOGGER.warn("Unauthenticated to path: {} -> redirect to the login page",path);
            response.sendRedirect(request.getContextPath()+"/v1/auth/login");
            return false;
        }

        boolean hasPermission = loggedInUser.getRoles() != null && loggedInUser.getRoles().stream().anyMatch(role -> role.getRoleCode().equals(requiredRole));
        if(!hasPermission){
            LOGGER.warn("Account with email : {} doesn't have authorize to access path : {} (Required role: {})",
                    loggedInUser.getEmail(), path, requiredRole);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Unauthorized to access this path");
            return false;
        }
        return true;
    }

    private AppRole convertPathToRole(String path){
        if(path == null || path.isBlank()){
            return null;
        }
        if(path.startsWith("/v1/admin")){
            return AppRole.ADMIN;
        }else if (path.startsWith("/v1/guest")){
            return AppRole.GUEST;
        }else if(path.startsWith("/v1/receptionist")){
            return AppRole.RECEPTIONIST;
        }
        return null;
    }
}
