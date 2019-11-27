package com.slack.slackproject.migration.services;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hubspot.slack.client.models.LiteMessage;
import com.hubspot.slack.client.models.SlackChannel;
import com.hubspot.slack.client.models.users.SlackUser;
import com.slack.slackproject.database.domain.Person;
import com.slack.slackproject.database.repositories.PersonRepository;
import com.slack.slackproject.slack.services.SlackService;

@Service
public class MigrationService {

    @Autowired
    private SlackService slackService;

    @Inject
    private PersonRepository personRepository;

    private Person convertToPerson(SlackUser slackUser) {
        String slackId = slackUser.getId();
        String username = slackUser.getUsername().orElse(slackId);
        String realName = slackUser.getRealName().orElse(username);

        return new Person(slackId, username, realName);
    }

    private void migrateUsers() throws ExecutionException, InterruptedException {
        List<SlackUser> slackUsers = slackService.findAllUsers().get();
        System.out.println("Number of people in IGZ for inserting to neo4j: " + slackUsers.size());

        List<Person>  people = slackUsers.stream().map(slackUser ->
            convertToPerson(slackUser)).collect(Collectors.toList());

        personRepository.saveAll(people);
    }

    private void processMessagesByChannel(/*SlackChannel slackChannel*/) throws ExecutionException, InterruptedException {
//        System.out.println("Reading messages from [channelId=" + slackChannel.getId() + ", name=" + slackChannel.getName() + "]");
        String slackChannelId = "CCWQ5ABE1";
        List<LiteMessage> messages = slackService.conversations(slackChannelId).get();
        System.out.println("Number of messages found: " + messages.size());

        messages.forEach(message -> {
            System.out.println(message);

            String slackUserId = message.getUser().orElse(null);
            if (slackUserId != null) {
                Person owner = personRepository.findBySlackId(slackUserId);

                if (owner != null) {
                    // REPLIES
                    message.getReplyUserIds().orElse(new ArrayList<String>())
                        .forEach(replyUserId -> {
                            Person replyUser = personRepository.findBySlackId(replyUserId);

                            if (replyUser != null) {
                                replyUser.answers(owner);
                                personRepository.save(replyUser);
                                System.out.println(replyUser.getName() + " - answers -> " + owner.getName());
                            }
                        });

                    // MENTIONS
                    // <!channel> <!here> <@UC18J4V2A>
                    // ignore channel_purpose channel_join
                    String textMessage = message.getText();
                    if (textMessage != ""
                        && !message.getSubtype().orElse("").equalsIgnoreCase("channel_join")
                        && !message.getSubtype().orElse("").equalsIgnoreCase("channel_purpose")
                    ) {
                        Pattern p = Pattern.compile("\\<@(.*?)\\>");
                        Matcher matcher = p.matcher(textMessage);

                        //System.out.println("In the message: " + textMessage);
                        while(matcher.find()) {
                            String mentionedUserId = matcher.group(1);

                            if (mentionedUserId != null
                                && !mentionedUserId.equalsIgnoreCase(slackUserId)) { // To avoid self relation
                                Person mentionedUser = personRepository.findBySlackId(mentionedUserId);

                                if (mentionedUser != null) {
                                    System.out.println(owner.getName() + " - mentions -> " + mentionedUser.getName());
                                    owner.mentions(mentionedUser);
                                } else {
                                    System.out.println("Mentioned user not found [id=" +  mentionedUserId + "]");
                                }
                            }
                        }

                        personRepository.save(owner);
                    }
                } else {
                    System.out.println("owner is null");
                }
            } else {
                System.out.println("slackUserId is null");
            }
        });
    }

    private void processMessages() throws ExecutionException, InterruptedException {
        List<SlackChannel> slackChannels = slackService.channels().get();
        System.out.println("Channels found: " + slackChannels.size());
//        slackChannels.forEach(slackChannel -> {
//            try {
//                processMessagesByChannel(slackChannel);
//            } catch (ExecutionException e) {
//                e.printStackTrace();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        });

        processMessagesByChannel();
    }

    public void run () {
        personRepository.deleteAll();
        try {
            // User Migrations
            migrateUsers();

            // Read all messages
            processMessages();

            System.out.println("all channels have been read");
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
