package com.khoaly.book_library.quartz;

import lombok.extern.slf4j.Slf4j;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class TimerUtils {

    @Bean(name = "scheduleOverdueBorrowingJob")
    public JobDetail scheduleOverdueBorrowingJob() {
        return JobBuilder.newJob().ofType(ScheduleOverdueBorrowingJob.class)
                .withIdentity("RUN_BOOT", "BOOT_JOB")
                .withDescription("Invoke Sample Job service...")
                .storeDurably(true)
                .build();
    }

    @Bean(name = "jobTriggerBoot")
    public Trigger jobTrigger(@Qualifier("scheduleOverdueBorrowingJob") JobDetail jobDetail) {
        log.info("Initial execute jobTrigger");
        try {
            String cron = "0 30 17 * * ?";
            return TriggerBuilder.newTrigger().forJob(jobDetail)
                    .withIdentity("RUN_BOOT", "BOOT_JOB")
                    .startNow()
                    .withSchedule(CronScheduleBuilder.cronSchedule(cron))
                    .build();
        } catch (Exception ex) {
            log.error("Error: ", ex);
            return null;
        }
    }

//    public static JobDetail buildJobDetail(final Class jobClass, final TimerInfo info) {
//        final JobDataMap jobDataMap = new JobDataMap();
//        jobDataMap.put(jobClass.getSimpleName(), info);
//
//        return JobBuilder
//                .newJob(jobClass)
//                .withIdentity(jobClass.getSimpleName())
//                .setJobData(jobDataMap)
//                .build();
//    }
//
//    public static Trigger buildTrigger(final Class jobClass, final TimerInfo info) {
//        return TriggerBuilder
//                .newTrigger()
//                .withIdentity(jobClass.getSimpleName())
//                .startAt(info.getStartTime())
//                .build();
//    }
}
