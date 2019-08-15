package ilia.nemankov.togrofbot.commands;

import java.util.List;

public interface CommandHandler {

    void initCommands(List<Command> commands);
    Command getCommandByName(String name);

}