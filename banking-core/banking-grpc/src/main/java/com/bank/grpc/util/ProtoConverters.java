package com.bank.grpc.util;

import com.bank.domain.common.Money;
import com.bank.grpc.common.MoneyProto;
import java.math.BigDecimal;

public class ProtoConverters {
    public static Money fromProto(MoneyProto proto) {
        return new Money(new BigDecimal(proto.getAmount()), proto.getCurrency());
    }

    public static MoneyProto toProto(Money money) {
        return MoneyProto.newBuilder().setAmount(money.getAmount().toPlainString()).setCurrency(money.getCurrency()).build();
    }
}