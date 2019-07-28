package ilia.nemankov.togrofbot.commands.impl;

import ilia.nemankov.togrofbot.commands.Command;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class Roll implements Command {

    private static final Logger logger = LoggerFactory.getLogger(Roll.class);

    @Override
    public String getName() {
        return this.getClass().getSimpleName().toLowerCase();
    }

    @Override
    public String[] getDescriptions() {
        return new String[] { "roll - Generate a random number from 0 to 100" };
    }

    @Override
    public void execute(GuildMessageReceivedEvent event) {
        logger.debug("Started execution of {} command", this.getClass().getSimpleName());
        logger.debug("Received message: {}", event.getMessage().getContentRaw());

        Random random = new Random(System.currentTimeMillis());
        String response = event.getAuthor().getAsMention() + ", you rolled " + random.nextInt(101);
        logger.debug("Generated response for command {}: \"{}\"", this.getClass().getSimpleName(), response);
        event.getChannel().sendMessage(response).queue();

        logger.debug("Finished execution of {} command", this.getClass().getSimpleName());
    }

}
