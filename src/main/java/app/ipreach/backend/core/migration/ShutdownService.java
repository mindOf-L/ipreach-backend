package app.ipreach.backend.core.migration;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ShutdownService {

    private final ConfigurableApplicationContext context;

    public void shutdown() {
        System.out.println("Shutting down...");
        SpringApplication.exit(context, () -> 0);
        System.exit(0);
    }

}
