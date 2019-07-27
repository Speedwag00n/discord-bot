package ilia.nemankov.heyheybot.commands;

import ilia.nemankov.heyheybot.commands.impl.Lottery;
import ilia.nemankov.heyheybot.commands.impl.Roll;
import ilia.nemankov.heyheybot.settings.SettingsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class CommandManagerMainImpl implements CommandManager {

    private static final Logger logger = LoggerFactory.getLogger(CommandManagerMainImpl.class);

    private static CommandManagerMainImpl instance;

    private Map<String, Command> commands;

    private CommandManagerMainImpl() {
        commands = new HashMap<>();
        SettingsProvider settings = SettingsProvider.getInstance();
        String commandPrefix = settings.getCommandPrefix();
        
        addCommand(new Roll(), commandPrefix);
        addCommand(new Lottery(), commandPrefix);

        logger.debug("Initialized map of commands");
    }

    public static CommandManager getInstance() {
        if (instance == null){
            logger.debug("Created {} class instance", CommandManagerMainImpl.class.getSimpleName());
            instance = new CommandManagerMainImpl();
        }
        return instance;
    }

    @Override
    public Map<String, Command> getCommands() {
        return commands;
    }

    private void addCommand(Command command, String commandPrefix) {
        commands.put(commandPrefix + command.getName(), command);
    }

}
