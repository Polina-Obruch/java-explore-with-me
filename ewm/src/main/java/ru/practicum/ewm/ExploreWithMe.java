package ru.practicum.ewm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"ru.practicum"})
public class ExploreWithMe {
    public static void main(String[] args) {
        SpringApplication.run(ExploreWithMe.class);
    }
}
