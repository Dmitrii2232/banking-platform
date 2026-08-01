package com.bank.grpc.util;

import java.util.UUID;

import com.bank.domain.common.ClientId;
import com.bank.domain.common.ProductId;
import com.bank.grpc.common.ClientIdProto;
import com.bank.grpc.common.ProductIdProto;

public class GrpcUtils {
    public static ProductIdProto toProto(ProductId id) { return ProductIdProto.newBuilder().setId(id.toString()).build(); }
    public static ProductId fromProto(ProductIdProto proto) { return new ProductId(UUID.fromString(proto.getId())); }
    public static ClientIdProto toProto(ClientId id) { return ClientIdProto.newBuilder().setId(id.toString()).build(); }
    public static ClientId fromProto(ClientIdProto proto) { return new ClientId(UUID.fromString(proto.getId())); }

    public static String extractTraceId() {
        try { return com.bank.grpc.server.interceptors.Constants.TRACE_ID_CONTEXT_KEY.get(); }
        catch (Exception e) { return "unknown"; }
    }
}