package com.slack.slackproject.service;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import com.hubspot.slack.client.models.users.SlackUser;
import com.slack.slackproject.repository.Person;

public class MigrationService {

    private SlackService slackService;

    public MigrationService() {
        this.slackService = new SlackService();
    }

    public List<Person> migrateUsers() throws ExecutionException, InterruptedException {
        List<SlackUser> slackUsers = slackService.findAllUsers().get();
        System.out.println("Number of people in IGZ for inserting to neo4j: " + slackUsers.size());

        return slackUsers.stream().map(slackUser ->
            convertToPerson(slackUser)).collect(Collectors.toList());
    }

    private Person convertToPerson(SlackUser slackUser) {
        System.out.println(slackUser);
        String slackId = slackUser.getId();
        String username = slackUser.getUsername().orElse(slackId);
        String realName = slackUser.getRealName().orElse(username);

        return new Person(slackId, username, realName);
    }

}
