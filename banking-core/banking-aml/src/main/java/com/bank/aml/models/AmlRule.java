package com.bank.aml.models;

import java.math.BigDecimal;

public record AmlRule(
    String ruleId,
    String name,
    String description,
    BigDecimal threshold,
    String action,
    boolean enabled
) {}