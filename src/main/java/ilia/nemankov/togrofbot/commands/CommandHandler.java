package ilia.nemankov.togrofbot.commands;

import java.util.Map;

public interface CommandHandler {

    void initCommands(Map<String, Command> commands);

}