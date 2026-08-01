package com.bank.antifraud.ml;

import com.bank.antifraud.models.Transaction;
import org.springframework.stereotype.Component;

@Component
public class FeatureExtractor {

    public double[] extract(Transaction transaction) {
        double[] features = new double[10];
        features[0] = Math.log10(transaction.getAmount().doubleValue() + 1) / 7.0;
        int hour = transaction.getTimestamp().getHour();
        features[1] = (hour >= 23 || hour <= 4) ? 1.0 : 0.0;
        features[2] = switch (transaction.getType()) {
            case "DEPOSIT" -> 0.1;
            case "WITHDRAWAL" -> 0.3;
            case "TRANSFER" -> 0.5;
            default -> 0.0;
        };
        double remainder = transaction.getAmount().remainder(new java.math.BigDecimal("1000")).doubleValue();
        features[3] = (remainder == 0 && transaction.getAmount().doubleValue() > 50000) ? 1.0 : 0.0;
        features[4] = transaction.getGeoLocation() != null ? 0.5 : 0.0;
        String desc = transaction.getDescription();
        features[5] = desc != null ? Math.min(desc.length() / 200.0, 1.0) : 0.0;
        features[6] = 0.0; features[7] = 0.0; features[8] = 0.0; features[9] = 0.0;
        return features;
    }
}