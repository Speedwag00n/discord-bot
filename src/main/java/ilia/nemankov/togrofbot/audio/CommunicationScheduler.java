package ilia.nemankov.togrofbot.audio;

import net.dv8tion.jda.core.entities.TextChannel;

public interface CommunicationScheduler extends Scheduler {

    TextChannel getCommunicationChannel();

    void setCommunicationChannel(TextChannel communicationChannel);

}
