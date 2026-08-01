package com.bank.query;

import com.bank.domain.common.ClientId;
import com.bank.domain.common.ProductId;
import com.bank.domain.product.BankProduct;
import com.bank.domain.product.ProductStatus;
import com.bank.eventstore.EventStore;
import com.bank.query.projection.ProductProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductQueryService {

    private final EventStore eventStore;
    private final ProductProjection productProjection;

    public Optional<BankProduct> getProduct(ProductId productId) {
        return eventStore.loadProduct(productId);
    }

    public List<BankProduct> getClientProducts(ClientId clientId) {
        return productProjection.getClientProducts(clientId);
    }

    public List<BankProduct> getClientProductsByStatus(ClientId clientId, ProductStatus status) {
        return productProjection.getClientProductsByStatus(clientId, status);
    }

    public boolean isProductActive(ProductId productId) {
        return getProduct(productId).map(p -> p.getStatus() == ProductStatus.ACTIVE).orElse(false);
    }
}