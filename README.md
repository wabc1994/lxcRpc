# 一个简单的rpc框架
总体流程如下所示
![总体框架图](https://github.com/wabc1994/lxcRpc/blob/master/%E9%A1%B9%E7%9B%AE%E8%A7%A3%E9%87%8A/picture/rpc.png)

## 包含的数据结构
### 客户端的请求消息结构一般需要包括以下的内容 
- 接口名称className

　　在我们的例子里接口名是“HelloWorldService”，如果不传，服务端就不知道调用哪个接口了；

- 方法名methodName

　　一个接口内可能有很多方法，如果不传方法名服务端也就不知道调用哪个方法；

- 参数类型&参数值

　　参数类型有很多，比如有bool、int、long、double、string、map、list，甚至如struct（class）；

　　以及相应的参数值；

- 超时时间timeout

- requestID，标识唯一请求id，在下面一节会详细描述requestID的用处。

### 同理服务端返回的消息结构一般包括以下内容。

- 返回值result

- 状态errro

- requestID 

## 模块分析
