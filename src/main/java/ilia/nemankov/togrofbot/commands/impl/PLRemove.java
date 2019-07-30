package ilia.nemankov.togrofbot.commands.impl;

import ilia.nemankov.togrofbot.commands.Command;
import ilia.nemankov.togrofbot.database.entity.PlaylistEntity;
import ilia.nemankov.togrofbot.database.repository.ItemNotPresentedException;
import ilia.nemankov.togrofbot.database.repository.PlaylistRepository;
import ilia.nemankov.togrofbot.database.repository.impl.PlaylistRepositoryImpl;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PLRemove implements Command {

    private static final Logger logger = LoggerFactory.getLogger(PLRemove.class);

    @Override
    public String getName() {
        return this.getClass().getSimpleName().toLowerCase();
    }

    @Override
    public String[] getDescriptions() {
        return new String[] { "PLRemove <name> - Remove playlist with name <name>" };
    }

    @Override
    public void execute(GuildMessageReceivedEvent event) {
        logger.debug("Started execution of {} command", this.getClass().getSimpleName());
        logger.debug("Received message: {}", event.getMessage().getContentRaw());

        String response;
        try {
            String argument = event.getMessage().getContentRaw().split(" ")[1];

            PlaylistEntity entity = new PlaylistEntity();
            entity.setName(argument);
            entity.setGuildId(event.getGuild().getIdLong());

            PlaylistRepository repository = new PlaylistRepositoryImpl();
            repository.removePlaylist(entity);

            response = "Removed playlist with name \"" + argument + "\"";
        } catch (IndexOutOfBoundsException e) {
            response = "Name of playlist must be presented";
        } catch (ItemNotPresentedException e) {
            response = "Playlist with specified name not found";
        }

        logger.debug("Generated response for command {}: \"{}\"", this.getClass().getSimpleName(), response);
        event.getChannel().sendMessage(response).queue();

        logger.debug("Finished execution of {} command", this.getClass().getSimpleName());
    }

}
