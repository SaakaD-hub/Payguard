package com.payguard.reconciliation.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Reconciliation Job Scheduler
 * Runs daily at 11:59 PM
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ReconciliationJobScheduler {

    private final JobLauncher jobLauncher;
    private final Job reconciliationJob;

    @Scheduled(cron = "0 59 23 * * ?") // 11:59 PM every day
    public void runReconciliation() {
        try {
            log.info("Starting scheduled reconciliation job");
            
            JobParameters params = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();
            
            jobLauncher.run(reconciliationJob, params);
            
            log.info("Reconciliation job completed successfully");
            
        } catch (Exception e) {
            log.error("Reconciliation job failed", e);
        }
    }
}