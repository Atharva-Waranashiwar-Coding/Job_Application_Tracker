package com.example.jobtracker;

import com.example.jobtracker.dto.CreateApplicationRequest;
import com.example.jobtracker.dto.ApplicationDto;
import com.example.jobtracker.service.ApplicationService;
import com.example.jobtracker.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Map;

@SpringBootTest
@ActiveProfiles("test")
public class ApplicationIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationService applicationService;

    @Test
    void createAndListApplications() {
        // Create a user (use a random username to avoid collisions across runs)
        var username = "tester_" + System.currentTimeMillis();
        var userRequest = new com.example.jobtracker.dto.CreateUserRequest();
        userRequest.setUsername(username);
        userRequest.setPassword("password123");
        userRequest.setDisplayName("Tester");
        userRequest.setRole("USER");
        var user = userService.createUser(userRequest);

        // Create an application for that user via service
        var appRequest = new CreateApplicationRequest();
        appRequest.setCompany("Acme Corp");
        appRequest.setRole("Engineer");
        appRequest.setAppliedAt(LocalDate.now());
        var created = applicationService.createForUser(user.getUsername(), appRequest);

        Assertions.assertNotNull(created.getId());
        Assertions.assertEquals("Acme Corp", created.getCompany());
        Assertions.assertNotNull(created.getStage());

        var list = applicationService.listForUser(user.getUsername());
        Assertions.assertFalse(list.isEmpty());
        Assertions.assertEquals(1, list.size());

        Map<String, Long> counts = applicationService.statusCounts(user.getUsername());
        Assertions.assertEquals(1L, counts.getOrDefault("Applied", 0L));
    }
}
