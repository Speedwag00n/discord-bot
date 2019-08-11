package ilia.nemankov.togrofbot.commands.parsing.matching;

import ilia.nemankov.togrofbot.commands.parsing.argument.Argument;
import ilia.nemankov.togrofbot.commands.parsing.argument.NumberArgument;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class NumberArgumentMatcher extends ArgumentMather {

    @Override
    public boolean match(Argument argument) {
        return (argument instanceof NumberArgument) && (((NumberArgument) argument).getNumberArgument() != null);
    }

}
