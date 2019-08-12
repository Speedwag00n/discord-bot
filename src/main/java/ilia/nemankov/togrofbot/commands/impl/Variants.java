package ilia.nemankov.togrofbot.commands.impl;

import ilia.nemankov.togrofbot.commands.AbstractCommand;
import ilia.nemankov.togrofbot.commands.Command;
import ilia.nemankov.togrofbot.commands.CommandHandlerImpl;
import ilia.nemankov.togrofbot.commands.CommandItem;
import ilia.nemankov.togrofbot.commands.parsing.CommandVariantDescription;
import ilia.nemankov.togrofbot.commands.parsing.argument.Argument;
import ilia.nemankov.togrofbot.commands.parsing.matching.ArgumentsTemplate;
import ilia.nemankov.togrofbot.commands.parsing.matching.StringArgumentMatcher;
import ilia.nemankov.togrofbot.settings.SettingsProvider;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.*;

public class Variants extends AbstractCommand {

    private static final Logger logger = LoggerFactory.getLogger(Variants.class);

    private static final String[] variants = new String[] {"variants", "vars", "var"};

    @Override
    public String[] getVariants() {
        return variants;
    }

    public Variants() {
        List<CommandItem> commandItems = new ArrayList<>();
        commandItems.add(new DefaultVariants());
        setCommandItems(commandItems);
    }

    private class DefaultVariants extends CommandItem {

        public DefaultVariants() {
            super(new ArgumentsTemplate(null, new StringArgumentMatcher()));
        }

        @Override
        public CommandVariantDescription getDescription() {
            ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());
            CommandVariantDescription description = new CommandVariantDescription(
                    resources.getString("description.command.variants.default.args"),
                    resources.getString("description.command.variants.default.desc")
            );
            return description;
        }

        @Override
        public String execute(GuildMessageReceivedEvent event, List<Argument> arguments) {
            ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());

            String commandName = arguments.get(0).getArgument();

            Command command = CommandHandlerImpl.getInstance().getCommandByName(commandName);
            return MessageFormat.format(
                    resources.getString("message.command.variants.default"),
                    command.getName(),
                    Arrays.toString(command.getVariants()).replace("[", "").replace("]", "")
            );
        }
    }

}
