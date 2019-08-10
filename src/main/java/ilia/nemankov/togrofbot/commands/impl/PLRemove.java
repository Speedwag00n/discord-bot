package ilia.nemankov.togrofbot.commands.impl;

import ilia.nemankov.togrofbot.commands.Command;
import ilia.nemankov.togrofbot.database.entity.PlaylistEntity;
import ilia.nemankov.togrofbot.database.repository.ItemNotPresentedException;
import ilia.nemankov.togrofbot.database.repository.PlaylistRepository;
import ilia.nemankov.togrofbot.database.repository.impl.PlaylistRepositoryImpl;
import ilia.nemankov.togrofbot.settings.SettingsProvider;
import ilia.nemankov.togrofbot.util.MessageUtils;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.ResourceBundle;

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

        ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());

        String response;
        try {
            String argument = event.getMessage().getContentRaw().split("\\s+")[1];

            PlaylistEntity entity = new PlaylistEntity();
            entity.setName(argument);
            entity.setGuildId(event.getGuild().getIdLong());

            PlaylistRepository repository = new PlaylistRepositoryImpl();
            repository.removePlaylist(entity);

            response = MessageFormat.format(
                    resources.getString("message.command.playlist.remove.successful"),
                    argument
            );
        } catch (IndexOutOfBoundsException e) {
            response = MessageFormat.format(
                    resources.getString("error.argument.empty"),
                    MessageUtils.capitalizeFirstLetter(resources.getString("arguments.name_of_playlist"))
            );
        } catch (ItemNotPresentedException e) {
            response = resources.getString("message.command.playlist.not_found");
        }

        logger.debug("Generated response for command {}: \"{}\"", this.getClass().getSimpleName(), response);
        event.getChannel().sendMessage(response).queue();

        logger.debug("Finished execution of {} command", this.getClass().getSimpleName());
    }

}
