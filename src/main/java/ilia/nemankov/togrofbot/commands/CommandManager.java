package ilia.nemankov.togrofbot.commands;

import java.util.List;
import java.util.Map;

public interface CommandManager {

    Map<String, Command> getCommands();
    List<Command> getOrderedCommands();

}
