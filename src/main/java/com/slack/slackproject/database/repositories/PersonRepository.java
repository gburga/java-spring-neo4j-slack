package com.slack.slackproject.database.repositories;

import org.springframework.data.repository.CrudRepository;

import com.slack.slackproject.database.domain.Person;

public interface PersonRepository extends CrudRepository<Person, Long> {

    Person findByName(String name);

    //@Depth(value = 0) // Note: for performance or
    // or use simple projection @See https://docs.spring.io/spring-data/neo4j/docs/5.2.2.RELEASE/reference/html/#reference_programming-projections
    Person findBySlackId(String slackId);
}
