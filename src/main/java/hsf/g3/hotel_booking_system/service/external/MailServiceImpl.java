package hsf.g3.hotel_booking_system.service.external;

import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;



@Service
public class MailServiceImpl implements MailService {

    private final Logger LOGGER = LoggerFactory.getLogger(MailServiceImpl.class);
    private final JavaMailSender mailSender ;

    public MailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Value("${spring.mail.username}")
    String FROM_EMAIL;


    @Override
    public void sendResetPasswordEmail(String email,String resetUrl) {
        String subject = "Hotel Management System -RESET PASSWORD";
        String body = "<html><head><meta charset='UTF-8'></head><body>" +
                "<p>You have requested to reset your password.</p>" +
                "<p>Please click the link below to proceed with the reset:</p>" +
                "<p><a href='" + resetUrl + "'>Click here to reset your password</a></p>" +
                "<p><i>Note: This link will expire in 24 hours.</i></p>" +
                "<p>If you did not request this, please ignore this email.</p>" +
                "</body></html>";
        Thread.startVirtualThread(() ->{
            try{
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

                helper.setFrom(FROM_EMAIL);
                helper.setTo(email);
                helper.setSubject(subject);
                helper.setText(body, true);

                mailSender.send(message);
            }catch (Exception e){
                LOGGER.error("Không thể gửi mail reset password cho email: {} với resetUrl: {}", email, resetUrl, e);            }
        });
    }
}
