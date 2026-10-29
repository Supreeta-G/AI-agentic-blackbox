package com.example.blackbox;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class BlackBoxTestcontainersTest {

    static boolean isDockerAvailable() {
        try {
            return DockerClientFactory.instance().isDockerAvailable();
        } catch (Throwable t) {
            return false;
        }
    }

    @Test
    @EnabledIf("isDockerAvailable")
    @DisplayName("Testcontainers: Spawns ephemeral PostgreSQL container and verifies connectivity")
    void testPostgresContainerConnectivity() {
        try (PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
                .withDatabaseName("test_blackbox")
                .withUsername("blackbox_user")
                .withPassword("secret_test_pass")) {

            postgres.start();

            try (Connection conn = DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT 1")) {

                assertTrue(rs.next());
                assertEquals(1, rs.getInt(1));
            }
        } catch (Exception e) {
            fail("Failed to connect to PostgreSQL container: " + e.getMessage());
        }
    }
}
