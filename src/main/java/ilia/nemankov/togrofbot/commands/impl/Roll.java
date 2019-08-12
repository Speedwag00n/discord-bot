package ilia.nemankov.togrofbot.commands.impl;

import ilia.nemankov.togrofbot.commands.AbstractCommand;
import ilia.nemankov.togrofbot.commands.CommandItem;
import ilia.nemankov.togrofbot.commands.parsing.CommandVariantDescription;
import ilia.nemankov.togrofbot.commands.parsing.argument.Argument;
import ilia.nemankov.togrofbot.commands.parsing.argument.NumberArgument;
import ilia.nemankov.togrofbot.commands.parsing.matching.ArgumentsTemplate;
import ilia.nemankov.togrofbot.commands.parsing.matching.NumberArgumentMatcher;
import ilia.nemankov.togrofbot.settings.SettingsProvider;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

public class Roll extends AbstractCommand {

    private static final Logger logger = LoggerFactory.getLogger(Roll.class);

    private static final String[] variants = new String[] {"roll"};

    @Override
    public String[] getVariants() {
        return variants;
    }

    public Roll() {
        List<CommandItem> commandItems = new ArrayList<>();
        commandItems.add(new DefaultRoll());
        commandItems.add(new BorderedRoll());
        setCommandItems(commandItems);
    }

    private class DefaultRoll extends CommandItem {

        public DefaultRoll() {
            super(new ArgumentsTemplate(null));
        }

        @Override
        public CommandVariantDescription getDescription() {
            ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());
            CommandVariantDescription description = new CommandVariantDescription(
                    resources.getString("description.command.roll.default.args"),
                    resources.getString("description.command.roll.default.desc")
            );
            return description;
        }

        @Override
        public String execute(GuildMessageReceivedEvent event, List<Argument> arguments) {
            ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());

            Random random = new Random(System.currentTimeMillis());
            return MessageFormat.format(
                    resources.getString("message.command.roll.result"),
                    event.getAuthor().getAsMention(),
                    random.nextInt(101)
            );
        }
    }

    private class BorderedRoll extends CommandItem {

        public BorderedRoll() {
            super(new ArgumentsTemplate(null, new NumberArgumentMatcher(), new NumberArgumentMatcher()));
        }

        @Override
        public CommandVariantDescription getDescription() {
            ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());
            CommandVariantDescription description = new CommandVariantDescription(
                    resources.getString("description.command.roll.bordered.args"),
                    resources.getString("description.command.roll.bordered.desc")
            );
            return description;
        }

        @Override
        public String execute(GuildMessageReceivedEvent event, List<Argument> arguments) {
            ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());

            int bottomBorder = ((NumberArgument)arguments.get(0)).getNumberArgument().intValue();
            if (bottomBorder < 0) {
                return resources.getString("message.command.roll.negative_bottom_border");
            }
            int topBorder = ((NumberArgument)arguments.get(1)).getNumberArgument().intValue();
            if (topBorder < 0) {
                return resources.getString("message.command.roll.negative_top_border");
            }
            if (topBorder < bottomBorder) {
                return resources.getString("message.command.roll.incorrect_borders");
            }

            Random random = new Random(System.currentTimeMillis());
            return MessageFormat.format(
                    resources.getString("message.command.roll.result"),
                    event.getAuthor().getAsMention(),
                    (random.nextInt(topBorder - bottomBorder + 1) + bottomBorder)
            );
        }
    }

}
