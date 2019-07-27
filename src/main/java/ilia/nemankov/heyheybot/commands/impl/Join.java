package ilia.nemankov.heyheybot.commands.impl;

import ilia.nemankov.heyheybot.commands.Command;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.managers.AudioManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class Join implements Command {

    private static final Logger logger = LoggerFactory.getLogger(Join.class);

    @Override
    public String getName() {
        return this.getClass().getSimpleName().toLowerCase();
    }

    @Override
    public String[] getDescriptions() {
        return new String[] { "join - The bot joins your voice channel" };
    }

    @Override
    public void execute(GuildMessageReceivedEvent event) {
        logger.debug("Started execution of {} command", this.getClass().getSimpleName());
        logger.debug("Received message: {}", event.getMessage().getContentRaw());

        VoiceChannel channel = event.getMember().getVoiceState().getChannel();
        String response;
        if (channel == null) {
            response = "You aren't connected to any voice channel. Please, select one";
        } else if (!event.getGuild().getSelfMember().hasPermission(channel, Permission.VOICE_CONNECT)) {
            response = "I don't have enough permissions to connect to this voice channel";
        } else {
            AudioManager audioManager = event.getGuild().getAudioManager();
            if (audioManager.isAttemptingToConnect()) {
                response = "I'm trying to connect now. Please, wait";
            } else {
                audioManager.openAudioConnection(channel);

                response = "I'm here!";
            }
        }

        logger.debug("Generated response for command {}: \"{}\"", this.getClass().getSimpleName(), response);
        event.getChannel().sendMessage(response).queue();

        logger.debug("Finished execution of {} command", this.getClass().getSimpleName());
    }

}
