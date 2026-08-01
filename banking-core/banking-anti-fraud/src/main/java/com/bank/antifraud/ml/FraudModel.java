package com.bank.antifraud.ml;

import com.bank.antifraud.models.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Random;

@Slf4j
@Component
public class FraudModel {

    @Value("${fraud.ml-model-path:/models/fraud-model.pmml}")
    private String modelPath;

    private final FeatureExtractor featureExtractor;
    private final Random random = new Random(42);
    private boolean modelLoaded = false;

    public FraudModel(FeatureExtractor featureExtractor) {
        this.featureExtractor = featureExtractor;
    }

    public double predict(Transaction transaction) {
        if (!modelLoaded) loadModel();
        double[] features = featureExtractor.extract(transaction);
        double score = simulatePrediction(features);
        log.debug("FraudModel.predict: score={}", score);
        return score;
    }

    private void loadModel() {
        log.info("Загрузка ML-модели из {}", modelPath);
        modelLoaded = true;
    }

    private double simulatePrediction(double[] features) {
        double baseScore = 0.0;
        if (features.length > 0) baseScore += features[0] * 0.3;
        if (features.length > 1) baseScore += features[1] * 0.25;
        if (features.length > 2) baseScore += features[2] * 0.15;
        if (features.length > 3) baseScore += features[3] * 0.2;
        baseScore += random.nextGaussian() * 0.05;
        return Math.max(0.0, Math.min(1.0, baseScore));
    }
}