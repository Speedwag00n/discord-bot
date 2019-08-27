package ilia.nemankov.togrofbot.commands.impl;

import ilia.nemankov.togrofbot.commands.*;
import ilia.nemankov.togrofbot.commands.parsing.CommandVariantDescription;
import ilia.nemankov.togrofbot.commands.parsing.argument.Argument;
import ilia.nemankov.togrofbot.commands.parsing.argument.NumberArgument;
import ilia.nemankov.togrofbot.commands.parsing.matching.ArgumentsTemplate;
import ilia.nemankov.togrofbot.commands.parsing.matching.NumberArgumentMatcher;
import ilia.nemankov.togrofbot.commands.parsing.matching.StringArgumentMatcher;
import ilia.nemankov.togrofbot.settings.SettingsProvider;
import ilia.nemankov.togrofbot.util.pagination.PaginationUtils;
import ilia.nemankov.togrofbot.util.pagination.header.impl.DefaultHeader;
import ilia.nemankov.togrofbot.util.pagination.row.Row;
import ilia.nemankov.togrofbot.util.pagination.row.impl.MarkedRow;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

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
            List<Command> commands = CommandManagerMainImpl.getInstance().getCommands();

            int itemsOnPage = SettingsProvider.getInstance().getDefaultPageSize();
            int descriptionsCount = getDescriptionCount(commands);
            int maxPageNumber = PaginationUtils.maxPage(itemsOnPage, descriptionsCount);

            List<Row> commandsDescription = mapEntitiesToRows(commands, itemsOnPage, 0);

            return PaginationUtils.buildPage(new DefaultHeader(1, maxPageNumber), commandsDescription, null).toString();
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
            int page = ((NumberArgument) arguments.get(0)).getNumberArgument().intValue();

            List<Command> commands = CommandManagerMainImpl.getInstance().getCommands();

            int itemsOnPage = SettingsProvider.getInstance().getDefaultPageSize();
            int descriptionsCount = getDescriptionCount(commands);
            int maxPageNumber = PaginationUtils.maxPage(itemsOnPage, descriptionsCount);

            if (maxPageNumber < page || page <= 0) {
                return MessageFormat.format(
                        resources.getString("message.pagination.page.not_found"),
                        page
                );
            }
            List<Row> commandsDescription = mapEntitiesToRows(commands, itemsOnPage, (page - 1) * itemsOnPage);

            return PaginationUtils.buildPage(new DefaultHeader(page, maxPageNumber), commandsDescription, null).toString();
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

            int descriptionsCount = command.getDescriptions().size();
            List<Row> commandsDescription = mapEntitiesToRows(Collections.singletonList(command), descriptionsCount, 0);

            return PaginationUtils.buildPage(null, commandsDescription, null).toString();
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

    private int getDescriptionCount(List<Command> commands) {
        int count = 0;
        for (Command command : commands) {
            count += command.getDescriptions().size();
        }
        return count;
    }

    private List<Row> mapEntitiesToRows(List<Command> commands, int count, int offset) {
        if (count <= 0 || offset < 0) {
            throw new IllegalArgumentException();
        }
        List<Row> rows = new ArrayList<>();
        int mapped = 0;
        int skipped = 0;
        mapper: for (Command command : commands) {
            for (CommandVariantDescription description : command.getDescriptions()) {
                if (skipped != offset) {
                    skipped++;
                    continue;
                }
                if (mapped++ == count) {
                    break mapper;
                }
                Row row = new MarkedRow(buildContent(command.getVariants()[0], description));
                rows.add(row);
            }
        }
        return rows;
    }

}
