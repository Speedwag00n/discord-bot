package ilia.nemankov.togrofbot.commands.impl;

import ilia.nemankov.togrofbot.commands.AbstractCommand;
import ilia.nemankov.togrofbot.commands.CommandItem;
import ilia.nemankov.togrofbot.commands.parsing.argument.Argument;
import ilia.nemankov.togrofbot.settings.SettingsProvider;
import ilia.nemankov.togrofbot.util.MessageUtils;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class Summon extends AbstractCommand {

    private static final Logger logger = LoggerFactory.getLogger(Summon.class);

    @Override
    public String[] getDescriptions() {
        return new String[] {"summon <@mentions> - The bot sends message to mentioned users with request to join guild where this command was used"};
    }

    public Summon() {
        List<CommandItem> commandItems = new ArrayList<>();
        commandItems.add(new SummonMentioned());
        setCommandItems(commandItems);
    }

    private class SummonMentioned extends CommandItem {

        public SummonMentioned() {
            super();
        }

        @Override
        public String execute(GuildMessageReceivedEvent event, List<Argument> arguments) {
            ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());

            List<Member> members = event.getMessage().getMentionedMembers();
            for (Member member : members) {
                MessageUtils.sendPrivateMessage(
                        MessageFormat.format(
                                resources.getString("message.command.call.message_body"),
                                event.getMessage().getAuthor().getName(),
                                event.getGuild().getName()),
                        member.getUser()
                );
            }
            return MessageFormat.format(
                    resources.getString("message.command.call.successful"),
                    members.size()
            );
        }
    }

}