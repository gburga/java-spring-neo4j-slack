package com.slack.slackproject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hubspot.slack.client.models.LiteMessage;
import com.hubspot.slack.client.models.SlackChannel;
import com.hubspot.slack.client.models.users.SlackUser;
import com.slack.slackproject.repository.Person;
import com.slack.slackproject.repository.PersonRepository;
import com.slack.slackproject.service.MigrationService;
import com.slack.slackproject.service.SlackService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;

@SpringBootApplication
@EnableNeo4jRepositories
public class SlackProjectApplication {
    private final static Logger log = LoggerFactory.getLogger(SlackProjectApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(SlackProjectApplication.class, args);
    }

    @Bean
    CommandLineRunner demo(PersonRepository personRepository) {
        return args -> {
            // USER MIGRATION
            /*personRepository.deleteAll();

            MigrationService ms = new MigrationService();
            List<Person>  people = ms.migrateUsers();
            personRepository.saveAll(people);*/

            SlackService slackService = new SlackService();
            List<SlackChannel> slackChannels = slackService.channels().get();
            System.out.println("Channels found: " + slackChannels.size());

            slackChannels.forEach(slackChannel -> {
                System.out.println("Reading messages from [channelId=" + slackChannel.getId() + ", name=" + slackChannel.getName() + "]");

                try {
                    List<LiteMessage> messages = slackService.conversations(slackChannel.getId()).get();
                    System.out.println("Number of messages found: " + messages.size());

                    messages.forEach(message -> {
                        //System.out.println(message);

                        String slackUserId = message.getUser().orElse(null);
                        if (slackUserId != null) {
                            Person owner = personRepository.findBySlackId(slackUserId);

                            if (owner != null) {
                                // REPLIES
                                message.getReplyUserIds().orElse(new ArrayList<String>())
                                .forEach(replyUserId -> {
                                    Person replyUser = personRepository.findBySlackId(replyUserId);

                                    if (replyUser != null) {
                                        owner.worksWith(replyUser);
                                        personRepository.save(owner);
                                        //System.out.println(owner.getName() + " works with: " + replyUser.getName());
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
                                        Person mentionedUser = personRepository.findBySlackId(mentionedUserId);

                                        if (mentionedUser != null) {
                                            //System.out.println(owner.getName() + " mentions " + mentionedUser.getName());
                                            owner.mentions(mentionedUser);
                                        } else {
                                            System.out.println("Mentioned user not found [id=" +  mentionedUserId + "]");
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
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            });

            System.out.println("all channels have been read");
        };
    }
}
