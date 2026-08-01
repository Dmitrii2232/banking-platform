package com.bank.query.projection;

import com.bank.domain.common.ClientId;
import com.bank.domain.common.ProductId;
import com.bank.domain.product.BankProduct;
import com.bank.domain.product.ProductStatus;
import com.bank.eventstore.EventStore;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductProjection {

    private final JdbcTemplate jdbc;
    private final EventStore eventStore;

    public List<BankProduct> getClientProducts(ClientId clientId) {
        String sql = "SELECT id FROM products WHERE client_id = ?";
        List<String> ids = jdbc.queryForList(sql, String.class, clientId.getUuid());
        return ids.stream()
            .map(id -> eventStore.loadProduct(new ProductId(id)))
            .filter(java.util.Optional::isPresent)
            .map(java.util.Optional::get)
            .toList();
    }

    public List<BankProduct> getClientProductsByStatus(ClientId clientId, ProductStatus status) {
        String sql = "SELECT id FROM products WHERE client_id = ? AND status = ?";
        List<String> ids = jdbc.queryForList(sql, String.class, clientId.getUuid(), status.name());
        return ids.stream()
            .map(id -> eventStore.loadProduct(new ProductId(id)))
            .filter(java.util.Optional::isPresent)
            .map(java.util.Optional::get)
            .toList();
    }
}