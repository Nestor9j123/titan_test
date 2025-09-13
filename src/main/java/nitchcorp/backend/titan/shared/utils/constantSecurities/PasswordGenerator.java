package nitchcorp.backend.titan.shared.utils.constantSecurities;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.SecureRandom;

public class PasswordGenerator {
    private static final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String CHAR_UPPER = CHAR_LOWER.toUpperCase();
    private static final String NUMBER = "0123456789";
    private static final String SPECIAL_CHARS = "!@#$%^&*";
    private static final String PASSWORD_ALLOW = CHAR_LOWER + CHAR_UPPER + NUMBER + SPECIAL_CHARS;
    private static final SecureRandom random = new SecureRandom();
    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public static PasswordResult generateSecurePassword() {
        StringBuilder password = new StringBuilder(12);

        // Ensure at least one character of each type
        password.append(CHAR_LOWER.charAt(random.nextInt(CHAR_LOWER.length())));
        password.append(CHAR_UPPER.charAt(random.nextInt(CHAR_UPPER.length())));
        password.append(NUMBER.charAt(random.nextInt(NUMBER.length())));
        password.append(SPECIAL_CHARS.charAt(random.nextInt(SPECIAL_CHARS.length())));

        // Fill to 12 characters
        for (int i = 4; i < 12; i++) {
            password.append(PASSWORD_ALLOW.charAt(random.nextInt(PASSWORD_ALLOW.length())));
        }

        String plainPassword = shuffleString(password.toString());
        String hashedPassword = passwordEncoder.encode(plainPassword);

        return new PasswordResult(plainPassword, hashedPassword);
    }

    private static String shuffleString(String input) {
        char[] characters = input.toCharArray();
        for (int i = characters.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = characters[i];
            characters[i] = characters[j];
            characters[j] = temp;
        }
        return new String(characters);
    }
}