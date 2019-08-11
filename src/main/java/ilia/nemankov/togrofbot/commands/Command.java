package ilia.nemankov.togrofbot.commands;

import ilia.nemankov.togrofbot.commands.parsing.CommandVariantDescription;
import ilia.nemankov.togrofbot.commands.parsing.argument.Argument;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.util.List;

public interface Command {

    String getName();
    List<CommandVariantDescription> getDescriptions();
    String[] getVariants();

    String execute(GuildMessageReceivedEvent event, List<Argument> arguments);

}
