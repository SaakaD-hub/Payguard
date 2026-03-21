package com.payguard.fraud.dto;

import lombok.*;

/**
 * Feature Vector for ML Model
 * Contains 12 features as per specification
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeatureVector {
    
    // 1. Transaction amount (normalized)
    private Double transactionAmount;
    
    // 2. Hour of day (0-23)
    private Integer hourOfDay;
    
    // 3. Day of week (0-6)
    private Integer dayOfWeek;
    
    // 4. Merchant transaction count (last 24h)
    private Integer merchantTxCountLast24h;
    
    // 5. Merchant total volume (last 24h)
    private Double merchantVolumeLast24h;
    
    // 6. Customer transaction count (last 24h)
    private Integer customerTxCountLast24h;
    
    // 7. Amount deviation from merchant average
    private Double amountDeviationFromAvg;
    
    // 8. Time since last transaction (minutes)
    private Integer timeSinceLastTx;
    
    // 9. Cross-border transaction flag (0 or 1)
    private Integer isCrossBorder;
    
    // 10. High-risk merchant category (0 or 1)
    private Integer isHighRiskCategory;
    
    // 11. Weekend transaction flag (0 or 1)
    private Integer isWeekend;
    
    // 12. Off-hours transaction flag (0 or 1)
    private Integer isOffHours;
    
    /**
     * Convert to float array for ONNX model input
     */
    public float[] toFloatArray() {
        return new float[] {
            transactionAmount.floatValue(),
            hourOfDay.floatValue(),
            dayOfWeek.floatValue(),
            merchantTxCountLast24h.floatValue(),
            merchantVolumeLast24h.floatValue(),
            customerTxCountLast24h.floatValue(),
            amountDeviationFromAvg.floatValue(),
            timeSinceLastTx.floatValue(),
            isCrossBorder.floatValue(),
            isHighRiskCategory.floatValue(),
            isWeekend.floatValue(),
            isOffHours.floatValue()
        };
    }
}