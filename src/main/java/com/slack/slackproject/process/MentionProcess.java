package com.slack.slackproject.process;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import com.hubspot.slack.client.models.LiteMessage;
import com.slack.slackproject.service.SlackService;

public class MentionProcess {

    private SlackService slackService;

    public MentionProcess() {
        this.slackService = new SlackService();
    }

    public void readMessagesByChannel (String channelId) throws ExecutionException, InterruptedException {
        List<LiteMessage> messages = slackService.conversations(channelId).get();

        messages.forEach(message -> {
            message.getReplyUserIds();
        });
    }

    /*public findMention() {

    }*/
}
