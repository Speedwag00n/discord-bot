package ilia.nemankov.togrofbot.commands.impl;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import ilia.nemankov.togrofbot.audio.GuildMusicManager;
import ilia.nemankov.togrofbot.audio.GuildMusicManagerProvider;
import ilia.nemankov.togrofbot.audio.MusicAudioLoader;
import ilia.nemankov.togrofbot.audio.TrackScheduler;
import ilia.nemankov.togrofbot.commands.Command;
import ilia.nemankov.togrofbot.database.entity.PlaylistEntity;
import ilia.nemankov.togrofbot.database.repository.PlaylistRepository;
import ilia.nemankov.togrofbot.database.repository.impl.PlaylistRepositoryImpl;
import ilia.nemankov.togrofbot.database.specification.impl.PlaylistSpecificationByNameAndGuildId;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.managers.AudioManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class Skip implements Command {

    private static final Logger logger = LoggerFactory.getLogger(Skip.class);

    @Override
    public String getName() {
        return this.getClass().getSimpleName().toLowerCase();
    }

    @Override
    public String[] getDescriptions() {
        return new String[] { "skip - Skip playing track" };
    }

    @Override
    public void execute(GuildMessageReceivedEvent event) {
        logger.debug("Started execution of {} command", this.getClass().getSimpleName());
        logger.debug("Received message: {}", event.getMessage().getContentRaw());

        String response;

        GuildMusicManagerProvider provider = GuildMusicManagerProvider.getInstance();
        GuildMusicManager musicManager = provider.getGuildMusicManager(event.getGuild());

        TrackScheduler trackScheduler = musicManager.getTrackScheduler();
        if (trackScheduler.getPlayingNow() == null) {
            response = "Nothing to skip";
        } else {
            trackScheduler.nextTrack();

            response = null;
        }

        logger.debug("Generated response for command {}: \"{}\"", this.getClass().getSimpleName(), response);
        if (response != null)
            event.getChannel().sendMessage(response).queue();

        logger.debug("Finished execution of {} command", this.getClass().getSimpleName());
    }

}
