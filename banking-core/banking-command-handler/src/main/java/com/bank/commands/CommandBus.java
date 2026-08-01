// banking-command-handler/src/main/java/com/bank/commands/CommandBus.java
package com.bank.commands;

import com.bank.domain.command.AccrueInterestCommand;
import com.bank.domain.command.ChangeMasterAccountCommand;
import com.bank.domain.command.CloseProductCommand;
import com.bank.domain.command.Command;
import com.bank.domain.command.DepositCashCommand;
import com.bank.domain.command.MakePaymentCommand;
import com.bank.domain.command.OpenProductCommand;
import com.bank.domain.command.WithdrawCashCommand;
import com.bank.domain.event.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommandBus {

    private final DepositCommandHandler depositHandler;
    private final WithdrawalCommandHandler withdrawalHandler;
    private final PaymentCommandHandler paymentHandler;
    private final InterestAccrualHandler interestHandler;
    private final ProductManagementHandler productHandler;

    public List<Event> dispatch(Command command) {
        log.debug("Диспетчеризация команды: {}", command.getClass().getSimpleName());
        return switch (command) {
            case DepositCashCommand cmd -> depositHandler.handle(cmd);
            case WithdrawCashCommand cmd -> withdrawalHandler.handle(cmd);
            case MakePaymentCommand cmd -> paymentHandler.handle(cmd);
            case AccrueInterestCommand cmd -> interestHandler.handle(cmd);
            case OpenProductCommand cmd -> productHandler.handle(cmd);
            case CloseProductCommand cmd -> productHandler.handle(cmd);
            case ChangeMasterAccountCommand cmd -> productHandler.handleChangeMaster(cmd);
            default -> throw new IllegalArgumentException("Неизвестная команда: " + command.getClass());
        };
    }

    public CompletableFuture<List<Event>> dispatchAsync(Command command) {
        return CompletableFuture.supplyAsync(() -> dispatch(command));
    }
}