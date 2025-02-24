package app.ipreach.backend.shared.creation;

import lombok.RequiredArgsConstructor;

import static org.springframework.security.crypto.argon2.Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8;

@RequiredArgsConstructor
public class EncryptedPassword {

    public static String getEncryptedPassword(String plainPassword) {
        return defaultsForSpringSecurity_v5_8().encode(plainPassword);
    }

}
