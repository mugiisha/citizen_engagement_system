package org.amir.ces.helper;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class PasswordGenerator {

    public static String generatePassword(String input, int length) {
        if (input == null || input.isEmpty() || length <= 0) {
            throw new IllegalArgumentException("Invalid input or length");
        }

        // Take a substring of the input (up to half the desired length or the input length)
        int substringLength = Math.min(input.length(), length / 2);
        String base = input.substring(0, substringLength);

        // Generate random numbers to fill the remaining length
        Random random = new Random();
        StringBuilder password = new StringBuilder(base);
        while (password.length() < length) {
            password.append(random.nextInt(10)); // Append a random digit (0-9)
        }

        return password.toString();
    }
}
