package app.ipreach.backend.auth.db.model;

import app.ipreach.backend.shared.conversion.ERoleConverter;
import app.ipreach.backend.shared.enums.ERole;
import app.ipreach.backend.shared.enums.ETokenType;
import com.nimbusds.jwt.SignedJWT;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tokens")
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @NotNull
    @Column(updatable = false, nullable = false)
    private Long userId;

    @NotNull
    @Column(updatable = false, nullable = false)
    private LocalDateTime expiresAt;

    @NotNull
    @Column(updatable = false, nullable = false)
    private LocalDateTime issuedAt;

    @NotBlank
    @Column(updatable = false, nullable = false)
    private String jwtID;

    @NotBlank
    @Column(columnDefinition = "text", updatable = false, nullable = false)
    private String jwk;

    @NotNull
    @Column(updatable = false, nullable = false)
    @Convert(converter = ERoleConverter.class)
    private List<ERole> roles;

    @NotNull
    @Column(updatable = false, nullable = false)
    private boolean termsSignedAndActive;

    @NotNull
    @Column(updatable = false, nullable = false)
    @Enumerated(EnumType.STRING)
    private ETokenType tokenType;

    @Column(updatable = false)
    private UUID refreshTokenId;

    private String tokenHash;

    @Transient
    private SignedJWT signedJWT;

}
