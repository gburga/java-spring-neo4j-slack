package com.slack.slackproject.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.hubspot.algebra.Result;
import com.hubspot.slack.client.SlackClient;
import com.hubspot.slack.client.methods.params.channels.ChannelsHistoryParams;
import com.hubspot.slack.client.methods.params.channels.ChannelsListParams;
import com.hubspot.slack.client.methods.params.conversations.ConversationMemberParams;
import com.hubspot.slack.client.methods.params.conversations.ConversationsHistoryParams;
import com.hubspot.slack.client.methods.params.users.UsersInfoParams;
import com.hubspot.slack.client.models.LiteMessage;
import com.hubspot.slack.client.models.SlackChannel;
import com.hubspot.slack.client.models.users.SlackUser;
import com.slack.slackproject.client.BasicRuntimeConfig;

public class SlackService {
    private SlackClient client;

    public SlackService() {
        this.client = BasicRuntimeConfig.getClient();
    }

    public CompletableFuture<List<SlackChannel>> channels() {
        ChannelsListParams channelParams = ChannelsListParams.builder().build();

        return client.listChannels(channelParams)
            .iterator().next().thenApply(Result::unwrapOrElseThrow);
    }

    public CompletableFuture<List<LiteMessage>> messagesByChannelId(String channelId) {
        ChannelsHistoryParams conversationParams = ChannelsHistoryParams
            .builder()
            .setChannelId(channelId)
            .build();

        return client.channelHistory(conversationParams)
            .iterator().next().thenApply(Result::unwrapOrElseThrow);
    }

    public CompletableFuture<List<LiteMessage>> conversations(String channelId) {
        ConversationsHistoryParams conversationParams = ConversationsHistoryParams
            .builder()
            .setChannelId(channelId)
            .build();

        return client.getConversationHistory(conversationParams)
            .iterator().next().thenApply(Result::unwrapOrElseThrow);
    }

    public CompletableFuture<SlackUser> findUserById(String userId) {
        UsersInfoParams usersInfoParams = UsersInfoParams.builder().setUserId(userId).build();

        return client.findUser(usersInfoParams)
            .thenApplyAsync(Result::unwrapOrElseThrow)
            .thenApplyAsync(result -> result.getUser());
    }

    public CompletableFuture<List<String>> findUsersByChannelId(String channelId) {
        ConversationMemberParams conversationMemberParams = ConversationMemberParams
            .builder()
            .setChannelId(channelId)
            .build();

        return client.getConversationMembers(conversationMemberParams)
           .iterator().next().thenApply(Result::unwrapOrElseThrow);
    }

    public CompletableFuture<List<SlackUser>> findAllUsers() {
        return client.listUsers()
            .iterator().next().thenApply(Result::unwrapOrElseThrow);

    }

}
