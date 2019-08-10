package ilia.nemankov.togrofbot.commands.parsing.argument;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultArgument implements NumberArgument, BooleanArgument {

    private static final Logger logger = LoggerFactory.getLogger(DefaultArgument.class);

    @Getter
    private String argument;

    private Integer number;

    private Boolean bool;

    public DefaultArgument(String argument) {
        this.argument = argument;
        logger.trace("Created argument \"{}\"", argument);

        try {
            number = Integer.parseInt(argument);
            logger.trace("Argument \"{}\" can be a number", argument);
        } catch (NumberFormatException e) {
            logger.trace("Argument \"{}\" can not be a number", argument);
        }

        if (argument.equals("true") || argument.equals("yes") || argument.equals("y")) {
            bool = true;
            logger.trace("Argument \"{}\" can be \"true\" boolean value", argument);
        } else if (argument.equals("false") || argument.equals("no") || argument.equals("n")) {
            bool = true;
            logger.trace("Argument \"{}\" can be \"false\" boolean value", argument);
        } else {
            logger.trace("Argument \"{}\" can not be a boolean", argument);
        }
    }

    @Override
    public Integer getNumberArgument() {
        return number;
    }

    @Override
    public Boolean getBooleanArgument() {
        return bool;
    }

}
