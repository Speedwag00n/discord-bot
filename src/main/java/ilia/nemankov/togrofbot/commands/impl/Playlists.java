package ilia.nemankov.togrofbot.commands.impl;

import ilia.nemankov.togrofbot.commands.Command;
import ilia.nemankov.togrofbot.database.entity.PlaylistEntity;
import ilia.nemankov.togrofbot.database.repository.PlaylistRepository;
import ilia.nemankov.togrofbot.database.repository.impl.PlaylistRepositoryImpl;
import ilia.nemankov.togrofbot.database.specification.impl.PlaylistSpecificationByGuildId;
import ilia.nemankov.togrofbot.settings.SettingsProvider;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class Playlists implements Command {

    private static final Logger logger = LoggerFactory.getLogger(Playlists.class);

    @Override
    public String getName() {
        return this.getClass().getSimpleName().toLowerCase();
    }

    @Override
    public String[] getDescriptions() {
        return new String[] {"playlists - Show the first page of all playlists list for this guild",
                "playlists <page> - Show the page of all playlists list preset in argument of this command"};
    }

    @Override
    public void execute(GuildMessageReceivedEvent event) {
        logger.debug("Started execution of {} command", this.getClass().getSimpleName());
        logger.debug("Received message: {}", event.getMessage().getContentRaw());

        PlaylistRepository repository = new PlaylistRepositoryImpl();
        List<PlaylistEntity> entities = repository.query(new PlaylistSpecificationByGuildId(event.getGuild().getIdLong()));
        List<String> playlists = entities.parallelStream().map(entity -> entity.getName()).collect(Collectors.toList());
        String response;
        try {
            int page = Integer.parseInt(event.getMessage().getContentRaw().split("\\s+")[2]);
            System.out.println(playlists.size());
            if (playlists.isEmpty()) {
                response = "There isn't any playlist";
            } else if ((page > 0) && (playlists.size() / 10 + ((playlists.size() % 10 == 0) ? 0 : 1)) >= page) {
                response = showPage(page, playlists);
            } else {
                response = "This page for command " + this.getClass().getSimpleName() + " not found";
            }
        } catch (NumberFormatException e) {
            response = "Argument must be a number";
        } catch (ArrayIndexOutOfBoundsException e) {
            if (playlists.isEmpty()) {
                response = "There isn't any playlist";
            } else {
                response = showPage(1, playlists);
            }
        }
        logger.debug("Generated response for command {}: \"{}\"", this.getClass().getSimpleName(), response);
        event.getChannel().sendMessage(response).queue();

        logger.debug("Finished execution of {} command", this.getClass().getSimpleName());
    }

    private String showPage(int pageNumber, List<String> playlists) {
        SettingsProvider settings = SettingsProvider.getInstance();

        StringBuilder responseBuilder = new StringBuilder();
        responseBuilder.append(pageNumber + " of " + (playlists.size() / 10 + ((playlists.size() % 10 == 0) ? 0 : 1)) + " page:\n");
        for (int i = (pageNumber - 1) * 10; i < ((playlists.size() > pageNumber * 10) ? pageNumber * 10 : playlists.size()); i++) {
            responseBuilder.append(settings.getListItemSeparator() + " " +  playlists.get(i) + "\n");
        }
        return responseBuilder.toString();
    }

}
