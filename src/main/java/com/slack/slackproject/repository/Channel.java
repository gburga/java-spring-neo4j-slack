package com.slack.slackproject.repository;

import lombok.Getter;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.Index;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
@Getter
public class Channel {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

//    @Index(unique = true) // Set in  application.properties: spring.data.neo4j.auto-index=validate
    private String slackChannelId;

    public Channel(String slackChannelId, String name) {
        this.slackChannelId = slackChannelId;
        this.name = name;
    }
}
