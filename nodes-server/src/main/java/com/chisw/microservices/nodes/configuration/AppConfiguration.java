package com.chisw.microservices.nodes.configuration;


import com.chisw.microservices.nodes.persistence.jpa.entity.Node;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@Configuration
@ComponentScan("com.chisw.microservices.nodes")
@EnableJpaRepositories("com.chisw.microservices.nodes.persistence.jpa.repository")
@EntityScan(basePackageClasses = Node.class)
public class AppConfiguration {
}
