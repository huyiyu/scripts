package org.example;

import org.example.dao.mapper.UserMapper;
import org.example.entity.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(Main.class, args);



        UserMapper bean = run.getBean(UserMapper.class);
        User user = new User();
        user.setId(1L);
        user.setAge(23);
        user.setName("huyiyu");
        bean.insertSelective(user);
    }
}