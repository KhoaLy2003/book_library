package com.khoaly.book_library.controller;

import com.khoaly.book_library.dto.UserRecord;
import com.khoaly.book_library.dto.UserRegistrationRecord;
import com.khoaly.book_library.notification.Notification;
import com.khoaly.book_library.notification.NotificationService;
import com.khoaly.book_library.service.BorrowingService;
import com.khoaly.book_library.service.KeycloakService;
import lombok.RequiredArgsConstructor;
import org.keycloak.admin.client.Keycloak;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {
    private final JobLauncher jobLauncher;
    private final Job job;
    private final BorrowingService borrowingService;
    private final NotificationService notificationService;
    private final Keycloak keycloak;
    private final KeycloakService keycloakService;

//    @PostMapping
//    public void scheduleOverdueBorrowing() {
//        borrowingService.scheduleOverdueBorrowing();
//    }

    @PostMapping
    public UserRegistrationRecord createUser(@RequestBody UserRegistrationRecord userRegistrationRecord) {
        return keycloakService.createUser(userRegistrationRecord);
    }

    @GetMapping
    public List<UserRecord> getUsers() {
        return keycloakService.getUsers();
    }


    @GetMapping("/{userId}")
    public UserRecord getUser(@PathVariable String userId) {
        return keycloakService.getUser(userId);
    }

    @PostMapping("/import")
    public void importCsvToDBJob() {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("startAt", System.currentTimeMillis()).toJobParameters();
        try {
            jobLauncher.run(job, jobParameters);
        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
                 JobParametersInvalidException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/send")
    public void sendMessage() {
        notificationService.sendNotification(Notification
                .builder()
                .message("HELLO")
                .build());
    }
}
