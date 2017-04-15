package com.maxplus1;

import com.maxplus1.demo.storm.AppMain;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
//@ImportResource("classpath:spring/beans.xml")
public class DemoApplication {

    public static void main(String[] args)
            throws InvalidTopologyException, AuthorizationException, AlreadyAliveException, InterruptedException {
        ConfigurableApplicationContext context = SpringApplication.run(DemoApplication.class, args);
        AppMain appMain = context.getBean(AppMain.class);
        appMain.Laugher();
        SpringApplication.exit(context);
    }


}
