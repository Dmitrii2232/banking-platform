// 27. Event.java
package com.bank.domain.event;
import java.time.LocalDateTime;

import com.bank.domain.common.ClientId;
import com.bank.domain.common.EventId;
import com.bank.domain.common.Money;
import com.bank.domain.common.ProductId;
public interface Event {
    EventId getEventId();
    ProductId getProductId();
    ClientId getClientId();
    Money getAmount();
    LocalDateTime getTimestamp();
}