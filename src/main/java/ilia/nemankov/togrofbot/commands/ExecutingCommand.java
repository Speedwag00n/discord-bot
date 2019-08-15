package ilia.nemankov.togrofbot.commands;

import net.dv8tion.jda.core.hooks.ListenerAdapter;

public interface ExecutingCommand {

    void subscribe(ListenerAdapter adapter);

}
