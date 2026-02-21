package org.payment;

import io.quarkus.runtime.Startup;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class StartupLogger {
    @ConfigProperty(name = "quarkus.http.host", defaultValue = "localhost")
    String host;

    @ConfigProperty(name = "quarkus.http.port", defaultValue = "8080")
    int port;

    @Startup
    void logHostAndPort() {
        log.info("Starting LedgerController application on {}:{}", host, port);
    }
}