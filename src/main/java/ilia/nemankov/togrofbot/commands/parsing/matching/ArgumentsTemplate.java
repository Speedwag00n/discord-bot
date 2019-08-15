package ilia.nemankov.togrofbot.commands.parsing.matching;

import ilia.nemankov.togrofbot.commands.parsing.argument.Argument;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
@EqualsAndHashCode
public class ArgumentsTemplate {

    private String determinant;
    private List<ArgumentMather> mathers;

    public ArgumentsTemplate(String determinant, ArgumentMather... mathers) {
        this.determinant = determinant;
        this.mathers = Arrays.asList(mathers);
    }

    public boolean validate(List<Argument> arguments) {
        if (mathers.size() != (arguments.size() - (determinant != null ? 1 : 0))) {
            return false;
        }
        if (determinant != null && !determinant.equals(arguments.get(0).getArgument())) {
            return false;
        }
        for (int i = 0; i < mathers.size(); i++) {
            if (!mathers.get(i).match(arguments.get(i + (determinant != null ? 1 : 0)))) {
                return false;
            }
        }
        return true;
    }

}
