package com.bank.aml.models;

import java.io.Serializable;
import java.time.LocalDateTime;

public record SanctionedPerson(
    String clientId,
    String reason,
    String source,
    LocalDateTime addedDate
) implements Serializable {}