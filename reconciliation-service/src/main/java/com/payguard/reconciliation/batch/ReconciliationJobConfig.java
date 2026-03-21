package com.payguard.reconciliation.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class ReconciliationJobConfig {

    private final ReconciliationTasklet reconciliationTasklet;

    @Bean
    public Job reconciliationJob(JobRepository jobRepository, Step reconciliationStep) {
        return new JobBuilder("reconciliationJob", jobRepository)
                .start(reconciliationStep)
                .build();
    }

    @Bean
    public Step reconciliationStep(JobRepository jobRepository, 
                                   PlatformTransactionManager transactionManager) {
        return new StepBuilder("reconciliationStep", jobRepository)
                .tasklet(reconciliationTasklet, transactionManager)
                .build();
    }
}