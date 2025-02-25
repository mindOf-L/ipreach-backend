package app.ipreach.backend;

import app.ipreach.backend.shared.constants.ContextConstants;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
@SpringBootApplication
@RequiredArgsConstructor
public class IPreachBackend {

    private static IPreachBackend Instance;
    private final ContextConstants contextConstants;

    @PostConstruct
    public void init() {
        IPreachBackend.Instance = this;
    }

    public static void main(String[] args) throws UnknownHostException {
        var _ = SpringApplication.run(IPreachBackend.class, args);

        log.info("Application started, ready to rock!");

        final String hostname = InetAddress.getLocalHost().getHostName();
        log.info("Check swagger here: http://{}:{}/api/v1/swagger-ui/index.html", hostname,
            Instance.contextConstants.getTomcatPort());
    }

}
