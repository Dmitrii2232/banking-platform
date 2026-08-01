package com.bank.antifraud.models;

import java.util.Map;

public class FraudScore {
    private final double totalScore;
    private final Map<String, Double> ruleScores;
    private final double mlScore;
    private final RiskLevel riskLevel;
    private final String decision;

    private FraudScore(Builder builder) {
        this.totalScore = builder.totalScore;
        this.ruleScores = builder.ruleScores;
        this.mlScore = builder.mlScore;
        this.riskLevel = builder.riskLevel;
        this.decision = builder.decision;
    }

    public static Builder builder() { return new Builder(); }

    public double getTotalScore() { return totalScore; }
    public Map<String, Double> getRuleScores() { return ruleScores; }
    public double getMlScore() { return mlScore; }
    public RiskLevel getRiskLevel() { return riskLevel; }
    public String getDecision() { return decision; }

    public static class Builder {
        private double totalScore;
        private Map<String, Double> ruleScores;
        private double mlScore;
        private RiskLevel riskLevel;
        private String decision;

        public Builder totalScore(double v) { totalScore = v; return this; }
        public Builder ruleScores(Map<String, Double> v) { ruleScores = v; return this; }
        public Builder mlScore(double v) { mlScore = v; return this; }
        public Builder riskLevel(RiskLevel v) { riskLevel = v; return this; }
        public Builder decision(String v) { decision = v; return this; }
        public FraudScore build() { return new FraudScore(this); }
    }
}