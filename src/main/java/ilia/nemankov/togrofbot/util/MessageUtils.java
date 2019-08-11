package ilia.nemankov.togrofbot.util;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageUtils {

    private static final Logger logger = LoggerFactory.getLogger(MessageUtils.class);

    public static String capitalizeFirstLetter(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    public static void sendTextResponse(GuildMessageReceivedEvent event, String response, boolean toDefaultChannel) {
        if (toDefaultChannel) {
            sendTextResponse(event.getGuild().getDefaultChannel(), response);
        } else {
            sendTextResponse(event.getMessage().getTextChannel(), response);
        }
    }

    public static void sendTextResponse(TextChannel channel, String response) {
        channel
                .sendMessage(response)
                .queue(sentMessage -> logger.debug("Sent message \"{}\" to \"{}\" channel in \"{}\" guild", sentMessage.getContentRaw(), channel.getName(), channel.getGuild()));
    }

    public static void sendPrivateMessage(String message, User user) {
        MessageBuilder builder = new MessageBuilder();
        builder
                .append(message)
                .sendTo(
                        user.openPrivateChannel().complete()
                )
                .queue(sentMessage -> logger.debug("Sent private message \"{}\" to \"{}\" user", sentMessage.getContentRaw(), user.getName()));
    }

}
