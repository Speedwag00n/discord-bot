package ilia.nemankov.togrofbot.commands.parsing;

import ilia.nemankov.togrofbot.commands.parsing.argument.DefaultArgument;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class DefaultCommandParser implements CommandParser {

    @Override
    public ParsedCommand parse(String command) {
        log.trace("Started parse command line \"{}}\"", command);

        List<String> items = new ArrayList<>();
        boolean closed = true;
        char empty = '1';
        char last = empty;
        char[] symbols = command.toCharArray();

        StringBuilder valueBuilder = new StringBuilder();

        for (char symbol : symbols) {
            if (symbol == '\"') {
                if (last == '\\') {
                    valueBuilder.append('\"');
                } else {
                    closed = !closed;
                }
            } else if (symbol == '\\') {
                if (last == '\\') {
                    valueBuilder.append('\\');
                    symbol = empty;
                }
            } else if (symbol == ' ') {
                if (closed) {
                    if (last != ' ') {
                        items.add(valueBuilder.toString());
                        valueBuilder = new StringBuilder();
                    }
                } else {
                    valueBuilder.append(' ');
                }
            } else {
                if (last == '\\') {
                    valueBuilder.append(last);
                }
                valueBuilder.append(symbol);
            }
            last = symbol;
        }
        if (valueBuilder.length() != 0) {
            items.add(valueBuilder.toString());
        }

        ParsedCommand parsedCommand = new ParsedCommand(items.get(0));
        for (int i = 1; i < items.size(); i++) {
            parsedCommand.addArgument(new DefaultArgument(items.get(i)));
        }

        log.trace("Finished parse command. Parse command \"{}\" contains {} arguments", parsedCommand.getCommandName(), parsedCommand.getArguments().size());
        return parsedCommand;
    }

}
