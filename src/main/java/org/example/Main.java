package org.example;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


@Configuration
@ComponentScan(basePackages = "org.example")
//SpringBoot
public class Main {
    public static void main(String[] args) {

        new AnnotationConfigApplicationContext(Main.class);

    }
}

//entity review (ManyToMany -> ExpertService)
//phase-1