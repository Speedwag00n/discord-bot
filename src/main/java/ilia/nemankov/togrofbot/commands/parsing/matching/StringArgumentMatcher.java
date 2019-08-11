package ilia.nemankov.togrofbot.commands.parsing.matching;

import ilia.nemankov.togrofbot.commands.parsing.argument.Argument;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class StringArgumentMatcher extends ArgumentMather {

    @Override
    public boolean match(Argument argument) {
        return argument.getArgument() != null;
    }

}
