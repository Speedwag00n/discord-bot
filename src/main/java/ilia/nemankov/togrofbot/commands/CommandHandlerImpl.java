package ilia.nemankov.togrofbot.commands;

import ilia.nemankov.togrofbot.commands.parsing.DefaultCommandParser;
import ilia.nemankov.togrofbot.commands.parsing.ParsedCommand;
import ilia.nemankov.togrofbot.settings.SettingsProvider;
import ilia.nemankov.togrofbot.util.MessageUtils;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class CommandHandlerImpl extends ListenerAdapter implements CommandHandler {

    private static final Logger logger = LoggerFactory.getLogger(CommandHandlerImpl.class);

    private static CommandHandlerImpl instance;

    private Map<String, Command> commands;

    private CommandHandlerImpl() {
        commands = new HashMap<>();
    }

    public static CommandHandler getInstance() {
        if (instance == null){
            logger.debug("Created {} class instance", CommandHandlerImpl.class.getSimpleName());
            instance = new CommandHandlerImpl();
        }
        return instance;
    }

    @Override
    public void initCommands(List<Command> commands) {
        this.commands = new HashMap<>();

        String commandPrefix = SettingsProvider.getInstance().getCommandPrefix();

        for (Command command : commands) {
            for (String variant : command.getVariants()) {
                this.commands.put(commandPrefix + variant, command);
            }
        }
        logger.info("Initialized commands map of {}", this.getClass().getSimpleName());
    }

    @Override
    public Command getCommandByName(String name) {
        if (!commands.isEmpty()) {
            String commandPrefix = SettingsProvider.getInstance().getCommandPrefix();
            return commands.get(commandPrefix + name);
        } else {
            return null;
        }
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        ParsedCommand parsedCommand = (new DefaultCommandParser()).parse(event.getMessage().getContentRaw());
        Command command = commands.get(parsedCommand.getCommandName());
        if (command != null) {
            try {
                logger.debug("Started execution of {} command", this.getClass().getSimpleName());
                logger.debug("Received message: {}", event.getMessage().getContentRaw());
                String response = command.execute(event, parsedCommand.getArguments());
                if (response != null) {
                    MessageUtils.sendTextResponse(event, response, false);
                }
                logger.debug("Finished execution of {} command", parsedCommand.getCommandName());
            } catch (Exception e) {
                ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());
                MessageUtils.sendTextResponse(event, MessageFormat.format(resources.getString("error.command.failed"), parsedCommand.getCommandName()), false);
                logger.error("Failed to execute {} command", parsedCommand.getCommandName(), e);
            }
        }
    }

}
