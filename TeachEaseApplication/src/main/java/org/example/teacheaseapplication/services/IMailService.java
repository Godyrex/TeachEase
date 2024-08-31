package org.example.teacheaseapplication.services;


import org.example.teacheaseapplication.models.CodeVerification;
import org.example.teacheaseapplication.models.User;

public interface IMailService {
    void sendConfirmationEmail(User user, CodeVerification codeVerification);
    void sendPasswordResetEmail(User user, CodeVerification codeVerification);
    void sendAccountCreatedEmail(User user);
    void sendAccountCreatedForYouMail(User user, String password);
}
