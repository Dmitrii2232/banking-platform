package com.bank.domain.product;

import com.bank.domain.common.Money;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductTerms {
    
    private final String productName;
    private final BigDecimal interestRate;
    private final String rateType;
    private final Money minBalance;
    private final Money maxBalance;
    private final Integer termMonths;
    private final Integer minTermMonths;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final String interestPaymentFrequency;
    private final boolean capitalization;
    private final boolean replenishable;
    private final boolean partialWithdrawal;
    private final Money monthlyFee;
    private final Integer gracePeriodDays;
    private final Money creditLimit;
    private final Map<String, String> additionalParams;
    
    @JsonCreator
    public ProductTerms(
            @JsonProperty("productName") String productName,
            @JsonProperty("interestRate") BigDecimal interestRate,
            @JsonProperty("rateType") String rateType,
            @JsonProperty("minBalance") Money minBalance,
            @JsonProperty("maxBalance") Money maxBalance,
            @JsonProperty("termMonths") Integer termMonths,
            @JsonProperty("minTermMonths") Integer minTermMonths,
            @JsonProperty("startDate") LocalDate startDate,
            @JsonProperty("endDate") LocalDate endDate,
            @JsonProperty("interestPaymentFrequency") String interestPaymentFrequency,
            @JsonProperty("capitalization") boolean capitalization,
            @JsonProperty("replenishable") boolean replenishable,
            @JsonProperty("partialWithdrawal") boolean partialWithdrawal,
            @JsonProperty("monthlyFee") Money monthlyFee,
            @JsonProperty("gracePeriodDays") Integer gracePeriodDays,
            @JsonProperty("creditLimit") Money creditLimit,
            @JsonProperty("additionalParams") Map<String, String> additionalParams) {
        this.productName = productName;
        this.interestRate = interestRate != null ? interestRate : BigDecimal.ZERO;
        this.rateType = rateType != null ? rateType : "FIXED";
        this.minBalance = minBalance != null ? minBalance : Money.ZERO_RUB;
        this.maxBalance = maxBalance;
        this.termMonths = termMonths;
        this.minTermMonths = minTermMonths;
        this.startDate = startDate;
        this.endDate = endDate;
        this.interestPaymentFrequency = interestPaymentFrequency != null ? interestPaymentFrequency : "MONTHLY";
        this.capitalization = capitalization;
        this.replenishable = replenishable;
        this.partialWithdrawal = partialWithdrawal;
        this.monthlyFee = monthlyFee != null ? monthlyFee : Money.ZERO_RUB;
        this.gracePeriodDays = gracePeriodDays;
        this.creditLimit = creditLimit;
        this.additionalParams = additionalParams != null ? new HashMap<>(additionalParams) : new HashMap<>();
    }
    
    public static Builder builder() { return new Builder(); }
    
    public String getProductName() { return productName; }
    public BigDecimal getInterestRate() { return interestRate; }
    public String getRateType() { return rateType; }
    public Money getMinBalance() { return minBalance; }
    public Money getMaxBalance() { return maxBalance; }
    public Integer getTermMonths() { return termMonths; }
    public Integer getMinTermMonths() { return minTermMonths; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public String getInterestPaymentFrequency() { return interestPaymentFrequency; }
    public boolean isCapitalization() { return capitalization; }
    public boolean isReplenishable() { return replenishable; }
    public boolean isPartialWithdrawal() { return partialWithdrawal; }
    public Money getMonthlyFee() { return monthlyFee; }
    public Integer getGracePeriodDays() { return gracePeriodDays; }
    public Money getCreditLimit() { return creditLimit; }
    public Map<String, String> getAdditionalParams() { return new HashMap<>(additionalParams); }
    
    public static class Builder {
        private String productName;
        private BigDecimal interestRate;
        private String rateType;
        private Money minBalance;
        private Money maxBalance;
        private Integer termMonths;
        private Integer minTermMonths;
        private LocalDate startDate;
        private LocalDate endDate;
        private String interestPaymentFrequency;
        private boolean capitalization;
        private boolean replenishable;
        private boolean partialWithdrawal;
        private Money monthlyFee;
        private Integer gracePeriodDays;
        private Money creditLimit;
        private Map<String, String> additionalParams;
        
        public Builder productName(String v) { productName = v; return this; }
        public Builder interestRate(BigDecimal v) { interestRate = v; return this; }
        public Builder rateType(String v) { rateType = v; return this; }
        public Builder minBalance(Money v) { minBalance = v; return this; }
        public Builder maxBalance(Money v) { maxBalance = v; return this; }
        public Builder termMonths(Integer v) { termMonths = v; return this; }
        public Builder minTermMonths(Integer v) { minTermMonths = v; return this; }
        public Builder startDate(LocalDate v) { startDate = v; return this; }
        public Builder endDate(LocalDate v) { endDate = v; return this; }
        public Builder interestPaymentFrequency(String v) { interestPaymentFrequency = v; return this; }
        public Builder capitalization(boolean v) { capitalization = v; return this; }
        public Builder replenishable(boolean v) { replenishable = v; return this; }
        public Builder partialWithdrawal(boolean v) { partialWithdrawal = v; return this; }
        public Builder monthlyFee(Money v) { monthlyFee = v; return this; }
        public Builder gracePeriodDays(Integer v) { gracePeriodDays = v; return this; }
        public Builder creditLimit(Money v) { creditLimit = v; return this; }
        public Builder additionalParams(Map<String, String> v) { additionalParams = v; return this; }
        
        public ProductTerms build() {
            return new ProductTerms(productName, interestRate, rateType, minBalance, maxBalance, termMonths, minTermMonths, startDate, endDate, interestPaymentFrequency, capitalization, replenishable, partialWithdrawal, monthlyFee, gracePeriodDays, creditLimit, additionalParams);
        }
    }
}