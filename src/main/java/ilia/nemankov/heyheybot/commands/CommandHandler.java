package ilia.nemankov.heyheybot.commands;

import java.util.Map;

public interface CommandHandler {

    void initCommands(Map<String, Command> commands);

}