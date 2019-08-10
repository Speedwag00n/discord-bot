package ilia.nemankov.togrofbot.commands.parsing;

import ilia.nemankov.togrofbot.commands.parsing.argument.DefaultArgument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultCommandParser implements CommandParser {

    private static final Logger logger = LoggerFactory.getLogger(DefaultCommandParser.class);

    @Override
    public ParsedCommand parse(String command) {
        logger.trace("Started parse command line \"{}}\"", command);

        String[] items = command.split("\\s+");

        ParsedCommand parsedCommand = new ParsedCommand(items[0]);
        for (int i = 1; i < items.length; i++) {
            parsedCommand.addArgument(new DefaultArgument(items[i]));
        }

        logger.trace("Finished parse command. Parse command \"{}\" contains {} arguments", parsedCommand.getCommandName(), parsedCommand.getArguments().size());
        return parsedCommand;
    }

}
