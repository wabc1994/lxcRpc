package com.coderlau.protocol;

/**
 * 使用RpcRequest封装 RPC 请求，代码如下：
 *
 */
public class RpcRequest {
    private  String request;
    private String className;
    private  String methodName;
    private Class<?>[] parameterTypes;
    private Object[] parameters;

    public String getRequestId(){
        return request;
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public void setRequestID(String request) {
        this.request = request;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public void setParameterTypes(Class<?>[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }
}
