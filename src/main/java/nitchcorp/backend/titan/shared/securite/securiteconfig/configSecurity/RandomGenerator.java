package nitchcorp.backend.titan.shared.securite.securiteconfig.configSecurity;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class RandomGenerator {
    public String getSaltString() {
        String num ="1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 4) {
            // length of the random string.
            int index = (int) (rnd.nextFloat() * num.length());
            salt.append(num.charAt(index));
        }

        String saltStr = salt.toString();

        return saltStr;
    }
}
