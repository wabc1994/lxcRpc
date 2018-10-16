package com.lxcrpc.test.server;

import com.coderlau.server.RpcService;
import com.lxcrpc.test.client.HelloService;
import com.lxcrpc.test.client.Person;

@RpcService(HelloService.class)
public class HelloServiceImpl  implements HelloService {
    public HelloServiceImpl() {
    }

    @Override
    public String hello(Person person) {
        return "Hello! " + person.getFirstName() + " " + person.getLastName();

    }

    @Override
    public String hello(String name) {
        return "Hello! " + name;
    }
}
