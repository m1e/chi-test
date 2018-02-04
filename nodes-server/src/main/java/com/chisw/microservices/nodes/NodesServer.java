package com.chisw.microservices.nodes;

import com.chisw.microservices.nodes.configuration.AppConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;


/**
 * Run as a micro-service, registering with the Discovery Server (Eureka)
 * if needed
 */
@EnableAutoConfiguration
@EnableDiscoveryClient
@Import(AppConfiguration.class)
public class NodesServer {


    /**
     * Run the application using Spring Boot and an embedded servlet engine.
     *
     * @param args Program arguments - ignored.
     */
    public static void main(String[] args) {
        // Tell server to look for nodes-server.properties or
        // nodes-server.yml
        System.setProperty("spring.config.name", "nodes-server");

        SpringApplication.run(NodesServer.class, args);
    }
}
