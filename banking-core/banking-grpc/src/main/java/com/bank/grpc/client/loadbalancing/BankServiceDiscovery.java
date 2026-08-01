package com.bank.grpc.client.loadbalancing;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@Component
public class BankServiceDiscovery {

    private final List<ServiceInstance> instances = new CopyOnWriteArrayList<>();

    public BankServiceDiscovery() {
        instances.add(new ServiceInstance("transaction-service", "localhost", 9091));
        instances.add(new ServiceInstance("fraud-service", "localhost", 9092));
        instances.add(new ServiceInstance("accounting-service", "localhost", 9093));
        instances.add(new ServiceInstance("product-service", "localhost", 9094));
    }

    public InetSocketAddress resolve(String serviceName) {
        List<ServiceInstance> found = instances.stream().filter(i -> i.serviceName().equals(serviceName)).toList();
        if (found.isEmpty()) throw new RuntimeException("Нет инстансов: " + serviceName);
        ServiceInstance instance = found.get((int) (System.currentTimeMillis() % found.size()));
        return new InetSocketAddress(instance.host(), instance.port());
    }

    public record ServiceInstance(String serviceName, String host, int port) {}
}