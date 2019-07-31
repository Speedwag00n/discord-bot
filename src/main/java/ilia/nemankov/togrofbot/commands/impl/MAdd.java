package ilia.nemankov.togrofbot.commands.impl;

import ilia.nemankov.togrofbot.commands.Command;
import ilia.nemankov.togrofbot.database.entity.MusicLinkEntity;
import ilia.nemankov.togrofbot.database.entity.PlaylistEntity;
import ilia.nemankov.togrofbot.database.repository.MusicLinkRepository;
import ilia.nemankov.togrofbot.database.repository.PlaylistRepository;
import ilia.nemankov.togrofbot.database.repository.impl.MusicLinkRepositoryImpl;
import ilia.nemankov.togrofbot.database.repository.impl.PlaylistRepositoryImpl;
import ilia.nemankov.togrofbot.database.specification.impl.PlaylistSpecificationByNameAndGuildId;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

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

        String response = "";
        try {
            String playlist = null;
            String link = null;

            try {
                playlist = event.getMessage().getContentRaw().split(" ")[1];
            } catch (IndexOutOfBoundsException e) {
                response = "Name of playlist must be presented";
            }
            try {
                link = event.getMessage().getContentRaw().split(" ")[2];
            } catch (IndexOutOfBoundsException e) {
                response = "Track link must be presented";
            }

            if (playlist != null && link != null) {
                PlaylistRepository playlistRepository = new PlaylistRepositoryImpl();
                List<PlaylistEntity> playlistEntities = playlistRepository.query(new PlaylistSpecificationByNameAndGuildId(playlist, event.getGuild().getIdLong()));

                if (!playlistEntities.isEmpty()) {
                    MusicLinkEntity entity = new MusicLinkEntity();
                    entity.setPlaylist(playlistEntities.get(0));
                    entity.setLink(link);

                    MusicLinkRepository musicLinkRepository = new MusicLinkRepositoryImpl();
                    musicLinkRepository.addMusicLink(entity);

                    response = "Added new track to a \"" + playlist + "\" playlist";
                } else {
                    response = "Specified playlist not found";
                }
            }
        } catch (IndexOutOfBoundsException e) {
            response = "Name of playlist must be presented";
        } catch (Throwable e) {
            if (e.getCause() instanceof ConstraintViolationException) {
                response = "This link already exists in specified playlist";
            } else {
                response = "Failed to add track";
            }
        }

        logger.debug("Generated response for command {}: \"{}\"", this.getClass().getSimpleName(), response);
        event.getChannel().sendMessage(response).queue();

        logger.debug("Finished execution of {} command", this.getClass().getSimpleName());
    }

}
