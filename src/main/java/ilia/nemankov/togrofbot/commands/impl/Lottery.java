package ilia.nemankov.togrofbot.commands.impl;

import ilia.nemankov.togrofbot.commands.AbstractCommand;
import ilia.nemankov.togrofbot.commands.CommandItem;
import ilia.nemankov.togrofbot.commands.parsing.argument.Argument;
import ilia.nemankov.togrofbot.commands.parsing.matching.ArgumentsTemplate;
import ilia.nemankov.togrofbot.settings.SettingsProvider;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

public class Lottery extends AbstractCommand {

    private static final Logger logger = LoggerFactory.getLogger(Lottery.class);

    private static final String[] variants = new String[] {"lottety", "lot"};

    @Override
    public String[] getDescriptions() {
        return new String[] {"lottery - Choose a random member of current guild",
            "lottery <@mentions> - Choose a random member mentioned in arguments of this command (few mentions of the same user don't affect on results)"};
    }

    @Override
    public String[] getVariants() {
        return variants;
    }

    public Lottery() {
        List<CommandItem> commandItems = new ArrayList<>();
        commandItems.add(new LotteryAllGuild());
        commandItems.add(new LotteryMentioned());
        setCommandItems(commandItems);
    }

    private class LotteryAllGuild extends CommandItem {

        public LotteryAllGuild() {
            super(new ArgumentsTemplate(null));
        }

        @Override
        public String execute(GuildMessageReceivedEvent event, List<Argument> arguments) {
            ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());

            List<Member> members = event.getGuild().getMembers();
            Random random = new Random(System.currentTimeMillis());
            return MessageFormat.format(
                    resources.getString("message.command.lottery.winner"),
                    members.get(random.nextInt(members.size())).getAsMention()
            );
        }
    }

    private class LotteryMentioned extends CommandItem {

        public LotteryMentioned() {
            super();
        }

        @Override
        public String execute(GuildMessageReceivedEvent event, List<Argument> arguments) {
            ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());

            List<Member> members = event.getMessage().getMentionedMembers();
            Random random = new Random(System.currentTimeMillis());
            return MessageFormat.format(
                    resources.getString("message.command.lottery.winner"),
                    members.get(random.nextInt(members.size())).getAsMention()
            );
        }
    }

}
