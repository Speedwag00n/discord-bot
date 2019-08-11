package ilia.nemankov.togrofbot.commands.impl;

import ilia.nemankov.togrofbot.commands.*;
import ilia.nemankov.togrofbot.commands.parsing.argument.Argument;
import ilia.nemankov.togrofbot.commands.parsing.argument.NumberArgument;
import ilia.nemankov.togrofbot.commands.parsing.matching.ArgumentsTemplate;
import ilia.nemankov.togrofbot.commands.parsing.matching.NumberArgumentMatcher;
import ilia.nemankov.togrofbot.settings.SettingsProvider;
import ilia.nemankov.togrofbot.util.pagination.PageNotFoundException;
import ilia.nemankov.togrofbot.util.pagination.PaginationUtils;
import ilia.nemankov.togrofbot.util.pagination.header.impl.DefaultHeader;
import ilia.nemankov.togrofbot.util.pagination.row.Row;
import ilia.nemankov.togrofbot.util.pagination.row.impl.MarkedRow;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class Help extends AbstractCommand {

    private static final Logger logger = LoggerFactory.getLogger(Help.class);

    private static final String[] variants = new String[] {"help", "h"};

    @Override
    public String getName() {
        return this.getClass().getSimpleName().toLowerCase();
    }

    @Override
    public String[] getDescriptions() {
        return new String[] {"- Show the first page of all commands list",
                "<page> - Show the page of all commands list preset in argument of this command"};
    }

    @Override
    public String[] getVariants() {
        return variants;
    }

    public Help() {
        List<CommandItem> commandItems = new ArrayList<>();
        commandItems.add(new HelpShowFirstPage());
        commandItems.add(new HelpShowSpecifiedPage());
        setCommandItems(commandItems);
    }

    private class HelpShowFirstPage extends CommandItem {

        public HelpShowFirstPage() {
            super(new ArgumentsTemplate(null));
        }

        @Override
        public String execute(GuildMessageReceivedEvent event, List<Argument> arguments) {
            ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());

            CommandManager commandManager = CommandManagerMainImpl.getInstance();
            List<Command> commands = commandManager.getCommands();
            List<Row> commandsDescription = new ArrayList<>();
            String commandPrefix = SettingsProvider.getInstance().getCommandPrefix();
            for (Command command : commands) {
                commandsDescription.addAll(Arrays
                        .stream(command.getDescriptions())
                        .map(description -> new MarkedRow(commandPrefix + command.getVariants()[0] + " " + description))
                        .collect(Collectors.toList()));
            }
            try {
                return PaginationUtils.buildPage(1, new DefaultHeader(), commandsDescription, null).toString();
            } catch (PageNotFoundException e) {
                logger.error("Error caused by building first page for command {}", this.getClass().getSimpleName(), e);
                return MessageFormat.format(
                        resources.getString("message.command.playlist.show.failed"),
                        1
                );
            }
        }
    }

    private class HelpShowSpecifiedPage extends CommandItem {

        public HelpShowSpecifiedPage() {
            super(new ArgumentsTemplate(null, new NumberArgumentMatcher()));
        }

        @Override
        public String execute(GuildMessageReceivedEvent event, List<Argument> arguments) {
            ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());

            CommandManager commandManager = CommandManagerMainImpl.getInstance();
            List<Command> commands = commandManager.getCommands();
            List<Row> commandsDescription = new ArrayList<>();
            String commandPrefix = SettingsProvider.getInstance().getCommandPrefix();
            for (Command command : commands) {
                commandsDescription.addAll(Arrays
                        .stream(command.getDescriptions())
                        .map(description -> new MarkedRow(commandPrefix + command.getVariants()[0] + " " + description))
                        .collect(Collectors.toList()));
            }
            int page = ((NumberArgument) arguments.get(0)).getNumberArgument().intValue();
            try {
                return PaginationUtils.buildPage(page, new DefaultHeader(), commandsDescription, null).toString();
            } catch (PageNotFoundException e) {
                return MessageFormat.format(
                        resources.getString("message.pagination.page.not_found"),
                        page
                );
            }
        }
    }

}
