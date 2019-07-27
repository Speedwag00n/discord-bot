package ilia.nemankov.heyheybot.commands;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public interface Command {

    String getName();
    String[] getDescriptions();

    void execute(MessageReceivedEvent event);

}
