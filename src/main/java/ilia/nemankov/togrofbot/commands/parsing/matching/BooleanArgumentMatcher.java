package ilia.nemankov.togrofbot.commands.parsing.matching;

import ilia.nemankov.togrofbot.commands.parsing.argument.Argument;
import ilia.nemankov.togrofbot.commands.parsing.argument.BooleanArgument;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class BooleanArgumentMatcher extends ArgumentMather {

    @Override
    public boolean match(Argument argument) {
        return (argument instanceof BooleanArgument) && (((BooleanArgument) argument).getBooleanArgument() != null);
    }

}
