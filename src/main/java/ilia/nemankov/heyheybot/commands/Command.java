package ilia.nemankov.heyheybot.commands;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public interface Command {

    String getName();
    String getDescription();

    void execute(MessageReceivedEvent event);

}
