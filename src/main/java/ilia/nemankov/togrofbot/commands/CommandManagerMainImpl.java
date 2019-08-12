package ilia.nemankov.togrofbot.commands;

import ilia.nemankov.togrofbot.commands.impl.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class CommandManagerMainImpl implements CommandManager {

    private static final Logger logger = LoggerFactory.getLogger(CommandManagerMainImpl.class);

    private static CommandManagerMainImpl instance;

    private List<Command> commands;

    private CommandManagerMainImpl() {
        commands = new ArrayList<>();
        
        addCommand(new Roll());
        addCommand(new Lottery());
        addCommand(new Help());
        addCommand(new Join());
        addCommand(new Playlist());
        addCommand(new Skip());
        addCommand(new Music());
        addCommand(new Summon());
        addCommand(new Leave());
        addCommand(new Play());
        addCommand(new Variants());
        addCommand(new Now());

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
    public List<Command> getCommands() {
        return commands;
    }

    private void addCommand(Command command) {
        commands.add(command);
    }

}
