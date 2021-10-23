package com.example.boot_demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.Inet4Address;
import java.net.UnknownHostException;

@SpringBootApplication
@RestController
public class BootDemoApplication {

    public static void main(String[] args) throws UnknownHostException {

        SpringApplication.run(BootDemoApplication.class, args);
    }


    @RequestMapping("/")
    public String home() throws UnknownHostException {
        return "Hello Docker World, host name is "+Inet4Address.getLocalHost().getHostName();
    }
}
