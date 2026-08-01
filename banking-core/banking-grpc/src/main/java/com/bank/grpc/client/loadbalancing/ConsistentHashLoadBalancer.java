package com.bank.grpc.client.loadbalancing;

import lombok.extern.slf4j.Slf4j;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.grpc.EquivalentAddressGroup;
import io.grpc.LoadBalancer;
import io.grpc.Status;

@Slf4j
public class ConsistentHashLoadBalancer extends LoadBalancer {
    private final Helper helper;
    private final Map<InetSocketAddress, Subchannel> subchannels = new ConcurrentHashMap<>();

    public ConsistentHashLoadBalancer(Helper helper) { this.helper = helper; }

    @Override
    public void handleResolvedAddresses(ResolvedAddresses resolvedAddresses) {
        for (EquivalentAddressGroup group : resolvedAddresses.getAddresses()) {
            InetSocketAddress addr = (InetSocketAddress) group.getAddresses().get(0);
            if (!subchannels.containsKey(addr)) {
                Subchannel subchannel = helper.createSubchannel(
                    CreateSubchannelArgs.newBuilder().setAddresses(List.of(new EquivalentAddressGroup(addr))).build());
                subchannel.start(state -> log.info("Subchannel: {}", state));
                subchannels.put(addr, subchannel);
            }
        }
    }

    @Override public void handleNameResolutionError(Status error) { log.error("DNS error: {}", error); }
    @Override public void shutdown() { subchannels.values().forEach(Subchannel::shutdown); }
}