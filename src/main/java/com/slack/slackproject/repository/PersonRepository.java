package com.slack.slackproject.repository;

import org.springframework.data.neo4j.annotation.Depth;
import org.springframework.data.repository.CrudRepository;

public interface PersonRepository extends CrudRepository<Person, Long> {

    Person findByName(String name);

    //@Depth(value = 0) // Note: for performance or
    // or use simple projection @See https://docs.spring.io/spring-data/neo4j/docs/5.2.2.RELEASE/reference/html/#reference_programming-projections
    Person findBySlackId(String slackId);
}
