package com.bank.domain.command;

import com.bank.domain.common.ClientId;
import java.time.LocalDateTime;

public interface Command {
    String getCommandId();
    ClientId getClientId();
    LocalDateTime getTimestamp();
}