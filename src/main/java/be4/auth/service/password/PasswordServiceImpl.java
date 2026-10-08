package be4.auth.service.password;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class PasswordServiceImpl implements PasswordService {
    @Override
    public String generatePassword() {
        final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        final SecureRandom random = new SecureRandom();

        StringBuilder pass = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            pass.append(chars.charAt(random.nextInt(chars.length())));
        }

        return pass.toString();
    }

    @Override
    public void sendEmail(String password) {
        System.out.println("Your new password: " + password);
    }
}
