package com.slack.slackproject.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.hubspot.algebra.Result;
import com.hubspot.slack.client.SlackClient;
import com.hubspot.slack.client.methods.params.channels.ChannelsHistoryParams;
import com.hubspot.slack.client.methods.params.channels.ChannelsListParams;
import com.hubspot.slack.client.methods.params.users.UsersInfoParams;
import com.hubspot.slack.client.models.SlackChannel;
import com.hubspot.slack.client.models.response.SlackError;
import com.slack.slackproject.client.BasicRuntimeConfig;

public class SlackService {
    private SlackClient client;

    public SlackService() {
        this.client = BasicRuntimeConfig.getClient();
    }

    public void channels() {
        ChannelsListParams channelParams = ChannelsListParams.builder().build();

        client.listChannels(channelParams).forEach(p -> p.whenCompleteAsync((channels, slackError) -> {
            channels.unwrapOrElseThrow().forEach(System.out::println);
        }));
    }

    public void messagesByChannelId(String channelId) {
        ChannelsHistoryParams conversationParams = ChannelsHistoryParams
            .builder()
            .setChannelId(channelId)
            .build();

        client.channelHistory(conversationParams).forEach(p -> p.whenCompleteAsync((users, slackError) -> {
            users.unwrapOrElseThrow().forEach(System.out::println);
        }));;
    }

    public void findUserById(String userId) {
        UsersInfoParams usersInfoParams = UsersInfoParams.builder().setUserId(userId).build();

        client.findUser(usersInfoParams).whenCompleteAsync((userInfo, slackError) -> {
            System.out.println(userInfo.unwrapOrElseThrow().toString());
        });
    }
}
