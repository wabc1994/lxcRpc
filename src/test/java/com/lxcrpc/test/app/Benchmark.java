package com.lxcrpc.test.app;

import com.coderlau.client.RpcClient;
import com.coderlau.registry.ServiceDiscovery;
import com.lxcrpc.test.client.HelloService;


public class Benchmark {
    public static void main(String [] args)throws InterruptedException{
        ServiceDiscovery serviceDiscovery = new ServiceDiscovery("10.245.247.105:2181");
        final RpcClient rpcClient = new RpcClient(serviceDiscovery);
        int threadNum =10;
        final int requestNum =100;
        Thread[] threads = new Thread[threadNum];
        long startTime = System.currentTimeMillis();
        for(int i=0;i<threadNum;i++){
            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    for(int i=0;i<requestNum;i++){
                        final HelloService syncClient =rpcClient.create(HelloService.class);
                        String result = syncClient.hello(Integer.toString(i));
                        if(!result.equals("hello!"+i))
                            System.out.println("error = "+ result);
                    }
                }
            });
            threads[i].start();
        }
        for(int i=0;i<threads.length;i++){
            threads[i].join();
        }
        long timeCost = (System.currentTimeMillis()-startTime);
        String msg = String.format("Sync call total-time-cost:%sms, req/s=%s", timeCost, ((double) (requestNum * threadNum)) / timeCost * 1000);
        System.out.println(msg);
        rpcClient.stop();
    }
}
