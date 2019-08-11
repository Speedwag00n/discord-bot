package ilia.nemankov.togrofbot.commands;

import ilia.nemankov.togrofbot.commands.parsing.DefaultCommandParser;
import ilia.nemankov.togrofbot.commands.parsing.ParsedCommand;
import ilia.nemankov.togrofbot.settings.SettingsProvider;
import ilia.nemankov.togrofbot.util.MessageUtils;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
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

    public void initCommands(Map<String, Command> commands) {
        this.commands = commands;
        logger.info("Initialized commands map of {}", this.getClass().getSimpleName());
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
                logger.debug("Finished execution of {} command", this.getClass().getSimpleName());
            } catch (Exception e) {
                ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());
                MessageUtils.sendTextResponse(event, resources.getString("error.command.failed"), false);
                logger.error("Failed to execute {} command", parsedCommand.getCommandName(), e);
            }
        }
    }

}
