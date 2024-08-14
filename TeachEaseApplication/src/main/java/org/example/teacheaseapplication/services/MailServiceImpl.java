package org.example.teacheaseapplication.services;


import lombok.AllArgsConstructor;
import org.example.teacheaseapplication.configurations.RabbitProducerConfig;
import org.example.teacheaseapplication.dto.requests.EmailRequest;
import org.example.teacheaseapplication.models.CodeVerification;
import org.example.teacheaseapplication.models.User;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MailServiceImpl implements IMailService {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Override
    public void sendConfirmationEmail(User user, CodeVerification codeVerification) {

        String htmlMsg = "<h3>Hello, " + user.getEmail() + "</h3>"
                + "<p>Thank you for registering. Please click the below link to verify your email:</p>"
                + "<a href='http://localhost:4200/sessions/verify-email?code=" + codeVerification.getCode() + "'>Verify Email</a>"
                + "<p>If you did not make this request, you can ignore this email.</p>"
                + "<p>Best,</p>"
                + "<p>Courzelo</p>";
        EmailRequest emailRequest = EmailRequest.builder()
                .to(user.getEmail())
                .subject("Email Verification")
                .text(htmlMsg)
                .build();
        rabbitTemplate.convertAndSend(RabbitProducerConfig.EXCHANGE, RabbitProducerConfig.ROUTING_KEY, emailRequest);
    }

    @Override
    public void sendPasswordResetEmail(User user, CodeVerification codeVerification) {

        String htmlMsg = "<h3>Hello, " + user.getEmail() + "</h3>"
                + "<p>You have requested to reset your password. Please click the below link to proceed:</p>"
                + "<a href='http://localhost:4200/sessions/reset-password?code=" + codeVerification.getCode() + "'>Reset Password</a>"
                + "<p>If you did not make this request, please ignore this email.</p>"
                + "<p>Best regards,</p>"
                + "<p>Courzelo Team</p>";

            EmailRequest emailRequest = EmailRequest.builder()
                    .to(user.getEmail())
                    .subject("Password Reset")
                    .text(htmlMsg)
                    .build();
        rabbitTemplate.convertAndSend(RabbitProducerConfig.EXCHANGE, RabbitProducerConfig.ROUTING_KEY, emailRequest);
    }

    @Override
    public void sendAccountCreatedEmail(User user) {
        String htmlMsg = "<h3>Hello, " + user.getEmail() + "</h3>"
                + "<p>Your account has been created successfully. You can now login to your account.</p>"
                + "<p>Best regards,</p>"
                + "<p>Courzelo Team</p>";

        EmailRequest emailRequest = EmailRequest.builder()
                .to(user.getEmail())
                .subject("Account Created")
                .text(htmlMsg)
                .build();
        rabbitTemplate.convertAndSend(RabbitProducerConfig.EXCHANGE, RabbitProducerConfig.ROUTING_KEY, emailRequest);
    }
}
