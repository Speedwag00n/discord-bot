package ilia.nemankov.togrofbot.commands;

import ilia.nemankov.togrofbot.commands.impl.*;
import ilia.nemankov.togrofbot.settings.SettingsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandManagerMainImpl implements CommandManager {

    private static final Logger logger = LoggerFactory.getLogger(CommandManagerMainImpl.class);

    private static CommandManagerMainImpl instance;

    private Map<String, Command> commands;
    private List<Command> orderedCommands;

    private CommandManagerMainImpl() {
        commands = new HashMap<>();
        orderedCommands = new ArrayList<>();
        SettingsProvider settings = SettingsProvider.getInstance();
        String commandPrefix = settings.getCommandPrefix();
        
        addCommand(new Roll(), commandPrefix);
        addCommand(new Lottery(), commandPrefix);
        addCommand(new Help(), commandPrefix);
        addCommand(new Join(), commandPrefix);
        addCommand(new Playlist(), commandPrefix);
        addCommand(new Skip(), commandPrefix);
        addCommand(new Music(), commandPrefix);
        addCommand(new Summon(), commandPrefix);

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

    @Override
    public List<Command> getOrderedCommands() {
        return orderedCommands;
    }

    private void addCommand(Command command, String commandPrefix) {
        commands.put(commandPrefix + command.getName(), command);
        orderedCommands.add(command);
    }

}
