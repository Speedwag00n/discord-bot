package ilia.nemankov.heyheybot.commands.impl;

import ilia.nemankov.heyheybot.commands.Command;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;

public class Lottery implements Command {

    private static final Logger logger = LoggerFactory.getLogger(Lottery.class);

    @Override
    public String getName() {
        return this.getClass().getSimpleName().toLowerCase();
    }

    @Override
    public String[] getDescriptions() {
        return new String[] {"lottery - Choose a random member of current guild",
            "lottery <@mentions> - Choose a random member mentioned in arguments of this command (few mentions of the same user don't affect on results)"};
    }

    @Override
    public void execute(MessageReceivedEvent event) {
        logger.debug("Started execution of {} command", this.getClass().getSimpleName());
        logger.debug("Received message: {}", event.getMessage().getContentRaw());

        List<Member> members = event.getMessage().getMentionedMembers();
        Random random = new Random(System.currentTimeMillis());
        String response;
        if (members.size() > 0) {
            response = "Winner is " + members.get(random.nextInt(members.size())).getAsMention();
        } else {
            members = event.getGuild().getMembers();
            response = "Winner is " + members.get(random.nextInt(members.size())).getAsMention();
        }
        logger.debug("Generated response for command {}: \"{}\"", this.getClass().getSimpleName(), response);
        event.getChannel().sendMessage(response).queue();

        logger.debug("Finished execution of {} command", this.getClass().getSimpleName());
    }

}
