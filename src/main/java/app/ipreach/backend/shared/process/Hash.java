package app.ipreach.backend.shared.process;

import app.ipreach.backend.core.exception.custom.RequestException;
import app.ipreach.backend.shared.constants.Messages;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.http.HttpStatus;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Slf4j
public class Hash {

    public static String hashString(String input) {
        // get a MessageDigest instance for SHA-256
        try {
            return Hex.toHexString(MessageDigest.getInstance("SHA3-256").digest(input.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new RequestException(HttpStatus.INTERNAL_SERVER_ERROR, Messages.ErrorDev.HASH_ALGORITHM_ERROR);
        }
    }

}
