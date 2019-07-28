package ilia.nemankov.togrofbot.commands.impl;

import ilia.nemankov.togrofbot.commands.Command;
import ilia.nemankov.togrofbot.commands.CommandManager;
import ilia.nemankov.togrofbot.commands.CommandManagerMainImpl;
import ilia.nemankov.togrofbot.settings.SettingsProvider;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Help implements Command {

    private static final Logger logger = LoggerFactory.getLogger(Help.class);

    @Override
    public String getName() {
        return this.getClass().getSimpleName().toLowerCase();
    }

    @Override
    public String[] getDescriptions() {
        return new String[] {"help - Show the first page of all commands list",
                "help <page> - Show the page of all commands list preset in argument of this command"};
    }

    @Override
    public void execute(GuildMessageReceivedEvent event) {
        logger.debug("Started execution of {} command", this.getClass().getSimpleName());
        logger.debug("Received message: {}", event.getMessage().getContentRaw());

        CommandManager commandManager = CommandManagerMainImpl.getInstance();
        Map<String, Command> commands = commandManager.getCommands();
        List<String> commandsDescription = new ArrayList<>();
        for (Command command : commands.values()) {
            commandsDescription.addAll(Arrays.asList(command.getDescriptions()));
        }
        String response;
        try {
            int argument = Integer.parseInt(event.getMessage().getContentRaw().split(" ")[1]);
            if ((argument > 0) && (commandsDescription.size() / 10 + 1) >= argument) {
                response = showPage(argument, commandsDescription);
            } else {
                response = "This page for command " + this.getClass().getSimpleName() + " not found";
            }
        } catch (NumberFormatException e) {
            response = "Argument must be a number";
        } catch (ArrayIndexOutOfBoundsException e) {
            response = showPage(1, commandsDescription);
        }
        logger.debug("Generated response for command {}: \"{}\"", this.getClass().getSimpleName(), response);
        event.getChannel().sendMessage(response).queue();

        logger.debug("Finished execution of {} command", this.getClass().getSimpleName());
    }

    private String showPage(int pageNumber, List<String> commandsDescription) {
        SettingsProvider settings = SettingsProvider.getInstance();

        StringBuilder responseBuilder = new StringBuilder();
        responseBuilder.append(pageNumber + " of " + (commandsDescription.size() / 10 + 1) + " page:\n");
        for (int i = (pageNumber - 1) * 10; i < ((commandsDescription.size() > pageNumber * 10) ? pageNumber * 10 : commandsDescription.size()); i++) {
            responseBuilder.append(settings.getListItemSeparator() + " " +  settings.getCommandPrefix() + commandsDescription.get(i) + "\n");
        }
        return responseBuilder.toString();
    }

}
