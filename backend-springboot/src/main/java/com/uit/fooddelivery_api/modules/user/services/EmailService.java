package com.uit.fooddelivery_api.modules.user.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Async
    public void sendOtpEmail(String toEmail, String otp) {
        log.info("Sending OTP verification email to: {}, OTP Code: {}", toEmail, otp);
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("Food Delivery App <no-reply@fooddelivery.com>");
            message.setTo(toEmail);
            message.setSubject("Password Reset OTP Verification Code");
            message.setText("Dear Customer,\n\n" +
                    "Your OTP verification code for resetting your password is: " + otp + "\n" +
                    "This code will expire in 5 minutes.\n\n" +
                    "If you did not request a password reset, please ignore this email.\n\n" +
                    "Best regards,\n" +
                    "Food Delivery Team");
            mailSender.send(message);
            log.info("OTP verification email sent successfully to {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send email to {}. Error: {}", toEmail, e.getMessage());
            System.out.println("=================================================");
            System.out.println("ALERT: EMAIL CONFIGURATION IS LACKING OR INVALID!");
            System.out.println("TESTING OTP CODE FOR " + toEmail + " IS: " + otp);
            System.out.println("=================================================");
        }
    }
}
