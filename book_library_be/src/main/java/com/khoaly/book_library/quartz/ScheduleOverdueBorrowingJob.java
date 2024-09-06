package com.khoaly.book_library.quartz;

import com.khoaly.book_library.service.BorrowingService;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
public class ScheduleOverdueBorrowingJob implements Job {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleOverdueBorrowingJob.class);
    private final BorrowingService borrowingService;

    @Override
    public void execute(JobExecutionContext context) {
        LOGGER.info("Execute schedule overdue borrowing job");
        borrowingService.scheduleOverdueBorrowing();
    }
}
