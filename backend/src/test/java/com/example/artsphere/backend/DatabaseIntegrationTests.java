package com.example.artsphere.backend;

import com.example.artsphere.backend.model.User;
import com.example.artsphere.backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class DatabaseIntegrationTests {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private UserRepository userRepository;


    @Test
    void testSimpleSelect() throws Exception {
        try (Connection conn = dataSource.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT 1")) {
            assertThat(rs.next()).isTrue();
            assertThat(rs.getInt(1)).isEqualTo(1);
        }
    }

    @Test
    void testWriteAndReadUser() {
        User u = new User();
        u.setUsername("integration_user");
        u.setEmail("int@example.com");
        u.setPassword("pass");
        u.setRole("BUYER");
        u.setBalance(java.math.BigDecimal.valueOf(50.0));

        User saved = userRepository.save(u);
        Optional<User> fetched = userRepository.findById(saved.getId());
        assertThat(fetched).isPresent();
        assertThat(fetched.get().getUsername()).isEqualTo("integration_user");
        assertThat(fetched.get().getEmail()).isEqualTo("int@example.com");
    }

    @Test
    void testSearchByRole() {
        User u1 = new User(); u1.setUsername("u1"); u1.setEmail("u1@example.com"); u1.setPassword("pass1"); u1.setRole("ARTIST");
        User u2 = new User(); u2.setUsername("u2"); u2.setEmail("u2@example.com"); u2.setPassword("pass2"); u2.setRole("ARTIST");
        User u3 = new User(); u3.setUsername("u3"); u3.setEmail("u3@example.com"); u3.setPassword("pass3"); u3.setRole("BUYER");
        userRepository.saveAll(List.of(u1,u2,u3));

        List<User> artists = userRepository.findByRole("ARTIST");
        // Upewnij się, że nowo dodani użytkownicy są obecni wśród artystów
        assertThat(artists).extracting(User::getUsername).contains("u1","u2");
    }
}

