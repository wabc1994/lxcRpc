package com.lxcrpc.test.server;

import com.coderlau.server.RpcService;
import com.lxcrpc.test.client.Person;
import com.lxcrpc.test.client.PersonService;

import java.util.ArrayList;
import java.util.List;

@RpcService(PersonService.class)
public class PersonServiceImpl implements PersonService {
    @Override
    public List<Person> GetTestPerson(String name, int num) {
        List<Person> persons = new ArrayList<>(num);
        for (int i = 0; i < num; ++i) {
            persons.add(new Person(Integer.toString(i), name));
        }
        return persons;
    }
}
