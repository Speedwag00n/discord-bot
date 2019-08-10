package ilia.nemankov.togrofbot.commands.impl;

import ilia.nemankov.togrofbot.audio.EmotionAudioLoader;
import ilia.nemankov.togrofbot.audio.GuildMusicManager;
import ilia.nemankov.togrofbot.audio.GuildMusicManagerProvider;
import ilia.nemankov.togrofbot.commands.Command;
import ilia.nemankov.togrofbot.settings.SettingsProvider;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.managers.AudioManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ResourceBundle;

public class Join implements Command {

    private static final Logger logger = LoggerFactory.getLogger(Join.class);

    @Override
    public String getName() {
        return this.getClass().getSimpleName().toLowerCase();
    }

    @Override
    public String[] getDescriptions() {
        return new String[] { "join - The bot joins your voice channel with a greeting" };
    }

    @Override
    public void execute(GuildMessageReceivedEvent event) {
        logger.debug("Started execution of {} command", this.getClass().getSimpleName());
        logger.debug("Received message: {}", event.getMessage().getContentRaw());

        ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());

        VoiceChannel channel = event.getMember().getVoiceState().getChannel();
        String response;
        if (channel == null) {
            response = resources.getString("error.connection.no_chosen_voice_channel");
        } else if (!event.getGuild().getSelfMember().hasPermission(channel, Permission.VOICE_CONNECT)) {
            response = resources.getString("error.permissions.join_voice_channel");
        } else {
            AudioManager audioManager = event.getGuild().getAudioManager();
            if (audioManager.isAttemptingToConnect()) {
                response = resources.getString("error.connection.try_to_connect");
            } else {
                GuildMusicManagerProvider provider = GuildMusicManagerProvider.getInstance();
                GuildMusicManager musicManager = provider.getGuildMusicManager(event.getGuild());

                audioManager.openAudioConnection(channel);

                provider.getPlayerManager().loadItem("src/main/resources/audio/greeting.mp3", new EmotionAudioLoader(musicManager.getTrackScheduler()));

                response = resources.getString("message.command.join.greeting");
            }
        }

        logger.debug("Generated response for command {}: \"{}\"", this.getClass().getSimpleName(), response);
        event.getChannel().sendMessage(response).queue();

        logger.debug("Finished execution of {} command", this.getClass().getSimpleName());
    }

}
