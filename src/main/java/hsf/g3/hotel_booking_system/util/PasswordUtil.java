package hsf.g3.hotel_booking_system.util;
import org.mindrot.jbcrypt.BCrypt;
public class PasswordUtil {
    public static String hashPassword(String plainPassword){
        return BCrypt.hashpw(plainPassword,BCrypt.gensalt());
    }

    public static boolean checkPassword(String password, String passwordInDB){
        return BCrypt.checkpw(password,passwordInDB);
    }
}
