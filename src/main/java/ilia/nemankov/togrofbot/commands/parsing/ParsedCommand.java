package ilia.nemankov.togrofbot.commands.parsing;

import ilia.nemankov.togrofbot.commands.parsing.argument.Argument;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ParsedCommand {

    @Getter
    private String commandName;
    private List<Argument> arguments;

    public ParsedCommand(String commandName) {
        this.commandName = commandName;
        this.arguments = new ArrayList<>();
    }

    public void addArgument(Argument argument) {
        arguments.add(argument);
    }

    public List<Argument> getArguments() {
        return Collections.unmodifiableList(arguments);
    }

}
