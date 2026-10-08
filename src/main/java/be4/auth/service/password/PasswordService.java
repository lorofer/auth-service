package be4.auth.service.password;

public interface PasswordService {
    String generatePassword();

    void sendEmail(String password);
}
