package com.lxcrpc.test.app;

import com.coderlau.client.RPCFuture;
import com.coderlau.client.RpcClient;
import com.coderlau.client.proxy.IAsyncObjectProxy;
import com.lxcrpc.test.client.HelloService;
import com.lxcrpc.test.client.Person;
import com.lxcrpc.test.client.PersonService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:client-spring.xml")
public class ServiceTest {
    @Autowired
    private RpcClient rpcClient;

    @Test
    public void helloTest1() {
        HelloService helloService = rpcClient.create(HelloService.class);
        String result = helloService.hello("world");
        Assert.assertEquals("Hello! World", result);

    }

    @Test
    public void helloTest2() {
        //调用哪一个类
        HelloService helloService = rpcClient.create(HelloService.class);
        Person person = new Person("Yong", "Huang");
        //指明调用类的哪个方法， 并指明参数
        String result = helloService.hello(person);
        Assert.assertEquals("Hello! Yong Huang", result);

    }
    @Test
    public void helloPersonTest() {
        PersonService personService = rpcClient.create(PersonService.class);
        int num = 5;
        List<Person> persons = personService.GetTestPerson("xiaoming", num);
        List<Person> expectedPersons = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            expectedPersons.add(new Person(Integer.toString(i), "xiaoming"));
        }
        assertThat(persons, equalTo(expectedPersons));

        for (int i = 0; i < persons.size(); ++i) {
            System.out.println(persons.get(i));
        }
    }


    @Test
    public void helloFutureTest1() throws ExecutionException, InterruptedException {
        IAsyncObjectProxy helloService = rpcClient.createAsync(HelloService.class);
        RPCFuture result = helloService.call("hello", "World");
        Assert.assertEquals("Hello! World", result.get());
    }
    @Test
    public void helloFutureTest2() throws ExecutionException, InterruptedException {
        IAsyncObjectProxy helloService = rpcClient.createAsync(HelloService.class);
        Person person = new Person("Yong", "Huang");
        RPCFuture result = helloService.call("hello", person);
        Assert.assertEquals("Hello! Yong Huang", result.get());
    }

}
