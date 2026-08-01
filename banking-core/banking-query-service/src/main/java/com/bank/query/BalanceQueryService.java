package com.bank.query;

import com.bank.domain.common.Money;
import com.bank.domain.common.ProductId;
import com.bank.eventstore.EventStore;
import com.bank.query.projection.BalanceProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BalanceQueryService {

    private final BalanceProjection balanceProjection;
    private final EventStore eventStore;

    public Optional<Money> getCurrentBalance(ProductId productId) {
        Optional<Money> fromProjection = balanceProjection.getBalance(productId);
        if (fromProjection.isPresent()) return fromProjection;
        return eventStore.loadProduct(productId).map(product -> product.getBalance());
    }

    public Money getAvailableBalance(ProductId productId) {
        return eventStore.loadProduct(productId)
            .map(product -> {
                if (product instanceof com.bank.domain.product.CurrentAccount account)
                    return account.getAvailableBalance();
                return product.getBalance();
            })
            .orElse(Money.ZERO_RUB);
    }
}