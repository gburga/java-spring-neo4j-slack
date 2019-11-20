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
        List<String> userIds = slackService.findUsersByChannelId("C030E758M").get();

        return userIds.stream().map(userId ->
        {
            SlackUser userInfo = SlackUser.builder().setRealName("no user information")
                .setId("pru")
                .setRealName("user not found").build();
            try {
                userInfo = slackService.findUserById(userId).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            System.out.println(userInfo);
            Person p = new Person(userInfo.getRealName().get());
            return p;
        }).collect(Collectors.toList());
    }

}
