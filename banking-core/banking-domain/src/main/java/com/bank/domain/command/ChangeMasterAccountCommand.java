// banking-domain/src/main/java/com/bank/domain/command/ChangeMasterAccountCommand.java
package com.bank.domain.command;

import com.bank.domain.common.ClientId;
import com.bank.domain.common.ProductId;
import java.time.LocalDateTime;
import java.util.UUID;

public record ChangeMasterAccountCommand(
    ClientId clientId,
    ProductId newMasterProductId,
    String commandId,
    LocalDateTime timestamp
) implements Command {
    
    public ChangeMasterAccountCommand(ClientId clientId, ProductId newMasterProductId) {
        this(clientId, newMasterProductId, UUID.randomUUID().toString(), LocalDateTime.now());
    }
    
    @Override
    public String getCommandId() { return commandId; }
    
    @Override
    public ClientId getClientId() { return clientId; }
    
    @Override
    public LocalDateTime getTimestamp() { return timestamp; }
    
    public ProductId getNewMasterProductId() { return newMasterProductId; }
}