package ilia.nemankov.togrofbot.commands;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public interface Command {

    String getName();
    String[] getDescriptions();

    void execute(GuildMessageReceivedEvent event);

}
