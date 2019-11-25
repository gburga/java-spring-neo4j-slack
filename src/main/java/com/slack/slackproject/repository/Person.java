package com.slack.slackproject.repository;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class Person {
    @Id @GeneratedValue private Long id;

    private String name;
    private String slackId;
    private String username;

    private Person() {
        // Empty constructor required as of Neo4j API 2.0.5
    };

    public Person(String slackId, String username, String name) {
        this.name = name;
        this.username = username;
        this.slackId = slackId;
    }

    /**
     * Neo4j doesn't REALLY have bi-directional relationships. It just means when querying
     * to ignore the direction of the relationship.
     * https://dzone.com/articles/modelling-data-neo4j
     */
    @Relationship(type = "TEAMMATE", direction = Relationship.UNDIRECTED)
    public Set<Person> teammates;

    public void worksWith(Person person) {
        if (teammates == null) {
            teammates = new HashSet<>();
        }
        teammates.add(person);
    }

    @Relationship(type = "MENTIONS", direction = Relationship.UNDIRECTED)
    public Set<Person> mentionedPeople;

    public void mentions(Person person) {
        if (mentionedPeople == null) {
            mentionedPeople = new HashSet<>();
        }
        mentionedPeople.add(person);
    }

    public String toString() {

        return this.name + "'s teammates => "
            + Optional.ofNullable(this.teammates).orElse(
            Collections.emptySet()).stream()
            .map(Person::getName)
            .collect(Collectors.toList());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlackId() {
        return slackId;
    }

    public void setSlackId(String slackId) {
        this.slackId = slackId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
