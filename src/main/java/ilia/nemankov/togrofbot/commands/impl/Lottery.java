package ilia.nemankov.togrofbot.commands.impl;

import ilia.nemankov.togrofbot.commands.AbstractCommand;
import ilia.nemankov.togrofbot.commands.CommandItem;
import ilia.nemankov.togrofbot.commands.parsing.CommandVariantDescription;
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

    private static final String[] variants = new String[] {"lottery", "lot"};

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
        public CommandVariantDescription getDescription() {
            ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());
            CommandVariantDescription description = new CommandVariantDescription(
                    resources.getString("description.command.lottery.all_guild.args"),
                    resources.getString("description.command.lottery.all_guild.desc")
            );
            return description;
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
        public CommandVariantDescription getDescription() {
            ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());
            CommandVariantDescription description = new CommandVariantDescription(
                    resources.getString("description.command.lottery.mentioned.args"),
                    resources.getString("description.command.lottery.mentioned.desc")
            );
            return description;
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
