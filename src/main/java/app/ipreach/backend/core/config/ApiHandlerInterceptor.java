package app.ipreach.backend.core.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApiHandlerInterceptor implements HandlerInterceptor {

    //private final JwtUtils jwtUtils;
    private UUID petitionID;
    private String petitionHttpMethod;
    private String petitionEndpoint;
    private LocalDateTime petitionStartTime;
    private int petitionHttpStatus;
    private String petitionAgent;
    private String petitionOrigin;

    @Value("${refresh-token-header}")
    private String refreshTokenHeader;

    @Value("${payload-token-header}")
    private String payloadTokenHeader;

    @Value("${signature-token-header}")
    private String signatureTokenHeader;

    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        this.petitionStartTime = LocalDateTime.now();

        this.petitionAgent = Optional.ofNullable(request.getHeader("user-agent")).orElse("postman/unknown");
        this.petitionOrigin = Optional.ofNullable(request.getHeader("origin")).orElse("No origin");

        if(MDC.get("petitionId") != null) {
            this.petitionID = UUID.fromString(MDC.get("petitionId"));
        } else {
            this.petitionID = UUID.randomUUID();
            MDC.put("petitionId", this.petitionID.toString());
        }

        this.petitionEndpoint = request.getRequestURI();
        this.petitionHttpMethod = request.getMethod();

        log.info("### [PRE] Petition with id {}, calling to [{}]{}, from {} with origin {} at {}.",
            this.petitionID,
            this.petitionHttpMethod,
            this.petitionEndpoint,
            this.petitionAgent,
            this.petitionOrigin,
            this.petitionStartTime);

        MDC.put("petitionEndpoint", this.petitionEndpoint);

        String refreshToken = request.getHeader(refreshTokenHeader);
        String headerPayload = request.getHeader(payloadTokenHeader);
        String signature = request.getHeader(signatureTokenHeader);

        if(ObjectUtils.allNotNull(headerPayload, signature)) {
            //SignedJWT signedJWT = jwtUtils.getDecodedJwt(String.format("%s.%s", headerPayload, signature));
            //String groupId = ((LinkedTreeMap<?,?>) signedJWT.getJWTClaimsSet().getClaim("userClaims")).get("groupId").toString();
        }

        return true;
    }

    public void postHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, ModelAndView modelAndView) {
        logPostHandle(request, response.getStatus());
        MDC.clear();
    }

    public void logPostHandle(HttpServletRequest request, int httpStatus) {
        this.petitionHttpStatus = httpStatus;
    }

    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, Exception ex) {
        long petitionStartTime = this.petitionStartTime.toEpochSecond(ZoneOffset.UTC);
        long petitionEndTime = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
        log.info("### [POST][{}] Petition with id {}, calling to [{}]{}, from {} with origin {} at {}. Time cost: {}ms",
            this.petitionHttpStatus,
            this.petitionID,
            this.petitionHttpMethod,
            this.petitionEndpoint,
            this.petitionAgent,
            this.petitionOrigin,
            this.petitionStartTime,
            petitionEndTime - petitionStartTime);
    }

}
