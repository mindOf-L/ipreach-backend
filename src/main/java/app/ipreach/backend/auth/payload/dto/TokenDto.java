package app.ipreach.backend.auth.payload.dto;

import app.ipreach.backend.shared.enums.ERole;
import app.ipreach.backend.shared.enums.ETokenType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.nimbusds.jwt.SignedJWT;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder(toBuilder = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record TokenDto (

    UUID id,
    Long userId,
    LocalDateTime expiresAt,
    LocalDateTime issuedAt,
    String jwtID,
    String jwk,
    List<ERole> roles,
    boolean termsSignedAndActive,
    ETokenType tokenType,
    UUID refreshTokenId,
    String tokenHash,
    SignedJWT signedJWT

) {
    public boolean isExpired() {
        LocalDateTime now = LocalDateTime.now();
        return issuedAt.isAfter(now) || expiresAt.isBefore(now);
    }
}
