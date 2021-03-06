package com.coderlau.server;

import com.coderlau.protocol.RpcRequest;
import com.coderlau.protocol.RpcResponse;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * 使用RpcHandler 中处理RPC请求，只需要扩展netty 的simpleChannelInboundHandle抽象类，代码如下
 */

public class RpcHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private static final Logger logger = LoggerFactory.getLogger(RpcHandler.class);

    private final Map<String , Object>handleMap;

    public RpcHandler(Map<String, Object> handleMap) {
        this.handleMap = handleMap;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }
    @Override
    public void channelRead0(final ChannelHandlerContext ctx,final RpcRequest request) throws Exception {
        RpcServer.submit(new Runnable() {
            @Override
            public void run() {
                logger.debug("Receive request " + request.getRequestId());
                RpcResponse response = new RpcResponse();
                response.setRequestId(request.getRequestId());
                try {
                    Object result = handle(request);
                    response.setResult(result);
                } catch (Throwable t) {
                    response.setError(t.toString());
                    logger.error("RPC Server handle request error",t);
                }
                ctx.writeAndFlush(response).addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture channelFuture) throws Exception {
                        logger.debug("Send response for request " + request.getRequestId());
                    }
                });
            }
        });
    }

    private Object handle(RpcRequest request)  throws Throwable{
        String className =request.getClassName();

        Object serviceBean = handleMap.get(className);

        Class<?> serviceClass = serviceBean.getClass();

        String methodName = request.getMethodName();
        Class<?> [] parameterTypes =request.getParameterTypes();
        Object[] parameters =request.getParameters();
        logger.debug(serviceClass.getName());
        logger.debug(methodName);
        for(int i=0;i<parameterTypes.length;++i){
            logger.debug(parameterTypes[i].getName());
        }
        for(int i=0;i<parameterTypes.length;++i){
            logger.debug(parameters[i].toString());
        }
        FastClass serviceFastClass = FastClass.create(serviceClass);
        FastMethod serviceFastMethod = serviceFastClass.getMethod(methodName, parameterTypes);
        return serviceFastMethod.invoke(serviceBean, parameters);
    }
}
