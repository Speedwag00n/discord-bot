package ilia.nemankov.togrofbot.commands.impl;

import ilia.nemankov.togrofbot.commands.Command;
import ilia.nemankov.togrofbot.database.entity.PlaylistEntity;
import ilia.nemankov.togrofbot.database.repository.PlaylistRepository;
import ilia.nemankov.togrofbot.database.repository.impl.PlaylistRepositoryImpl;
import ilia.nemankov.togrofbot.database.specification.impl.PlaylistSpecificationByNameAndGuildId;
import ilia.nemankov.togrofbot.settings.SettingsProvider;
import ilia.nemankov.togrofbot.util.MessageUtils;
import ilia.nemankov.togrofbot.util.pagination.PageNotFoundException;
import ilia.nemankov.togrofbot.util.pagination.PaginationUtils;
import ilia.nemankov.togrofbot.util.pagination.header.impl.DefaultHeader;
import ilia.nemankov.togrofbot.util.pagination.row.Row;
import ilia.nemankov.togrofbot.util.pagination.row.impl.DefaultIndexedRow;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.List;
import java.util.ResourceBundle;
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

        ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());

        String response;
        try {
            String playlist = event.getMessage().getContentRaw().split("\\s+")[1];

            PlaylistRepository repository = new PlaylistRepositoryImpl();
            List<PlaylistEntity> entities = repository.query(new PlaylistSpecificationByNameAndGuildId(playlist, event.getGuild().getIdLong()));

            if (entities.isEmpty()) {
                response = resources.getString("message.command.playlist.create.exists");
            } else {
                List<Row> titles = entities.get(0).getLinks()
                        .parallelStream()
                        .map(entity -> new DefaultIndexedRow(entity.getTitle()))
                        .collect(Collectors.toList());
                try {
                    int page = Integer.parseInt(event.getMessage().getContentRaw().split("\\s+")[2]);
                    if (titles.isEmpty()) {
                        response = MessageFormat.format(
                                resources.getString("message.command.music.show.empty"),
                                playlist
                        );
                    } else {
                        response = PaginationUtils.buildPage(page, new DefaultHeader(), titles, null).toString();
                    }
                } catch (NumberFormatException e) {
                    response = "Argument must be a number";
                } catch (ArrayIndexOutOfBoundsException e) {
                    if (titles.isEmpty()) {
                        response = MessageFormat.format(
                                resources.getString("message.command.music.show.empty"),
                                playlist
                        );
                    } else {
                        try {
                            response = PaginationUtils.buildPage(1, new DefaultHeader(), titles, null).toString();
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
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            response = MessageFormat.format(
                    resources.getString("error.argument.empty"),
                    MessageUtils.capitalizeFirstLetter(resources.getString("arguments.name_of_playlist"))
            );
        }

        logger.debug("Generated response for command {}: \"{}\"", this.getClass().getSimpleName(), response);
        event.getChannel().sendMessage(response).queue();

        logger.debug("Finished execution of {} command", this.getClass().getSimpleName());
    }

}
