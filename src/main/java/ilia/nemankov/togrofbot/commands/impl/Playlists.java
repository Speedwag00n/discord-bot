package ilia.nemankov.togrofbot.commands.impl;

import ilia.nemankov.togrofbot.commands.Command;
import ilia.nemankov.togrofbot.database.entity.PlaylistEntity;
import ilia.nemankov.togrofbot.database.repository.PlaylistRepository;
import ilia.nemankov.togrofbot.database.repository.impl.PlaylistRepositoryImpl;
import ilia.nemankov.togrofbot.database.specification.impl.PlaylistSpecificationByGuildId;
import ilia.nemankov.togrofbot.settings.SettingsProvider;
import ilia.nemankov.togrofbot.util.MessageUtils;
import ilia.nemankov.togrofbot.util.pagination.PageNotFoundException;
import ilia.nemankov.togrofbot.util.pagination.PaginationUtils;
import ilia.nemankov.togrofbot.util.pagination.header.impl.DefaultHeader;
import ilia.nemankov.togrofbot.util.pagination.row.impl.MarkedRow;
import ilia.nemankov.togrofbot.util.pagination.row.Row;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.List;
import java.util.ResourceBundle;
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

        ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());

        PlaylistRepository repository = new PlaylistRepositoryImpl();
        List<PlaylistEntity> entities = repository.query(new PlaylistSpecificationByGuildId(event.getGuild().getIdLong()));
        List<Row> playlists = entities
                .parallelStream()
                .map(entity -> new MarkedRow(entity.getName()))
                .collect(Collectors.toList());
        String response;
        try {
            int page = Integer.parseInt(event.getMessage().getContentRaw().split("\\s+")[2]);
            if (playlists.isEmpty()) {
                response = resources.getString("message.command.music.show.empty");
            } else {
                response = PaginationUtils.buildPage(page, new DefaultHeader(), playlists, null).toString();
            }
        } catch (NumberFormatException e) {
            response = MessageFormat.format(
                    resources.getString("error.argument.type"),
                    MessageUtils.capitalizeFirstLetter(resources.getString("arguments.argument")),
                    resources.getString("arguments.number")
            );
        } catch (ArrayIndexOutOfBoundsException e) {
            if (playlists.isEmpty()) {
                response = resources.getString("message.command.music.show.empty");
            } else {
                try {
                    response = PaginationUtils.buildPage(1, new DefaultHeader(), playlists, null).toString();
                } catch (PageNotFoundException e1) {
                    response = MessageFormat.format(
                            resources.getString("message.command.playlist.show.failed"),
                            1
                    );
                    logger.error("Error caused by building first page for command {}", this.getClass().getSimpleName(), e1);
                }
            }
        } catch (PageNotFoundException e) {
            response = MessageFormat.format(
                    resources.getString("message.pagination.page.not_found"),
                    Integer.parseInt(event.getMessage().getContentRaw().split("\\s+")[1]),
                    this.getName()
            );
        }
        logger.debug("Generated response for command {}: \"{}\"", this.getClass().getSimpleName(), response);
        event.getChannel().sendMessage(response).queue();

        logger.debug("Finished execution of {} command", this.getClass().getSimpleName());
    }

}
