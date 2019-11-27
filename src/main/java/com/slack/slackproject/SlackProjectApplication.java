package com.slack.slackproject;

import com.slack.slackproject.migration.services.MigrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

@SpringBootApplication
@EnableNeo4jRepositories("com.slack.slackproject.database.repositories")
public class SlackProjectApplication {
    private final static Logger log = LoggerFactory.getLogger(SlackProjectApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SlackProjectApplication.class, args);
    }

    @Bean
    CommandLineRunner demo(MigrationService ms) {
        return args -> {
            ms.run();
        };
    }
}
