package ilia.nemankov.togrofbot.commands.parsing;

import ilia.nemankov.togrofbot.commands.parsing.argument.DefaultArgument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class DefaultCommandParser implements CommandParser {

    private static final Logger logger = LoggerFactory.getLogger(DefaultCommandParser.class);

    @Override
    public ParsedCommand parse(String command) {
        logger.trace("Started parse command line \"{}}\"", command);

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
                    items.add(valueBuilder.toString());
                    valueBuilder = new StringBuilder();
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

        logger.trace("Finished parse command. Parse command \"{}\" contains {} arguments", parsedCommand.getCommandName(), parsedCommand.getArguments().size());
        return parsedCommand;
    }

}
