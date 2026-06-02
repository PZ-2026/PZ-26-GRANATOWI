package com.example.artsphere.backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import javax.sql.DataSource;
import java.sql.Connection;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Testy połączenia z bazą danych.
 */
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class DatabaseConnectionTest {
    /**
     * Konstruktor domyślny.
     */
    public DatabaseConnectionTest() {}

    @Autowired
    private DataSource dataSource;

    @Test
    void shouldConnectToDatabase() throws Exception {
        try (Connection conn = dataSource.getConnection()) {
            assertThat(conn).isNotNull();
            assertThat(conn.isValid(2)).isTrue();
        }
    }
}
