package com.bank.domain.accounting;

import com.bank.domain.event.Event;
import java.util.List;

@FunctionalInterface
public interface BookingRule {
    List<AccountingEntry> apply(Event event);
}