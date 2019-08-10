package ilia.nemankov.togrofbot.commands.impl;

import ilia.nemankov.togrofbot.commands.Command;
import ilia.nemankov.togrofbot.settings.SettingsProvider;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

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
    public void execute(GuildMessageReceivedEvent event) {
        logger.debug("Started execution of {} command", this.getClass().getSimpleName());
        logger.debug("Received message: {}", event.getMessage().getContentRaw());

        ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());

        List<Member> members = event.getMessage().getMentionedMembers();
        Random random = new Random(System.currentTimeMillis());
        String response;
        if (members.size() > 0) {
            response = MessageFormat.format(
                    resources.getString("message.command.lottery.winner"),
                    members.get(random.nextInt(members.size())).getAsMention()
            );
        } else {
            members = event.getGuild().getMembers();
            response = MessageFormat.format(
                    resources.getString("message.command.lottery.winner"),
                    members.get(random.nextInt(members.size())).getAsMention()
            );
        }
        logger.debug("Generated response for command {}: \"{}\"", this.getClass().getSimpleName(), response);
        event.getChannel().sendMessage(response).queue();

        logger.debug("Finished execution of {} command", this.getClass().getSimpleName());
    }

}
