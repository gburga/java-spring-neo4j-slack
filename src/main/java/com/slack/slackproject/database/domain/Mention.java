package com.slack.slackproject.database.domain;

import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

@RelationshipEntity(type = "MENTIONS")
//@NoArgsConstructor(force = true, access = AccessLevel.PRIVATE)
//@CompositeIndex(value = {"sourceUser", "targetUser"}) // 	Note: his feature is only supported by Neo4j Enterprise 3.2 and higher.
@Getter
@Setter
public class Mention {

    @Id
    @GeneratedValue
    private Long id;

    @StartNode
    private Person sourceUser;

    @EndNode
    private Person targetUser;

    public Mention(Person sourceUser, Person targetUser) {
        this.sourceUser = sourceUser;
        this.targetUser = targetUser;
    }

    /*@PostLoad
    public void counter () {
        System.out.println("adding new mention: " + sourceUser.getName() + " -> " + targetUser.getName());
    }*/

}
