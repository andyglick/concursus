package com.opencredo.concourse.spring.demo;

import com.opencredo.concourse.domain.events.cataloguing.AggregateCatalogue;
import com.opencredo.concourse.spring.cassandra.CassandraBeans;
import com.opencredo.concourse.spring.cassandra.CassandraCatalogueBeans;
import com.opencredo.concourse.spring.commands.CommandSystemBeans;
import com.opencredo.concourse.spring.events.EventSystemBeans;
import com.opencredo.concourse.spring.events.filtering.AggregateCatalogueUpdatingFilter;
import com.opencredo.concourse.spring.events.filtering.Filter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@ComponentScan(basePackageClasses = {
        EventSystemBeans.class,
        CommandSystemBeans.class,
        CassandraBeans.class,
        CassandraCatalogueBeans.class,
        Application.class })
@EnableAutoConfiguration
@EnableWebMvc
@Configuration
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Filter(Integer.MIN_VALUE)
    @Bean
    public AggregateCatalogueUpdatingFilter aggregateCatalogueUpdatingFilter(AggregateCatalogue aggregateCatalogue) {
        return new AggregateCatalogueUpdatingFilter(aggregateCatalogue);
    }
}