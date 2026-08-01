package com.bank.grpc.server.interceptors;

import io.grpc.Context;

public class Constants {
    public static final Context.Key<String> TRACE_ID_CONTEXT_KEY = Context.key("trace-id");
    public static final Context.Key<String> SPAN_ID_CONTEXT_KEY = Context.key("span-id");
    public static final Context.Key<String> CLIENT_ID_CONTEXT_KEY = Context.key("client-id");
    public static final Context.Key<String> AUTH_METHOD_CONTEXT_KEY = Context.key("auth-method");
}