package com.payguard.fraud.service;

import ai.onnxruntime.*;
import com.payguard.fraud.dto.FeatureVector;
import com.payguard.fraud.exception.FraudEngineException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * Model Inference Service - ONNX Runtime
 * Loads ML model and performs inference
 */
@Service
@Slf4j
public class ModelInferenceService {

    @Value("${fraud.model.path:classpath:models/fraud_model.onnx}")
    private Resource modelResource;

    @Value("${fraud.model.version:1.0.0}")
    private String modelVersion;

    private OrtSession session;
    private OrtEnvironment env;

    @PostConstruct
    public void loadModel() {
        try {
            log.info("Loading ONNX model from: {}", modelResource.getURI());
            
            env = OrtEnvironment.getEnvironment();
            OrtSession.SessionOptions opts = new OrtSession.SessionOptions();
            
            // Load model
            session = env.createSession(modelResource.getFile().getAbsolutePath(), opts);
            
            log.info("ONNX model loaded successfully. Version: {}", modelVersion);
            log.info("Model inputs: {}", session.getInputNames());
            log.info("Model outputs: {}", session.getOutputNames());
            
        } catch (Exception e) {
            log.error("Failed to load ONNX model - using fallback", e);
            // Don't throw - service will use fallback scoring
        }
    }

    /**
     * Perform inference on feature vector
     * Returns fraud probability (0.0 - 1.0)
     */
    public float predict(FeatureVector features) {
        if (session == null) {
            log.warn("ONNX model not loaded, using fallback scoring");
            return fallbackScore(features);
        }
        
        try {
            long startTime = System.currentTimeMillis();
            
            // Prepare input tensor
            float[] inputData = features.toFloatArray();
            long[] shape = {1, 12}; // Batch size 1, 12 features
            
            OnnxTensor inputTensor = OnnxTensor.createTensor(
                    env, 
                    FloatBuffer.wrap(inputData), 
                    shape
            );
            
            Map<String, OnnxTensor> inputs = new HashMap<>();
            inputs.put("float_input", inputTensor);
            
            // Run inference
            OrtSession.Result results = session.run(inputs);
            
            // Extract prediction
            float[][] output = (float[][]) results.get(0).getValue();
            float fraudProbability = output[0][0];
            
            long latency = System.currentTimeMillis() - startTime;
            log.debug("Model inference completed in {}ms. Score: {}", latency, fraudProbability);
            
            inputTensor.close();
            results.close();
            
            return fraudProbability;
            
        } catch (Exception e) {
            log.error("ONNX inference failed, using fallback", e);
            return fallbackScore(features);
        }
    }

    /**
     * Fallback scoring when model is unavailable
     * Simple rule-based scoring
     */
    private float fallbackScore(FeatureVector features) {
        float score = 0.0f;
        
        // High amount = higher risk
        if (features.getTransactionAmount() > 1.0) score += 0.2f;
        
        // Off-hours = higher risk
        if (features.getIsOffHours() == 1) score += 0.1f;
        
        // Weekend = slight risk
        if (features.getIsWeekend() == 1) score += 0.05f;
        
        // High customer activity = lower risk
        if (features.getCustomerTxCountLast24h() > 5) score -= 0.1f;
        
        // Large deviation from average = higher risk
        if (Math.abs(features.getAmountDeviationFromAvg()) > 2.0) score += 0.15f;
        
        return Math.max(0.0f, Math.min(1.0f, score));
    }

    public String getModelVersion() {
        return modelVersion;
    }
}