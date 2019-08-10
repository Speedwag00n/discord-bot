package ilia.nemankov.togrofbot.commands.impl;

import ilia.nemankov.togrofbot.audio.GuildMusicManager;
import ilia.nemankov.togrofbot.audio.GuildMusicManagerProvider;
import ilia.nemankov.togrofbot.audio.MusicAudioLoader;
import ilia.nemankov.togrofbot.audio.TrackScheduler;
import ilia.nemankov.togrofbot.commands.Command;
import ilia.nemankov.togrofbot.database.entity.MusicLinkEntity;
import ilia.nemankov.togrofbot.database.entity.PlaylistEntity;
import ilia.nemankov.togrofbot.database.entity.VideoInfo;
import ilia.nemankov.togrofbot.database.repository.MusicLinkRepository;
import ilia.nemankov.togrofbot.database.repository.PlaylistRepository;
import ilia.nemankov.togrofbot.database.repository.impl.MusicLinkRepositoryImpl;
import ilia.nemankov.togrofbot.database.repository.impl.PlaylistRepositoryImpl;
import ilia.nemankov.togrofbot.database.specification.impl.PlaylistSpecificationByNameAndGuildId;
import ilia.nemankov.togrofbot.settings.SettingsProvider;
import ilia.nemankov.togrofbot.util.LinkUtils;
import ilia.nemankov.togrofbot.util.MessageUtils;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class MAdd implements Command {

    private static final Logger logger = LoggerFactory.getLogger(MAdd.class);

    @Override
    public String getName() {
        return this.getClass().getSimpleName().toLowerCase();
    }

    @Override
    public String[] getDescriptions() {
        return new String[] { "MAdd <playlist_name> <link> - Add a track link to a specified playlist" };
    }

    @Override
    public void execute(GuildMessageReceivedEvent event) {
        logger.debug("Started execution of {} command", this.getClass().getSimpleName());
        logger.debug("Received message: {}", event.getMessage().getContentRaw());

        ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());

        String response = "";
        try {
            String playlist = null;
            String link = null;

            try {
                playlist = event.getMessage().getContentRaw().split("\\s+")[1];
            } catch (IndexOutOfBoundsException e) {
                response = MessageFormat.format(
                        resources.getString("error.argument.empty"),
                        MessageUtils.capitalizeFirstLetter(resources.getString("arguments.name_of_playlist"))
                );
            }
            try {
                link = event.getMessage().getContentRaw().split("\\s+")[2];
            } catch (IndexOutOfBoundsException e) {
                response = MessageFormat.format(
                        resources.getString("error.argument.empty"),
                        MessageUtils.capitalizeFirstLetter(resources.getString("arguments.track_link"))
                );
            }

            if (playlist != null && link != null) {
                VideoInfo videoInfo = LinkUtils.parseLink(link);
                if (videoInfo != null) {
                    PlaylistRepository playlistRepository = new PlaylistRepositoryImpl();
                    List<PlaylistEntity> playlistEntities = playlistRepository.query(new PlaylistSpecificationByNameAndGuildId(playlist, event.getGuild().getIdLong()));

                    if (!playlistEntities.isEmpty()) {
                        MusicLinkEntity entity = new MusicLinkEntity();
                        entity.setPlaylist(playlistEntities.get(0));
                        entity.setIdentifier(videoInfo.getIdentifier());
                        entity.setSource(videoInfo.getSource());
                        entity.setTitle(videoInfo.getTitle());
                        entity.setCreationDatetime(new Date());

                        MusicLinkRepository musicLinkRepository = new MusicLinkRepositoryImpl();
                        musicLinkRepository.addMusicLink(entity);

                        GuildMusicManagerProvider provider = GuildMusicManagerProvider.getInstance();
                        GuildMusicManager musicManager = provider.getGuildMusicManager(event.getGuild());
                        TrackScheduler scheduler = musicManager.getTrackScheduler();

                        if (scheduler.getPlayingNow() != null && playlist.equals(scheduler.getPlaylist())) {
                            provider.getPlayerManager().loadItem(link, new MusicAudioLoader(scheduler));
                            logger.debug("Adding track pushed to playing playlist queue");
                        }

                        response = MessageFormat.format(
                                resources.getString("message.command.music.add.successful"),
                                videoInfo.getTitle(),
                                playlist
                        );
                    } else {
                        response = resources.getString("message.command.playlist.not_found");
                    }
                } else {
                    response = resources.getString("error.command.music.not_found");
                }
            }
        } catch (IndexOutOfBoundsException e) {
            response = MessageFormat.format(
                    resources.getString("error.argument.empty"),
                    MessageUtils.capitalizeFirstLetter(resources.getString("arguments.name_of_playlist"))
            );
        } catch (Throwable e) {
            if (e.getCause() instanceof ConstraintViolationException) {
                response = resources.getString("message.command.music.add.exists");
            } else {
                response = resources.getString("error.command.music.add.failed");
            }
        }

        logger.debug("Generated response for command {}: \"{}\"", this.getClass().getSimpleName(), response);
        event.getChannel().sendMessage(response).queue();

        logger.debug("Finished execution of {} command", this.getClass().getSimpleName());
    }

}
