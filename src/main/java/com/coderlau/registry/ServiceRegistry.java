package com.coderlau.registry;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ServiceRegistry {
    
    
    private static final Logger logger = LoggerFactory.getLogger(ServiceRegistry.class);
    private CountDownLatch latch = new CountDownLatch(1);
    private String registryAddress;

    public ServiceRegistry(String registryAddress) {
        this.registryAddress = registryAddress;
    }
    
    
    
    public void register(String data){
        if(data!=null){
            ZooKeeper zk = connectServer();
            if(zk!=null){
                AddRootNode(zk);
                createNode(zk,data);
            }
        }
    }

    private void createNode(ZooKeeper zk, String data) {
        try{
            byte[] bytes = data.getBytes();
            String path = zk.create(Constant.ZK_DATA_PAHT,bytes,ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);

        }catch (KeeperException e){
            logger.error(e.toString());
        }catch (InterruptedException e){
            logger.error(e.toString());
        }
    }


    private ZooKeeper connectServer(){
        ZooKeeper zk = null;
        try{
            zk = new ZooKeeper(registryAddress, Constant.ZK_SESSION_TIMEOUT, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    if(event.getState()==Event.KeeperState.SyncConnected){
                        latch.countDown();
                    }
                }
            });
            latch.await();
        }catch (IOException e){
            logger.error("",e);
        }catch (InterruptedException ex){
            logger.error("",ex);
        }
        return zk;
    }
    
    
    private  void AddRootNode(ZooKeeper zk){
        try {
            Stat s =zk.exists(Constant.ZK_REGISTRY_PATH,false);
            if(s==null){
                //参数分别是创建的节点路径、节点的数据、权限（此处对所有用户开放）、节点的类型（此处是持久节点）
                zk.create(Constant.ZK_REGISTRY_PATH,new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
            }
        }catch (KeeperException e){
            logger.error(e.toString());
        }catch (InterruptedException e){
            logger.error(e.toString());
        }
    }
}
