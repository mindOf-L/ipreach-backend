package app.ipreach.backend.app.security.jwt;

import app.ipreach.backend.app.exception.custom.RequestException;
import app.ipreach.backend.db.model.Token;
import app.ipreach.backend.db.model.User;
import app.ipreach.backend.db.repository.TokenRepository;
import app.ipreach.backend.db.repository.UserRepository;
import app.ipreach.backend.mapper.dto.auth.TokenDto;
import app.ipreach.backend.mapper.dto.user.UserDto;
import app.ipreach.backend.mapper.payload.user.TokenMapper;
import app.ipreach.backend.shared.constants.Messages;
import app.ipreach.backend.shared.enums.ERole;
import app.ipreach.backend.shared.enums.ETokenType;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.Ed25519Signer;
import com.nimbusds.jose.crypto.Ed25519Verifier;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.OctetKeyPair;
import com.nimbusds.jose.jwk.gen.OctetKeyPairGenerator;
import com.nimbusds.jose.util.Base64;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static app.ipreach.backend.shared.conversion.Convert.dateToLocalDateTime;
import static app.ipreach.backend.shared.process.Hash.hashString;
import static app.ipreach.backend.shared.process.Threads.runInBackground;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtils {

    private final UserRepository userRepository;

    @Value("${app.auth.jwtExpiration:86400}")
    private int jwtExpiration;

    private ETokenType tokenType;

    private final TokenRepository tokenRepository;
    private static final Clock clock = Clock.systemUTC();
    private static final String ISSUER_URL = "https://dev.ipreach.app";

    private static final Map<Long, UUID> refreshTokenLocalRepository = new HashMap<>();

    public synchronized SignedJWT generateNewToken(User user) throws ParseException, JOSEException {
        runInBackground(() -> tokenRepository.deleteAllExpiredTokensFromUser(user.getId()));

        // Generate a key pair with Ed25519 curve
        OctetKeyPair jwk = new OctetKeyPairGenerator(Curve.Ed25519)
            .keyUse(KeyUse.SIGNATURE)
            .keyID(UUID.randomUUID().toString())
            .algorithm(JWSAlgorithm.EdDSA)
            .generate();
        OctetKeyPair publicJWK = jwk.toPublicJWK();

        // Create the EdDSA signer
        JWSSigner signer = new Ed25519Signer(jwk);

        Map<String, String> claims = getClaimsByToken(user);

        Date now = new Date();
        // expiration time is set by parameter (default: 24 hours -> 86400 seconds)
        Date expirationTime = new Date(now.toInstant().plusSeconds(jwtExpiration).toEpochMilli());

        if (tokenType.equalsAny(ETokenType.REFRESH, ETokenType.ANONYMOUS)) {
            expirationTime = new Date(expirationTime.toInstant().plus(30, ChronoUnit.DAYS).toEpochMilli());
            runInBackground(() -> tokenRepository.deleteAllTokensFromUser(user.getId()));
        }

        // Prepare JWT with claims set
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
            .jwtID(jwk.getKeyID())
            .issuer(ISSUER_URL)
            .issueTime(now)
            .subject(user.getId().toString())
            .claim("userClaims", ImmutableMap.copyOf(claims))
            .expirationTime(expirationTime)
            .build();

        SignedJWT signedJWT = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.EdDSA)
            .keyID(jwk.getKeyID())
            .build(),
            claimsSet);

        // Compute the EC signature
        signedJWT.sign(signer);

        // Serialize the JWS to compact form
        String s = signedJWT.serialize();

        // On the consumer side, parse the JWS and verify its EdDSA signature
        signedJWT = SignedJWT.parse(s);

        JWSVerifier verifier = new Ed25519Verifier(publicJWK);

        Token token = Token.builder()
            .jwtID(jwk.getKeyID())
            .jwk(Base64.encode(jwk.toJSONString().getBytes()).toString())
            .userId(user.getId())
            .roles(user.getRoles())
                .issuedAt(dateToLocalDateTime(now))
            .expiresAt(dateToLocalDateTime(expirationTime))
            .tokenType(tokenType)
            .refreshTokenId(tokenType.equals(ETokenType.REGULAR) // attach refresh token only when regular token
                ? refreshTokenLocalRepository.get(user.getId())
                : null)
            .signedJWT(signedJWT)
            .tokenHash(hashString(signedJWT.serialize()))
            .build();
        tokenRepository.saveAndFlush(token);

        if(tokenType.equals(ETokenType.REGULAR))
            refreshTokenLocalRepository.remove(user.getId());
        tokenType = null; // reset token type state

        Preconditions.checkArgument(signedJWT.verify(verifier));
        Preconditions.checkArgument(new Date().before(signedJWT.getJWTClaimsSet().getExpirationTime()));
        Preconditions.checkArgument(signedJWT.getJWTClaimsSet().getIssuer().equals(ISSUER_URL));

        return signedJWT;

    }

    private Map<String, String> getClaimsByToken(User user) {
        return switch(tokenType) {
            case ANONYMOUS, RECOVERY -> new HashMap<>();
            case REGULAR, REFRESH, CONFIRM -> new HashMap<>() {{
                put("email", user.getEmail());
                put("role", user.getRoles().stream().map(ERole::getRoleName).collect(Collectors.joining(",")));
                put("token-type", tokenType.getName());
            }};
        };
    }

    public SignedJWT generateRegularToken(User user) throws ParseException, JOSEException {
        tokenType = ETokenType.REGULAR;
        return generateNewToken(user);
    }

    public SignedJWT generateRefreshToken(User user) throws ParseException, JOSEException {
        tokenType = ETokenType.REFRESH;
        return generateNewToken(user);
    }

    // TODO needs some dev, this token could be tranformed into REGULAR to save resources on login
    public SignedJWT generateAnonymousToken(String email) throws ParseException, JOSEException {
        UserDto user = userRepository.userDetailsByEmail(email);

        runInBackground(() -> tokenRepository.deleteAllTokensFromUser(user.id()));

        // Generate a key pair with Ed25519 curve
        OctetKeyPair jwk = new OctetKeyPairGenerator(Curve.Ed25519)
            .keyUse(KeyUse.SIGNATURE)
            .keyID(UUID.randomUUID().toString())
            .algorithm(JWSAlgorithm.EdDSA)
            .generate();
        OctetKeyPair publicJWK = jwk.toPublicJWK();

        // Create the EdDSA signer
        JWSSigner signer = new Ed25519Signer(jwk);

        Map<String, String> claims = Map.of("email", user.email());

        Date now = new Date();
        // expiration time is set by parameter (default: 24 hours -> 86400 seconds)
        Date expirationTime = new Date(now.toInstant().plus(1, ChronoUnit.DAYS).toEpochMilli());
        runInBackground(() -> tokenRepository.deleteAllTokensFromUser(user.id()));

        // Prepare JWT with claims set
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
            .jwtID(jwk.getKeyID())
            .issuer(ISSUER_URL)
            .issueTime(now)
            .subject(user.id().toString())
            .claim("userClaims", ImmutableMap.copyOf(claims))
            .expirationTime(expirationTime)
            .build();

        SignedJWT signedJWT = new SignedJWT(new JWSHeader.Builder(JWSAlgorithm.EdDSA)
            .keyID(jwk.getKeyID())
            .build(),
            claimsSet);

        // Compute the EC signature
        signedJWT.sign(signer);

        // Serialize the JWS to compact form
        String s = signedJWT.serialize();

        // On the consumer side, parse the JWS and verify its EdDSA signature
        signedJWT = SignedJWT.parse(s);

        JWSVerifier verifier = new Ed25519Verifier(publicJWK);

        Token token = Token.builder()
            .jwtID(jwk.getKeyID())
            .jwk(Base64.encode(jwk.toJSONString().getBytes()).toString())
            .userId(user.id())
            .roles(user.roles())
            .issuedAt(dateToLocalDateTime(now))
            .expiresAt(dateToLocalDateTime(expirationTime))
            .tokenType(ETokenType.ANONYMOUS)
            .refreshTokenId(null)
            .signedJWT(signedJWT)
            .tokenHash(hashString(s))
            .build();

        tokenRepository.saveAndFlush(token);

        Preconditions.checkArgument(signedJWT.verify(verifier));
        Preconditions.checkArgument(new Date().before(signedJWT.getJWTClaimsSet().getExpirationTime()));
        Preconditions.checkArgument(signedJWT.getJWTClaimsSet().getIssuer().equals(ISSUER_URL));

        return signedJWT;
    }

    public Long getUserIdFromJwtToken(SignedJWT signedJWT) {
        try {
            return Long.valueOf(signedJWT.getJWTClaimsSet().getSubject());
        } catch (ParseException ex) {
            throw new RequestException(HttpStatus.BAD_REQUEST, Messages.ErrorClient.TOKEN_INVALID, ex);
        }
    }

    public Long getUserIdFromStringToken(String token) throws ParseException {
        return Long.valueOf(getDecodedJwt(token).getJWTClaimsSet().getSubject());
    }

    public String getEmailFromJwtToken(SignedJWT signedJWT) {
        try {
            return ((Map<?, ?>) signedJWT.getJWTClaimsSet().getClaim("userClaims")).get("email").toString();
        } catch (ParseException ex) {
            throw new RequestException(HttpStatus.BAD_REQUEST, Messages.ErrorClient.TOKEN_INVALID, ex);
        }
    }

    public SignedJWT getDecodedJwt(String jwt) {
        try {
            if (jwt.matches("^Bearer .*"))
                return SignedJWT.parse(jwt.split("Bearer ")[1]);
            return SignedJWT.parse(jwt);
        } catch (ParseException ex) {
            throw new RequestException(HttpStatus.BAD_REQUEST, Messages.ErrorClient.TOKEN_NOT_PARSEABLE, jwt);
        }
    }

    public synchronized boolean verifyJwt(SignedJWT signedJwt) throws IllegalArgumentException {

        long userId = getUserIdFromJwtToken(signedJwt);

        // parse signed token into header / claims
        JWSHeader jwsHeader = signedJwt.getHeader();

        // must exist and match the algorithm
        String kid = jwsHeader.getKeyID();
        String alg = jwsHeader.getAlgorithm().getName();

        TokenDto token = tokenRepository.findByTokenHash(hashString(signedJwt.serialize()))
            .map(TokenMapper.MAPPER::toDto)
            .orElseThrow(() -> new RequestException(HttpStatus.BAD_REQUEST,
                String.format(Messages.ErrorClient.THIS_TOKEN_INVALID, signedJwt.serialize())));

        String jwkString = new String(Base64.from(token.jwk()).decode());

        // header must have algorithm("alg") and "kid"
        Preconditions.checkArgument(token.userId().equals(userId));
        Preconditions.checkNotNull(jwsHeader.getAlgorithm());
        Preconditions.checkNotNull(jwsHeader.getKeyID());

        try {
            JWTClaimsSet claims = signedJwt.getJWTClaimsSet();

            //TODO: claims must have audience, issuer
            // |> Preconditions.checkArgument(claims.getAudience().contains(expectedAudience));
            Preconditions.checkArgument(claims.getIssuer().equals(ISSUER_URL));

            // claim must have issued at time in the past
            Date currentTime = Date.from(Instant.now(clock));
            Preconditions.checkArgument(claims.getIssueTime().before(currentTime));
            // claim must have expiration time in the future
            Preconditions.checkArgument(claims.getExpirationTime().after(currentTime));

            // must have subject, email
            Preconditions.checkNotNull(claims.getSubject());
            Preconditions.checkNotNull(((Map<?, ?>) claims.getClaim("userClaims")).get("email"));

            JWK jwk = JWK.parse(jwkString);
            Preconditions.checkNotNull(jwk);
            // confirm algorithm matches
            Preconditions.checkArgument(jwk.getAlgorithm().getName().equals(alg));

            // verify using public key : lookup with key id, algorithm name provided
            OctetKeyPair publicJWK = OctetKeyPair.parse(jwk.toJSONString()).toPublicJWK();

            Preconditions.checkNotNull(publicJWK);
            Preconditions.checkArgument(LocalDateTime.now().isBefore(token.expiresAt()));
            JWSVerifier jwsVerifier = new Ed25519Verifier(publicJWK);

            return signedJwt.verify(jwsVerifier);
        } catch (ParseException | JOSEException ex) {
            throw new RuntimeException(ex);
        }

    }

}
