package com.slack.slackproject.repository;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

import lombok.Getter;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
@Getter
@EnableNeo4jRepositories
public class Person {
    @Id @GeneratedValue private Long id;

    private String name;
    private String slackId;
    private String username;

    /**
     * Neo4j doesn't REALLY have bi-directional relationships. It just means when querying
     * to ignore the direction of the relationship.
     * https://dzone.com/articles/modelling-data-neo4j
     */
    @Relationship(type = "ANSWERS")
    public Set<Person> answeredPeople;

    @Relationship(type = "MENTIONS")
    public Set<Mention> mentions;

    @Relationship(type = "BELONGS_TO", direction = Relationship.UNDIRECTED)
    public Set<Channel> channels;

    private Person() {
        // Empty constructor required as of Neo4j API 2.0.5
    };

    public Person(String slackId, String username, String name) {
        this.name = name;
        this.username = username;
        this.slackId = slackId;
    }

    public void answers(Person person) {
        if (answeredPeople == null) {
            answeredPeople = new HashSet<>();
        }
        answeredPeople.add(person);
    }

    public void mentions(Person mentionedUser) {
        Mention mention = new Mention(this, mentionedUser);

        if (mentions == null) {
            mentions = new HashSet<>();
        }

         mentions.add(mention);
    }

    public void belongTo(Channel channel) {
        if (channels == null) {
            channels = new HashSet<>();
        }
        channels.add(channel);
    }

    public String toString() {
        return this.name + "'s teammates => "
            + Optional.ofNullable(this.answeredPeople).orElse(
            Collections.emptySet()).stream()
            .map(Person::getName)
            .collect(Collectors.toList());
    }
}
