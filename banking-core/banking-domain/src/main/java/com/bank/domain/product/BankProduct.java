package com.bank.domain.product;

import com.bank.domain.command.Command;
import com.bank.domain.common.ClientId;
import com.bank.domain.common.EventId;
import com.bank.domain.common.Money;
import com.bank.domain.common.ProductId;
import com.bank.domain.event.Event;
import com.bank.domain.event.ProductClosedEvent;
import com.bank.domain.event.ProductOpenedEvent;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public abstract class BankProduct {
    
    protected final ProductId id;
    protected final ClientId clientId;
    protected Money balance;
    protected ProductStatus status;
    protected ProductTerms terms;
    protected LocalDateTime createdAt;
    protected LocalDateTime updatedAt;
    protected long version;
    protected boolean isMaster;
    
    protected BankProduct(ProductId id, ClientId clientId, ProductTerms terms) {
        this.id = Objects.requireNonNull(id, "ProductId обязателен");
        this.clientId = Objects.requireNonNull(clientId, "ClientId обязателен");
        this.terms = Objects.requireNonNull(terms, "ProductTerms обязательны");
        this.balance = Money.ZERO_RUB;
        this.status = ProductStatus.DRAFT;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.version = 0;
        this.isMaster = false;
    }
    
    public abstract List<Event> apply(Command cmd);
    
    public abstract void replay(Event event);
    
    /**
     * Проверка владения продуктом — защита от IDOR.
     * Вызывается в начале каждого apply() в наследниках.
     */
    protected void assertOwnedBy(ClientId actor) {
        if (!this.clientId.equals(actor)) {
            throw new com.bank.domain.exception.ProductBlockedException(
                "Продукт " + id.toString().substring(0, Math.min(8, id.toString().length())) 
                + " не принадлежит клиенту " + actor.toString().substring(0, Math.min(8, actor.toString().length())));
        }
    }
    
    public List<Event> activate() {
        if (this.status != ProductStatus.DRAFT) {
            throw new IllegalStateException("Активировать можно только DRAFT продукт. Текущий: " + this.status);
        }
        this.status = ProductStatus.ACTIVE;
        touch();
        return List.of(new ProductOpenedEvent(
            EventId.generate(),
            this.id,
            this.clientId,
            this.balance,
            this.getClass().getSimpleName(),
            this.terms,
            LocalDateTime.now()
        ));
    }
    
    public void block(String reason) {
        if (this.status == ProductStatus.CLOSED) {
            throw new IllegalStateException("Нельзя заблокировать закрытый продукт");
        }
        this.status = ProductStatus.BLOCKED;
        touch();
    }
    
    public void unblock() {
        if (this.status != ProductStatus.BLOCKED) {
            throw new IllegalStateException("Продукт не заблокирован. Текущий статус: " + this.status);
        }
        this.status = ProductStatus.ACTIVE;
        touch();
    }
    
    public List<Event> close() {
        if (this.status == ProductStatus.CLOSED) {
            throw new IllegalStateException("Продукт уже закрыт");
        }
        this.status = ProductStatus.CLOSED;
        touch();
        return List.of(new ProductClosedEvent(
            EventId.generate(),
            this.id,
            this.clientId,
            this.balance,
            LocalDateTime.now()
        ));
    }
    
    public void setMaster(boolean master) {
        this.isMaster = master;
        touch();
    }
    
    public boolean isMaster() {
        return isMaster;
    }
    
    public boolean canBeMaster() {
        return this instanceof CurrentAccount && this.status == ProductStatus.ACTIVE;
    }
    
    @SuppressWarnings("incomplete-switch")
    protected void assertOperable() {
        switch (this.status) {
            case BLOCKED:
                throw new com.bank.domain.exception.ProductBlockedException(
                    "Продукт " + id + " заблокирован");
            case CLOSED:
                throw new com.bank.domain.exception.ProductBlockedException(
                    "Продукт " + id + " закрыт");
            case FROZEN:
                throw new com.bank.domain.exception.ProductBlockedException(
                    "Продукт " + id + " заморожен");
            case DRAFT:
                throw new com.bank.domain.exception.ProductBlockedException(
                    "Продукт " + id + " не активирован");
        }
    }
    
    protected void touch() {
        this.updatedAt = LocalDateTime.now();
        this.version++;
    }
    
    public ProductId getId() { return id; }
    public ClientId getClientId() { return clientId; }
    public Money getBalance() { return balance; }
    public ProductStatus getStatus() { return status; }
    public ProductTerms getTerms() { return terms; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public long getVersion() { return version; }
    
    @Override
    public String toString() {
        return String.format("%s[id=%s, client=%s, balance=%s, status=%s, master=%s, v=%d]",
            this.getClass().getSimpleName(),
            id.toString().substring(0, Math.min(8, id.toString().length())),
            clientId.toString().substring(0, Math.min(8, clientId.toString().length())),
            balance,
            status,
            isMaster,
            version);
    }
}