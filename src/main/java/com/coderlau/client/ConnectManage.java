package com.coderlau.client;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

//设计成单例模式

/**
 *
 *  RPC Connect Manage of ZooKeeper
 */
public class ConnectManage {
    private static final Logger logger = LoggerFactory.getLogger(ConnectManage.class);
    private volatile static ConnectManage connectManage;

    private EventLoopGroup eventLoopGroup = new NioEventLoopGroup(4);
    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(16, 16,
            600L, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(65536));
    private CopyOnWriteArrayList<RpcClientHandler> connectedHandlers = new CopyOnWriteArrayList<>();
    private Map<InetSocketAddress,RpcClientHandler> connectedServerNodes=new ConcurrentHashMap<>();
    private ReentrantLock lock = new ReentrantLock();
    private Condition connected = lock.newCondition();
    private long connectTimeoutMillis = 6000;
    private volatile boolean isRunning = true;
    
    private AtomicInteger roundRobin = new AtomicInteger(0);

    private ConnectManage(){
    }

    public static ConnectManage getInstance(){
            if(connectManage==null){
                synchronized (ConnectManage.class){
                    if(connectManage==null){
                        connectManage = new ConnectManage();
                    }
                }
            }
            return connectManage;
    }

    public void updateConnectedServer(List<String> allServerAddress){
        if(allServerAddress!=null){
            if(allServerAddress.size()>0){
                // Get available server node
                //update local serverNodes cache
                HashSet<InetSocketAddress> newAllServerNodeSet = new HashSet<InetSocketAddress>();
                for (int i = 0; i < allServerAddress.size(); ++i) {
                    String[] array = allServerAddress.get(i).split(":");
                    // Should check IP and port
                    if(array.length==2){
                        String host = array[0];
                        int port = Integer.parseInt(array[1]);
                        final InetSocketAddress  remotePeer = new InetSocketAddress(host, port);
                        newAllServerNodeSet.add(remotePeer);
                    }
            }
            for(final InetSocketAddress serverNodeAddress:newAllServerNodeSet){
                if(!connectedServerNodes.keySet().contains(serverNodeAddress)){
                    connectedServerNodes(serverNodeAddress);
                }
                }
            }
        }
    }

    private void connectedServerNodes(final InetSocketAddress remotePeer){
        threadPoolExecutor.submit(new Runnable() {
            @Override
            public void run() {
                Bootstrap b = new Bootstrap();
                b.group(eventLoopGroup).channel(NioSocketChannel.class).handler(new RpcClientInitializer());
                ChannelFuture channelFuture= b.connect(remotePeer);
                channelFuture.addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture channelFuture) throws Exception {
                        if(channelFuture.isCancelled()){
                            logger.debug("Successfully connect to remote server. remote peer = " + remotePeer);
                            RpcClientHandler handler = channelFuture.channel().pipeline().get(RpcClientHandler.class);
                            addHandler(handler);
                        }
                    }
                });
            }
        });
    }

    private void addHandler(RpcClientHandler handler){
        connectedHandlers.add(handler);
        InetSocketAddress remoteAddress = (InetSocketAddress) handler.getChannel().remoteAddress();
        connectedServerNodes.put(remoteAddress,handler);
        signalAvailableHandle();

    }

    private void signalAvailableHandle(){
        lock.lock();
        try{
            connected.signalAll();
        }finally {
            lock.unlock();
        }
    }

    private  boolean waitiongForHandler() throws InterruptedException{
        lock.lock();
        try{
            return connected.await(this.connectTimeoutMillis,TimeUnit.MILLISECONDS);
        }finally {
            lock.unlock();
        }
    }

    public RpcClientHandler chooseHandler(){
        int size = connectedHandlers.size();
        while(isRunning && size<=0){
            try{
                boolean available = waitiongForHandler();
                if(available){
                    size = connectedHandlers.size();
                }
            }catch (InterruptedException e){
                logger.error("Waiting for available node is interrupted! ", e);
                throw new RuntimeException("Can't connect any servers!", e);
            }
        }
        int index = (roundRobin.getAndAdd(1) + size) % size;
        return connectedHandlers.get(index);
    }


    public void stop(){
        isRunning = false;
        for(int i=0;i<connectedHandlers.size();++i){
            RpcClientHandler connectedServerHandler = connectedHandlers.get(i);
            connectedServerHandler.close();
        }
        signalAvailableHandle();
        threadPoolExecutor.shutdown();
        eventLoopGroup.shutdownGracefully();
    }

}
