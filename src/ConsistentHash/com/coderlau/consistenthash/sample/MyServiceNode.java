package com.coderlau.consistenthash.sample;

import com.coderlau.consistenthash.ConsistenHashRouter;
import com.coderlau.consistenthash.Node;

import java.util.Arrays;

/**
 * a sample usage for routing a request to services based on requester ip
 */
public class MyServiceNode implements Node {
    private final String idc;
    private final String ip;
    private final int port;

    public MyServiceNode(String idc, String ip, int port) {
        this.idc = idc;
        this.ip = ip;
        this.port = port;
    }

    @Override
    public String toString() {
        return getKey();
    }



    @Override
    public String getKey() {
        return idc + "-"+ip+":"+port;
    }

    public static void main(String[] args) {
        //initialize 4 service node
        MyServiceNode node1 = new MyServiceNode("IDC1","127.0.0.1",8080);
        MyServiceNode node2 = new MyServiceNode("IDC1","127.0.0.1",8081);
        MyServiceNode node3 = new MyServiceNode("IDC1","127.0.0.1",8082);
        MyServiceNode node4 = new MyServiceNode("IDC1","127.0.0.1",8084);

        //hash them to hash ring

        ConsistenHashRouter<MyServiceNode> consistentHashRouter = new ConsistenHashRouter<>(Arrays.asList(node1,node2,node3,node4),10);

        //10 virtual node

        //we have 5 requester ip, we are trying them to route to one service node
        String requestIP1 = "192.168.0.1";
        String requestIP2 = "192.168.0.2";
        String requestIP3 = "192.168.0.3";
        String requestIP4 = "192.168.0.4";
        String requestIP5 = "192.168.0.5";

        goRoute(consistentHashRouter,requestIP1,requestIP2,requestIP3,requestIP4,requestIP5);
       //服务器的数目突然增多了 n+1
        MyServiceNode node5 = new MyServiceNode("IDC2","127.0.0.1",8080);
        //put new service online
        System.out.println("-------------putting new node online " +node5.getKey()+"------------");

        consistentHashRouter.addNode(node5,10);

        goRoute(consistentHashRouter,requestIP1,requestIP2,requestIP3,requestIP4,requestIP5);

    }

    private static void goRoute(ConsistenHashRouter<MyServiceNode> consistentHashRouter, String... requestIPs) {
        for(String requestIp :requestIPs){
            System.out.println(requestIp+ "is route to "+consistentHashRouter.routeNode(requestIp));
        }
    }

}
