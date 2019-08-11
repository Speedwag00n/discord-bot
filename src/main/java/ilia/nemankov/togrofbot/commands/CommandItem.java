package ilia.nemankov.togrofbot.commands;

import ilia.nemankov.togrofbot.commands.parsing.CommandVariantDescription;
import ilia.nemankov.togrofbot.commands.parsing.argument.Argument;
import ilia.nemankov.togrofbot.commands.parsing.matching.ArgumentsTemplate;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public abstract class CommandItem {

    private ArgumentsTemplate argumentsTemplate;

    public abstract CommandVariantDescription getDescription();

    public abstract String execute(GuildMessageReceivedEvent event, List<Argument> arguments);

}
