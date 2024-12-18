package com.team1206.pos;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
@Component
public class DatabaseConnectionVerifier implements ApplicationListener<ApplicationReadyEvent> {

    private final DataSource dataSource;

    public DatabaseConnectionVerifier(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try (Connection _ = dataSource.getConnection()) {
            log.info("Database connection is successful!");
        } catch (SQLException e) {
            log.error("Failed to connect to the database: {}", e.toString());
        }
    }
}
