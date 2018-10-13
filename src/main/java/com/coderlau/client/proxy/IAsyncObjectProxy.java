package com.coderlau.client.proxy;

import com.coderlau.client.RPCFuture;

public interface IAsyncObjectProxy {
    public RPCFuture call(String funcName, Object... args);
}
