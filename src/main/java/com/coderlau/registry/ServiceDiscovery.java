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
        // 使用之前都要进行判断是否为空处理
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
        // 像这样的代码都是这样来写法， 创建并返回某个东西， 一开始先定义一个空的东西, 然后执行函数进行创建，
        ZooKeeper zk= null;
        try{
            // 在主程序当中创建一个zk , 需要依赖参数(资源或条件),
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
