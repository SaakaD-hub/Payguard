package com.payguard.reconciliation.batch;

import com.payguard.reconciliation.dto.ReconciliationReport;
import com.payguard.reconciliation.service.ReconciliationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Reconciliation Tasklet - Executes daily reconciliation
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ReconciliationTasklet implements Tasklet {

    private final ReconciliationService reconciliationService;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        log.info("Executing reconciliation tasklet");
        
        // Get yesterday's date (typically reconcile previous day)
        LocalDate reconciliationDate = LocalDate.now().minusDays(1);
        
        // Mock internal totals (in production, query from transaction database)
        BigDecimal internalTotal = BigDecimal.valueOf(12345.67);
        int transactionCount = 42;
        
        // Perform reconciliation
        ReconciliationReport report = reconciliationService.reconcileDay(
                reconciliationDate, 
                internalTotal, 
                transactionCount
        );
        
        log.info("Reconciliation completed for {}: Status={}, Difference={}", 
                reconciliationDate, report.getStatus(), report.getDifference());
        
        return RepeatStatus.FINISHED;
    }
}