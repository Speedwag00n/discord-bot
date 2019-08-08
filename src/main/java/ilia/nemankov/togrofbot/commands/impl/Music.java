package ilia.nemankov.togrofbot.commands.impl;

import ilia.nemankov.togrofbot.commands.Command;
import ilia.nemankov.togrofbot.database.entity.PlaylistEntity;
import ilia.nemankov.togrofbot.database.repository.PlaylistRepository;
import ilia.nemankov.togrofbot.database.repository.impl.PlaylistRepositoryImpl;
import ilia.nemankov.togrofbot.database.specification.impl.PlaylistSpecificationByNameAndGuildId;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class Music implements Command {

    private static final Logger logger = LoggerFactory.getLogger(Music.class);

    @Override
    public String getName() {
        return this.getClass().getSimpleName().toLowerCase();
    }

    @Override
    public String[] getDescriptions() {
        return new String[] {"music <playlist> - Show the first page of all tracks list for specified playlist",
                "music <playlist> <page> - Show the <page> of all tracks list for specified playlist"};
    }

    @Override
    public void execute(GuildMessageReceivedEvent event) {
        logger.debug("Started execution of {} command", this.getClass().getSimpleName());
        logger.debug("Received message: {}", event.getMessage().getContentRaw());

        String response;
        try {
            String playlist = event.getMessage().getContentRaw().split("\\s+")[1];

            PlaylistRepository repository = new PlaylistRepositoryImpl();
            List<PlaylistEntity> entities = repository.query(new PlaylistSpecificationByNameAndGuildId(playlist, event.getGuild().getIdLong()));

            if (entities.isEmpty()) {
                response = "Playlist with specified name not found";
            } else {
                List<String> titles = entities.get(0).getLinks().parallelStream().map(entity -> entity.getTitle()).collect(Collectors.toList());

                try {
                    int page = Integer.parseInt(event.getMessage().getContentRaw().split("\\s+")[2]);
                    if (titles.isEmpty()) {
                        response = "There isn't any track in playlist \"" + playlist + "\"";
                    } else if ((page > 0) && (titles.size() / 10 + ((titles.size() % 10 == 0) ? 0 : 1)) >= page) {
                        response = showPage(page, titles);
                    } else {
                        response = "This page for command " + this.getClass().getSimpleName() + " not found";
                    }
                } catch (NumberFormatException e) {
                    response = "Argument must be a number";
                } catch (ArrayIndexOutOfBoundsException e) {
                    if (titles.isEmpty()) {
                        response = "There isn't any track in playlist \"" + playlist + "\"";
                    } else {
                        response = showPage(1, titles);
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

    private String showPage(int pageNumber, List<String> titles) {
        StringBuilder responseBuilder = new StringBuilder();

        responseBuilder.append(pageNumber + " of " + (titles.size() / 10 + ((titles.size() % 10 == 0) ? 0 : 1)) + " page:\n");
        for (int i = (pageNumber - 1) * 10; i < ((titles.size() > pageNumber * 10) ? pageNumber * 10 : titles.size()); i++) {
            responseBuilder.append((i + 1) + ") " + titles.get(i) + "\n");
        }
        return responseBuilder.toString();
    }

}
