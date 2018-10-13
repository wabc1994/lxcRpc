package com.coderlau.registry;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
/**
 * 什么情况下需要使用volatile 有可能对这个数据进行改变对情况下
 * 同样使用 ZooKeeper 实现服务发现功能
 */
public class ServiceDiscovery {

    private static final Logger logger = LoggerFactory.getLogger(ServiceDiscovery.class);

    private CountDownLatch latch = new CountDownLatch(1);

    private volatile List<String> dataList =new ArrayList<>();

    private String registryAddress;

    private ZooKeeper zooKeeper;

    public ServiceDiscovery( String registryAddress) {
        this.registryAddress = registryAddress;
        zooKeeper=connectServer();
        if(zooKeeper!=null){
            watchNode(zooKeeper);
        }
    }

    private void watchNode(final  ZooKeeper zooKeeper) {
        try{
            List<String> nodeList = zooKeeper.getChildren(Constant.ZK_REGISTRY_PATH, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                    if(watchedEvent.getType()==Event.EventType.NodeChildrenChanged){
                        watchNode(zooKeeper);
                    }
                }
            });
            List<String> dataList =new ArrayList<>();
            for(String node:nodeList){
                byte[] bytes = zooKeeper.getData(Constant.ZK_REGISTRY_PATH+"/"+node,false,null);
            }
        }catch (InterruptedException | KeeperException e){
            logger.error("",e);
        }
    }


    private ZooKeeper connectServer() {
        ZooKeeper zk= null;
        try{
            zk =new ZooKeeper(registryAddress, Constant.ZK_SESSION_TIMEOUT, new Watcher() {
                @Override
                public void process(WatchedEvent watchedEvent) {
                     if(watchedEvent.getState()==Event.KeeperState.SyncConnected){
                         latch.countDown();
                     }
                }
            });
            latch.await();
        }catch (IOException |InterruptedException e){
            logger.error("",e);
        }
    return zooKeeper;
    }

    public void  stop(){
        try{
            zooKeeper.close();
        }catch (InterruptedException e){
            logger.error(" ", e);
        }
    }
}
