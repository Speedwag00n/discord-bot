package ilia.nemankov.togrofbot.commands.impl;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import ilia.nemankov.togrofbot.audio.EmotionAudioLoader;
import ilia.nemankov.togrofbot.audio.GuildMusicManager;
import ilia.nemankov.togrofbot.audio.GuildMusicManagerProvider;
import ilia.nemankov.togrofbot.audio.MusicAudioLoader;
import ilia.nemankov.togrofbot.commands.Command;
import ilia.nemankov.togrofbot.database.entity.MusicLinkEntity;
import ilia.nemankov.togrofbot.database.entity.PlaylistEntity;
import ilia.nemankov.togrofbot.database.repository.MusicLinkRepository;
import ilia.nemankov.togrofbot.database.repository.PlaylistRepository;
import ilia.nemankov.togrofbot.database.repository.impl.MusicLinkRepositoryImpl;
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

public class PLPlay implements Command {

    private static final Logger logger = LoggerFactory.getLogger(PLPlay.class);

    @Override
    public String getName() {
        return this.getClass().getSimpleName().toLowerCase();
    }

    @Override
    public String[] getDescriptions() {
        return new String[] { "PLPlay <playlist> - The bot starts play tracks from the specified playlist" };
    }

    @Override
    public void execute(GuildMessageReceivedEvent event) {
        logger.debug("Started execution of {} command", this.getClass().getSimpleName());
        logger.debug("Received message: {}", event.getMessage().getContentRaw());


        VoiceChannel channel = event.getMember().getVoiceState().getChannel();
        String response;
        try {
            String playlist = event.getMessage().getContentRaw().split("\\s+")[1];

            PlaylistRepository repository = new PlaylistRepositoryImpl();
            List<PlaylistEntity> entities = repository.query(new PlaylistSpecificationByNameAndGuildId(playlist, event.getGuild().getIdLong()));

            if (entities.isEmpty()) {
                response = "Playlist with specified name not found";
            } else {
                List<String> links = entities.get(0).getLinks().parallelStream().map(entity -> entity.getLink()).collect(Collectors.toList());

                if (channel == null) {
                    response = "You aren't connected to any voice channel. Please, select one";
                } else if (!event.getGuild().getSelfMember().hasPermission(channel, Permission.VOICE_CONNECT)) {
                    response = "I don't have enough permissions to connect to this voice channel";
                } else {
                    AudioManager audioManager = event.getGuild().getAudioManager();
                    if (audioManager.isAttemptingToConnect()) {
                        response = "I'm trying to connect now. Please, wait";
                    } else {
                        GuildMusicManagerProvider provider = GuildMusicManagerProvider.getInstance();
                        GuildMusicManager musicManager = provider.getGuildMusicManager(event.getGuild());

                        musicManager.getAudioPlayer().stopTrack();
                        musicManager.getTrackScheduler().clearAll();

                        audioManager.openAudioConnection(channel);

                        AudioLoadResultHandler audioLoader = new MusicAudioLoader(musicManager.getTrackScheduler());

                        for (String link : links) {
                            provider.getPlayerManager().loadItem(link, audioLoader);
                        }

                        response = "Started play tracks from playlist " + playlist;
                    }
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            response = "Name of playlist must be presented";
        }

        logger.debug("Generated response for command {}: \"{}\"", this.getClass().getSimpleName(), response);
        event.getChannel().sendMessage(response).queue();

        logger.debug("Finished execution of {} command", this.getClass().getSimpleName());
    }

}
