package com.slack.slackproject;

import java.util.Arrays;
import java.util.List;

import com.hubspot.slack.client.models.users.SlackUser;
import com.slack.slackproject.repository.Person;
import com.slack.slackproject.repository.PersonRepository;
import com.slack.slackproject.service.MigrationService;
import com.slack.slackproject.service.SlackService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

@SpringBootApplication
@EnableNeo4jRepositories
public class SlackProjectApplication {
    private final static Logger log = LoggerFactory.getLogger(SlackProjectApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SlackProjectApplication.class, args);
    }

    @Bean
    CommandLineRunner demo(PersonRepository personRepository) {
        return args -> {

            personRepository.deleteAll();

            Person greg = new Person("Greg");
            Person roy = new Person("Roy");
            Person craig = new Person("Craig");

            List<Person> team = Arrays.asList(greg, roy, craig);

            log.info("Before linking up with Neo4j...");

            team.stream().forEach(person -> log.info("\t" + person.toString()));

            personRepository.save(greg);
            personRepository.save(roy);
            personRepository.save(craig);

            greg = personRepository.findByName(greg.getName());
            greg.worksWith(roy);
            greg.worksWith(craig);
            personRepository.save(greg);

            roy = personRepository.findByName(roy.getName());
            roy.worksWith(craig);
            // We already know that roy works with greg
            personRepository.save(roy);

            // We already know craig works with roy and greg

            log.info("Lookup each person by name...");
            team.stream().forEach(person -> log.info(
                "\t" + personRepository.findByName(person.getName()).toString()));

            MigrationService ms = new MigrationService();
            List<Person>  people = ms.migrateUsers();
            System.out.println("Number of people in IGZ" + people.size());
            personRepository.saveAll(people);

            //SlackService slackService = new SlackService();
            //slackService.messagesByChannelId("C030E758M");
            //slackService.findUserById("U7CJ9AWJW");
            //slackService.findUsersByChannelId("C030E758M");
        };
    }
}
