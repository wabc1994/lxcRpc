## 为何需要zookeeper作为注册中心？
如何让别人使用我们的服务呢？有同学说很简单嘛，告诉使用者服务的IP以及端口就可以了啊。确实是这样，这里问题的关键在于是自动告知还是人肉告知。
人肉告知的方式：如果你发现你的服务一台机器不够，要再添加一台，这个时候就要告诉调用者我现在有两个ip了，你们要轮询调用来实现负载均衡；调用者咬咬牙改了，结果某天一台机器挂了，调用者发现服务有一半不可用，他又只能手动修改代码来删除挂掉那台机器的ip。现实生产环境当然不会使用人肉方式。
有没有一种方法能实现自动告知，即机器的增添、剔除对调用方透明，调用者不再需要写死服务提供方地址？当然可以，现如今zookeeper被广泛用于实现服务自动注册与发现功能！
简单来讲，zookeeper可以充当一个服务注册表（Service Registry），让多个服务提供者形成一个集群，让服务消费者通过服务注册表获取具体的服务访问地址（ip+端口）去访问具体的服务提供者。如下图所示
![图片链接](https://github.com/wabc1994/lxcRpc/blob/master/%E9%A1%B9%E7%9B%AE%E8%A7%A3%E9%87%8A/picture/zookeeper.png)
具体来说，zookeeper就是个分布式文件系统，每当一个服务提供者部署后都要将自己的服务注册到zookeeper的某一路径上: /{service}/{version}/{ip:port}, 比如我们的HelloWorldService部署到两台机器，那么zookeeper上就会创建两条目录：分别为/HelloWorldService/1.0.0/100.19.20.01:16888  /HelloWorldService/1.0.0/100.19.20.02:16888。


讲解通过zookeeper作为注册中心实现分布式系统中服务注册与发现的具体实现
## 原理
### 注册根节点情况
创建服务节点(这里是持久节点)，而实际保存服务信息的节点是服务节点下子节点，子节点是临时顺序的节点
### 服务注册 ServiceRegister
利用zookeeper临时顺序节点的性质，每个应用服务在zookeeper 上创建临时顺序的节点就（这个节点就是后面的服务节点），这个过程也叫做服务注册功能，为一个服务注册一个地址；
每当一个服务提供者部注册都要将自己的服务注册到zookeeper的某一路径上: /{service}/{version}/{ip:port}, 
createNode()
### 服务发现  ServiceDiscovery
要创造一个服务发现列子
```java
ServiceDiscovery serviceDiscovery =new ServiceDiscovery("127.0.0.1:2181");
rpcClient = new RpcClient(服务发现)
rpcClient.call(服务类)
```
服务消费者去相应服务节点下取出服务节点的信息，从而实现服务发现功能。
服务发现调用getServerinfo()方法传入servername即可获得服务提供者的ip+port，然后去调用服务即可；
[参考来源一](http://www.cnblogs.com/LBSer/p/4853234.html)
[参考来源二](https://my.oschina.net/huangyong/blog/361751)