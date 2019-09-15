package ilia.nemankov.togrofbot.commands;

import ilia.nemankov.togrofbot.commands.impl.*;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CommandManagerMainImpl implements CommandManager {

    private static CommandManagerMainImpl instance;

    private List<Command> commands;

    private CommandManagerMainImpl() {
        commands = new ArrayList<>();

        addCommand(new Help());
        addCommand(new Variants());
        addCommand(new Play());
        addCommand(new Playlist());
        addCommand(new Music());
        addCommand(new Stop());
        addCommand(new Skip());
        addCommand(new Now());
        addCommand(new Join());
        addCommand(new Leave());
        addCommand(new Alias());
        addCommand(new Roll());
        addCommand(new Lottery());
        addCommand(new Summon());

        log.debug("Initialized map of commands");
    }

    public static CommandManager getInstance() {
        if (instance == null){
            log.debug("Created {} class instance", CommandManagerMainImpl.class.getSimpleName());
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
