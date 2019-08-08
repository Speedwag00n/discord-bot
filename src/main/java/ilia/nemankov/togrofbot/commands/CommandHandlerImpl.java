package ilia.nemankov.togrofbot.commands;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

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
        String commandName = event.getMessage().getContentRaw().split(" ")[0].toLowerCase();
        Command command = commands.get(commandName);
        if (command != null) {
            try {
                command.execute(event);
            } catch (Exception e) {
                logger.error("Failed to execute {} command", commandName, e);
            }
        }
    }

}
