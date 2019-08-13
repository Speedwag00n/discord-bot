package ilia.nemankov.togrofbot.commands.impl;

import ilia.nemankov.togrofbot.audio.GuildMusicManager;
import ilia.nemankov.togrofbot.audio.GuildMusicManagerProvider;
import ilia.nemankov.togrofbot.audio.MusicAudioLoader;
import ilia.nemankov.togrofbot.commands.AbstractCommand;
import ilia.nemankov.togrofbot.commands.CommandItem;
import ilia.nemankov.togrofbot.commands.parsing.CommandVariantDescription;
import ilia.nemankov.togrofbot.commands.parsing.argument.Argument;
import ilia.nemankov.togrofbot.commands.parsing.argument.NumberArgument;
import ilia.nemankov.togrofbot.commands.parsing.matching.ArgumentsTemplate;
import ilia.nemankov.togrofbot.commands.parsing.matching.NumberArgumentMatcher;
import ilia.nemankov.togrofbot.commands.parsing.matching.StringArgumentMatcher;
import ilia.nemankov.togrofbot.database.entity.MusicLinkEntity;
import ilia.nemankov.togrofbot.database.entity.PlaylistEntity;
import ilia.nemankov.togrofbot.database.entity.VideoInfo;
import ilia.nemankov.togrofbot.database.repository.PlaylistRepository;
import ilia.nemankov.togrofbot.database.repository.impl.PlaylistRepositoryImpl;
import ilia.nemankov.togrofbot.database.specification.impl.PlaylistSpecificationByGuildId;
import ilia.nemankov.togrofbot.database.specification.impl.PlaylistSpecificationByNameAndGuildId;
import ilia.nemankov.togrofbot.settings.SettingsProvider;
import ilia.nemankov.togrofbot.util.LinkUtils;
import ilia.nemankov.togrofbot.util.pagination.PageNotFoundException;
import ilia.nemankov.togrofbot.util.pagination.PaginationUtils;
import ilia.nemankov.togrofbot.util.pagination.header.impl.DefaultHeader;
import ilia.nemankov.togrofbot.util.pagination.row.Row;
import ilia.nemankov.togrofbot.util.pagination.row.impl.MarkedRow;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.managers.AudioManager;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class Playlist extends AbstractCommand {

    private static final Logger logger = LoggerFactory.getLogger(Playlist.class);

    private static final String[] variants = new String[] {"playlist", "pl"};

    @Override
    public String[] getVariants() {
        return variants;
    }

    public Playlist() {
        List<CommandItem> commandItems = new ArrayList<>();
        commandItems.add(new PlaylistAdd());
        commandItems.add(new PlaylistRemoveByName());
        commandItems.add(new PlaylistShowFirstPage());
        commandItems.add(new PlaylistShowSpecifiedPage());
        commandItems.add(new PlaylistPlay());
        setCommandItems(commandItems);
    }

    private class PlaylistAdd extends CommandItem {

        public PlaylistAdd() {
            super(new ArgumentsTemplate("add", new StringArgumentMatcher()));
        }

        @Override
        public CommandVariantDescription getDescription() {
            ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());
            CommandVariantDescription description = new CommandVariantDescription(
                    resources.getString("description.command.playlist.add.args"),
                    resources.getString("description.command.playlist.add.desc")
            );
            return description;
        }

        @Override
        public String execute(GuildMessageReceivedEvent event, List<Argument> arguments) {
            ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());

            String name = arguments.get(1).getArgument();

            try {
                PlaylistEntity entity = new PlaylistEntity();
                entity.setName(name);
                entity.setGuildId(event.getGuild().getIdLong());

                PlaylistRepository repository = new PlaylistRepositoryImpl();
                repository.addPlaylist(entity);

                return MessageFormat.format(
                        resources.getString("message.command.playlist.create.successful"),
                        name
                );
            } catch (Throwable e) {
                if (e instanceof ConstraintViolationException) {
                    return resources.getString("message.command.playlist.create.exists");
                } else {
                    return resources.getString("message.command.playlist.create.failed");
                }
            }
        }
    }

    private class PlaylistRemoveByName extends CommandItem {

        public PlaylistRemoveByName() {
            super(new ArgumentsTemplate("remove", new StringArgumentMatcher()));
        }

        @Override
        public CommandVariantDescription getDescription() {
            ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());
            CommandVariantDescription description = new CommandVariantDescription(
                    resources.getString("description.command.playlist.remove_by_name.args"),
                    resources.getString("description.command.playlist.remove_by_name.desc")
            );
            return description;
        }

        @Override
        public String execute(GuildMessageReceivedEvent event, List<Argument> arguments) {
            ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());

            String name = arguments.get(1).getArgument();

            PlaylistEntity entity = new PlaylistEntity();
            entity.setName(name);
            entity.setGuildId(event.getGuild().getIdLong());

            PlaylistRepository repository = new PlaylistRepositoryImpl();
            if (repository.removePlaylist(entity) == 0) {
                return resources.getString("message.command.playlist.not_found");
            } else {
                return MessageFormat.format(
                        resources.getString("message.command.playlist.remove.successful"),
                        name
                );
            }
        }
    }

    private class PlaylistShowFirstPage extends CommandItem {

        public PlaylistShowFirstPage() {
            super(new ArgumentsTemplate("show"));
        }

        @Override
        public CommandVariantDescription getDescription() {
            ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());
            CommandVariantDescription description = new CommandVariantDescription(
                    resources.getString("description.command.playlist.show_first_page.args"),
                    resources.getString("description.command.playlist.show_first_page.desc")
            );
            return description;
        }

        @Override
        public String execute(GuildMessageReceivedEvent event, List<Argument> arguments) {
            ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());

            PlaylistRepository repository = new PlaylistRepositoryImpl();
            List<PlaylistEntity> entities = repository.query(new PlaylistSpecificationByGuildId(event.getGuild().getIdLong()));
            List<Row> playlists = entities
                    .parallelStream()
                    .map(entity -> new MarkedRow(entity.getName()))
                    .collect(Collectors.toList());
            if (playlists.isEmpty()) {
                return resources.getString("message.command.playlist.show.empty");
            }
            try {
                return PaginationUtils.buildPage(1, new DefaultHeader(), playlists, null).toString();
            } catch (PageNotFoundException e) {
                logger.error("Error caused by building first page for command {}", this.getClass().getSimpleName(), e);
                return MessageFormat.format(
                        resources.getString("message.command.playlist.show.failed"),
                        1
                );
            }
        }
    }

    private class PlaylistShowSpecifiedPage extends CommandItem {

        public PlaylistShowSpecifiedPage() {
            super(new ArgumentsTemplate("show", new NumberArgumentMatcher()));
        }

        @Override
        public CommandVariantDescription getDescription() {
            ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());
            CommandVariantDescription description = new CommandVariantDescription(
                    resources.getString("description.command.playlist.show_specified_page.args"),
                    resources.getString("description.command.playlist.show_specified_page.desc")
            );
            return description;
        }

        @Override
        public String execute(GuildMessageReceivedEvent event, List<Argument> arguments) {
            ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());

            PlaylistRepository repository = new PlaylistRepositoryImpl();
            List<PlaylistEntity> entities = repository.query(new PlaylistSpecificationByGuildId(event.getGuild().getIdLong()));
            List<Row> playlists = entities
                    .parallelStream()
                    .map(entity -> new MarkedRow(entity.getName()))
                    .collect(Collectors.toList());
            if (playlists.isEmpty()) {
                return resources.getString("message.command.playlist.show.empty");
            }
            int page = ((NumberArgument)arguments.get(1)).getNumberArgument().intValue();
            try {
                return PaginationUtils.buildPage(page, new DefaultHeader(), playlists, null).toString();
            } catch (PageNotFoundException e) {
                return MessageFormat.format(
                        resources.getString("message.pagination.page.not_found"),
                        page
                );
            }
        }
    }

    private class PlaylistPlay extends CommandItem {

        public PlaylistPlay() {
            super(new ArgumentsTemplate("play", new StringArgumentMatcher()));
        }

        @Override
        public CommandVariantDescription getDescription() {
            ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());
            CommandVariantDescription description = new CommandVariantDescription(
                    resources.getString("description.command.playlist.play.args"),
                    resources.getString("description.command.playlist.play.desc")
            );
            return description;
        }

        @Override
        public String execute(GuildMessageReceivedEvent event, List<Argument> arguments) {
            ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());

            VoiceChannel channel = event.getMember().getVoiceState().getChannel();
            String playlist = arguments.get(1).getArgument();
            PlaylistRepository repository = new PlaylistRepositoryImpl();
            List<PlaylistEntity> playlistEntities = repository.query(new PlaylistSpecificationByNameAndGuildId(playlist, event.getGuild().getIdLong()));

            if (playlistEntities.isEmpty()) {
                return resources.getString("message.command.playlist.not_found");
            }
            List<MusicLinkEntity> musicLinkEntities = playlistEntities.get(0).getLinks();

            if (channel == null) {
                return resources.getString("error.connection.no_chosen_voice_channel");
            } else if (!event.getGuild().getSelfMember().hasPermission(channel, Permission.VOICE_CONNECT)) {
                return resources.getString("error.permissions.join_voice_channel");
            } else {
                AudioManager audioManager = event.getGuild().getAudioManager();
                if (audioManager.isAttemptingToConnect()) {
                    return resources.getString("error.connection.try_to_connect");
                } else {
                    GuildMusicManagerProvider provider = GuildMusicManagerProvider.getInstance();
                    GuildMusicManager musicManager = provider.getGuildMusicManager(event.getGuild());

                    musicManager.getAudioPlayer().stopTrack();
                    musicManager.getTrackScheduler().clearAll();

                    audioManager.openAudioConnection(channel);

                    musicManager.getTrackScheduler().setPlaylist(playlist);

                    musicManager.getTrackScheduler().setCommunicationChannel(event.getChannel());
                    for (MusicLinkEntity musicLinkEntity : musicLinkEntities) {
                        VideoInfo info = new VideoInfo(musicLinkEntity.getIdentifier(), musicLinkEntity.getSource(), musicLinkEntity.getTitle());
                        String link;
                        if ((link = LinkUtils.buildLink(info)) != null) {
                            provider.getPlayerManager().loadItem(link, new MusicAudioLoader(musicManager.getTrackScheduler()));
                        }
                    }

                    return null;
                }
            }
        }
    }

}
