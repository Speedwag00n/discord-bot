package ilia.nemankov.togrofbot.commands.impl;

import ilia.nemankov.togrofbot.commands.Command;
import ilia.nemankov.togrofbot.database.entity.PlaylistEntity;
import ilia.nemankov.togrofbot.database.repository.PlaylistRepository;
import ilia.nemankov.togrofbot.database.repository.impl.PlaylistRepositoryImpl;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PLAdd implements Command {

    private static final Logger logger = LoggerFactory.getLogger(PLAdd.class);

    @Override
    public String getName() {
        return this.getClass().getSimpleName().toLowerCase();
    }

    @Override
    public String[] getDescriptions() {
        return new String[] { "PLAdd <name> - Create new playlist with name <name>" };
    }

    @Override
    public void execute(GuildMessageReceivedEvent event) {
        logger.debug("Started execution of {} command", this.getClass().getSimpleName());
        logger.debug("Received message: {}", event.getMessage().getContentRaw());

        String response;
        try {
            String argument = event.getMessage().getContentRaw().split("\\s+")[1];

            PlaylistEntity entity = new PlaylistEntity();
            entity.setName(argument);
            entity.setGuildId(event.getGuild().getIdLong());

            PlaylistRepository repository = new PlaylistRepositoryImpl();
            repository.addPlaylist(entity);

            response = "Created playlist with name \"" + argument + "\"";
        } catch (IndexOutOfBoundsException e) {
            response = "Name of playlist must be presented";
        } catch (Throwable e) {
            if (e instanceof ConstraintViolationException) {
                response = "Playlist with specified name already exists";
            } else {
                response = "Failed to create playlist";
            }
        }

        logger.debug("Generated response for command {}: \"{}\"", this.getClass().getSimpleName(), response);
        event.getChannel().sendMessage(response).queue();

        logger.debug("Finished execution of {} command", this.getClass().getSimpleName());
    }

}
