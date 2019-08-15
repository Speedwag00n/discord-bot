package ilia.nemankov.togrofbot.commands.impl;

import ilia.nemankov.togrofbot.commands.*;
import ilia.nemankov.togrofbot.commands.parsing.CommandVariantDescription;
import ilia.nemankov.togrofbot.commands.parsing.argument.Argument;
import ilia.nemankov.togrofbot.commands.parsing.argument.NumberArgument;
import ilia.nemankov.togrofbot.commands.parsing.matching.ArgumentsTemplate;
import ilia.nemankov.togrofbot.commands.parsing.matching.NumberArgumentMatcher;
import ilia.nemankov.togrofbot.commands.parsing.matching.StringArgumentMatcher;
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
    public String[] getVariants() {
        return variants;
    }

    public Help() {
        List<CommandItem> commandItems = new ArrayList<>();
        commandItems.add(new HelpShowFirstPage());
        commandItems.add(new HelpShowSpecifiedPage());
        commandItems.add(new HelpShowCommandUsages());
        setCommandItems(commandItems);
    }

    private class HelpShowFirstPage extends CommandItem {

        public HelpShowFirstPage() {
            super(new ArgumentsTemplate(null));
        }

        @Override
        public CommandVariantDescription getDescription() {
            ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());
            CommandVariantDescription description = new CommandVariantDescription(
                    resources.getString("description.command.help.show_first_page.args"),
                    resources.getString("description.command.help.show_first_page.desc")
            );
            return description;
        }

        @Override
        public String execute(GuildMessageReceivedEvent event, List<Argument> arguments) {
            ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());

            CommandManager commandManager = CommandManagerMainImpl.getInstance();
            List<Command> commands = commandManager.getCommands();
            List<Row> commandsDescription = new ArrayList<>();
            for (Command command : commands) {
                commandsDescription.addAll(
                        command
                                .getDescriptions()
                                .stream()
                                .map(description -> new MarkedRow(buildContent(command.getVariants()[0], description)))
                                .collect(Collectors.toList())
                );
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
        public CommandVariantDescription getDescription() {
            ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());
            CommandVariantDescription description = new CommandVariantDescription(
                    resources.getString("description.command.help.show_specified_page.args"),
                    resources.getString("description.command.help.show_specified_page.desc")
            );
            return description;
        }

        @Override
        public String execute(GuildMessageReceivedEvent event, List<Argument> arguments) {
            ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());

            CommandManager commandManager = CommandManagerMainImpl.getInstance();
            List<Command> commands = commandManager.getCommands();
            List<Row> commandsDescription = new ArrayList<>();
            for (Command command : commands) {
                commandsDescription.addAll(
                        command
                                .getDescriptions()
                                .stream()
                                .map(description -> new MarkedRow(buildContent(command.getVariants()[0], description)))
                                .collect(Collectors.toList())
                );
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

    private class HelpShowCommandUsages extends CommandItem {

        public HelpShowCommandUsages() {
            super(new ArgumentsTemplate(null, new StringArgumentMatcher()));
        }

        @Override
        public CommandVariantDescription getDescription() {
            ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());
            CommandVariantDescription description = new CommandVariantDescription(
                    resources.getString("description.command.help.show_command_usages.args"),
                    resources.getString("description.command.help.show_command_usages.desc")
            );
            return description;
        }

        @Override
        public String execute(GuildMessageReceivedEvent event, List<Argument> arguments) {
            ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());

            String commandName = arguments.get(0).getArgument();
            Command command = CommandHandlerImpl.getInstance().getCommandByName(commandName);
            if (command == null) {
                return resources.getString("message.command.help.command.not_found");
            }
            List<Row> commandsDescription = new ArrayList<>();
            commandsDescription.addAll(
                    command
                            .getDescriptions()
                            .stream()
                            .map(description -> new MarkedRow(buildContent(command.getVariants()[0], description)))
                            .collect(Collectors.toList())
            );
            try {
                return PaginationUtils.buildPage(1, commandsDescription.size(), null, commandsDescription, null).toString();
            } catch (PageNotFoundException e) {
                logger.error("Error caused by building help page for command {}", commandName, e);
                return null;
            }
        }
    }

    private String buildContent(String commandName, CommandVariantDescription description) {
        String commandPrefix = SettingsProvider.getInstance().getCommandPrefix();
        String content = commandPrefix
                + commandName + " "
                + ((description.getCommandArgs() != null && !description.getCommandArgs().equals("")) ? description.getCommandArgs()+ " " : "")
                + "- "
                + description.getDescription();
        return content;
    }

}
