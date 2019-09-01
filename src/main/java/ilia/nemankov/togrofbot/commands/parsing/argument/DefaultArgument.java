package ilia.nemankov.togrofbot.commands.parsing.argument;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultArgument implements NumberArgument, BooleanArgument {

    @Getter
    private String argument;

    private Integer number;

    private Boolean bool;

    public DefaultArgument(String argument) {
        this.argument = argument;
        log.trace("Created argument \"{}\"", argument);

        try {
            number = Integer.parseInt(argument);
            log.trace("Argument \"{}\" can be a number", argument);
        } catch (NumberFormatException e) {
            log.trace("Argument \"{}\" can not be a number", argument);
        }

        if (argument.equals("true") || argument.equals("yes") || argument.equals("y")) {
            bool = true;
            log.trace("Argument \"{}\" can be \"true\" boolean value", argument);
        } else if (argument.equals("false") || argument.equals("no") || argument.equals("n")) {
            bool = true;
            log.trace("Argument \"{}\" can be \"false\" boolean value", argument);
        } else {
            log.trace("Argument \"{}\" can not be a boolean", argument);
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
