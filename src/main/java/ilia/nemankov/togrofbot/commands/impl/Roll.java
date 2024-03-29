package ilia.nemankov.togrofbot.commands.impl;

import ilia.nemankov.togrofbot.commands.AbstractCommand;
import ilia.nemankov.togrofbot.commands.CommandItem;
import ilia.nemankov.togrofbot.commands.parsing.CommandVariantDescription;
import ilia.nemankov.togrofbot.commands.parsing.argument.Argument;
import ilia.nemankov.togrofbot.commands.parsing.argument.NumberArgument;
import ilia.nemankov.togrofbot.commands.parsing.matching.ArgumentsTemplate;
import ilia.nemankov.togrofbot.commands.parsing.matching.NumberArgumentMatcher;
import ilia.nemankov.togrofbot.settings.SettingsProvider;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

@Slf4j
public class Roll extends AbstractCommand {

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
            return generateResult(1, 100, event.getAuthor());
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
            int topBorder = ((NumberArgument)arguments.get(1)).getNumberArgument().intValue();

            if (bottomBorder < 0) {
                return resources.getString("message.command.roll.negative_bottom_border");
            }
            if (topBorder < 0) {
                return resources.getString("message.command.roll.negative_top_border");
            }
            if (topBorder < bottomBorder) {
                return resources.getString("message.command.roll.incorrect_borders");
            }

            return generateResult(bottomBorder, topBorder, event.getAuthor());
        }
    }

    private String generateResult(int bottomBorder, int topBorder, User author) {
        if (bottomBorder < 0 || topBorder < 0) {
            log.error("Received invalid args: bottomBorder={}, topBorder={}", bottomBorder, topBorder);
            throw new IllegalArgumentException();
        }
        ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());

        Random random = new Random(System.currentTimeMillis());
        return MessageFormat.format(
                resources.getString("message.command.roll.result"),
                author.getAsMention(),
                (random.nextInt(topBorder - bottomBorder + 1) + bottomBorder)
        );
    }

}
