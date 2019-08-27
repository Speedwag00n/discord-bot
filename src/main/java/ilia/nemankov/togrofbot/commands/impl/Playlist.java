package ilia.nemankov.togrofbot.commands.impl;

import ilia.nemankov.togrofbot.audio.AudioLoaderInfo;
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
import ilia.nemankov.togrofbot.database.repository.MusicLinkRepository;
import ilia.nemankov.togrofbot.database.repository.PlaylistRepository;
import ilia.nemankov.togrofbot.database.repository.QuerySettings;
import ilia.nemankov.togrofbot.database.repository.impl.MusicLinkRepositoryImpl;
import ilia.nemankov.togrofbot.database.repository.impl.PlaylistRepositoryImpl;
import ilia.nemankov.togrofbot.database.specification.impl.MusicLinkSpecificationByPlaylist;
import ilia.nemankov.togrofbot.database.specification.impl.PlaylistSpecificationByGuildId;
import ilia.nemankov.togrofbot.database.specification.impl.PlaylistSpecificationByNameAndGuildId;
import ilia.nemankov.togrofbot.settings.SettingsProvider;
import ilia.nemankov.togrofbot.util.LinkUtils;
import ilia.nemankov.togrofbot.util.VoiceUtils;
import ilia.nemankov.togrofbot.util.pagination.PaginationUtils;
import ilia.nemankov.togrofbot.util.pagination.header.impl.DefaultHeader;
import ilia.nemankov.togrofbot.util.pagination.row.Row;
import ilia.nemankov.togrofbot.util.pagination.row.impl.MarkedRow;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
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
        commandItems.add(new PlaylistPlayFromTrack());
        commandItems.add(new PlaylistUpdate());
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
                    logger.error("Failed to create playlist", e);
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

            int itemsOnPage = SettingsProvider.getInstance().getDefaultPageSize();
            int maxPageNumber = getMaxPageNumber(event.getGuild().getIdLong(), itemsOnPage);
            if (maxPageNumber == 0) {
                return resources.getString("message.command.playlist.show.empty");
            }

            List<PlaylistEntity> entities = getPlaylistsFromDB(1, itemsOnPage, event.getGuild().getIdLong());
            List<Row> playlists = mapEntitiesToRows(entities);

            return PaginationUtils.buildPage(new DefaultHeader(1, maxPageNumber), playlists, null).toString();
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
            int page = ((NumberArgument)arguments.get(1)).getNumberArgument().intValue();

            int itemsOnPage = SettingsProvider.getInstance().getDefaultPageSize();
            int maxPageNumber = getMaxPageNumber(event.getGuild().getIdLong(), itemsOnPage);
            if (maxPageNumber == 0) {
                return resources.getString("message.command.playlist.show.empty");
            }
            if (maxPageNumber < page || page <= 0) {
                return MessageFormat.format(
                        resources.getString("message.pagination.page.not_found"),
                        page
                );
            }

            List<PlaylistEntity> entities = getPlaylistsFromDB(page, itemsOnPage, event.getGuild().getIdLong());
            List<Row> playlists = mapEntitiesToRows(entities);

            return PaginationUtils.buildPage(new DefaultHeader(page, maxPageNumber), playlists, null).toString();
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
            String playlist = arguments.get(1).getArgument();

            PlaylistRepository repository = new PlaylistRepositoryImpl();
            List<PlaylistEntity> playlistEntities = repository.query(new PlaylistSpecificationByNameAndGuildId(playlist, event.getGuild().getIdLong()), "playlist-entity.with-links");
            if (playlistEntities.isEmpty()) {
                return resources.getString("message.command.playlist.not_found");
            }
            List<MusicLinkEntity> musicLinkEntities = playlistEntities.get(0).getLinks();

            List<String> links = buildLinks(musicLinkEntities);

            AudioLoaderInfo info = new AudioLoaderInfo();

            info.setVoiceChannel(event.getMember().getVoiceState().getChannel());
            info.setGuild(event.getGuild());
            info.setCommunicationChannel(event.getMessage().getTextChannel());
            info.setLinks(links);

            String result = VoiceUtils.playMusic(info, false);
            VoiceUtils.setPlayingPlaylist(event.getGuild(), playlist);
            return result;
        }
    }

    private class PlaylistPlayFromTrack extends CommandItem {

        public PlaylistPlayFromTrack() {
            super(new ArgumentsTemplate("play", new StringArgumentMatcher(), new NumberArgumentMatcher()));
        }

        @Override
        public CommandVariantDescription getDescription() {
            ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());
            CommandVariantDescription description = new CommandVariantDescription(
                    resources.getString("description.command.playlist.play_from_track.args"),
                    resources.getString("description.command.playlist.play_from_track.desc")
            );
            return description;
        }

        @Override
        public String execute(GuildMessageReceivedEvent event, List<Argument> arguments) {
            ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());
            String playlist = arguments.get(1).getArgument();
            int fromTrack = ((NumberArgument)arguments.get(2)).getNumberArgument().intValue();

            PlaylistRepository playlistRepository = new PlaylistRepositoryImpl();
            List<PlaylistEntity> playlistEntities = playlistRepository.query(new PlaylistSpecificationByNameAndGuildId(playlist, event.getGuild().getIdLong()), "playlist-entity.without-links");
            if (playlistEntities.isEmpty()) {
                return resources.getString("message.command.playlist.not_found");
            }

            MusicLinkRepository musicLinkRepository = new MusicLinkRepositoryImpl();

            long tracksCount = musicLinkRepository.count(new MusicLinkSpecificationByPlaylist(playlistEntities.get(0)));

            if (fromTrack <= 0 || tracksCount < fromTrack) {
                return MessageFormat.format(
                        resources.getString("message.command.playlist.play.incorrect_index"),
                        playlist
                );
            }

            QuerySettings querySettings = new QuerySettings();
            querySettings.setFirstResult(fromTrack);
            List<MusicLinkEntity> musicLinkEntities = musicLinkRepository.query(new MusicLinkSpecificationByPlaylist(playlistEntities.get(0)), "music-link-entity");

            List<String> links = buildLinks(musicLinkEntities);

            AudioLoaderInfo info = new AudioLoaderInfo();

            info.setVoiceChannel(event.getMember().getVoiceState().getChannel());
            info.setGuild(event.getGuild());
            info.setCommunicationChannel(event.getMessage().getTextChannel());
            info.setLinks(links);

            String result = VoiceUtils.playMusic(info, false);
            VoiceUtils.setPlayingPlaylist(event.getGuild(), playlist);
            return result;
        }
    }

    private class PlaylistUpdate extends CommandItem {

        public PlaylistUpdate() {
            super(new ArgumentsTemplate("rename", new StringArgumentMatcher(), new StringArgumentMatcher()));
        }

        @Override
        public CommandVariantDescription getDescription() {
            ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());
            CommandVariantDescription description = new CommandVariantDescription(
                    resources.getString("description.command.playlist.update.args"),
                    resources.getString("description.command.playlist.update.desc")
            );
            return description;
        }

        @Override
        public String execute(GuildMessageReceivedEvent event, List<Argument> arguments) {
            ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());
            String oldName = arguments.get(1).getArgument();
            String newName = arguments.get(2).getArgument();

            PlaylistRepository repository = new PlaylistRepositoryImpl();
            List<PlaylistEntity> entities = repository.query(new PlaylistSpecificationByNameAndGuildId(oldName, event.getGuild().getIdLong()), "playlist-entity.without-links");
            if (entities.isEmpty()) {
                return resources.getString("message.command.playlist.update.not_found");
            }

            PlaylistEntity entity = entities.get(0);
            entity.setName(newName);

            try {
                repository.updatePlaylist(entity);
            } catch (Throwable e) {
                if (e.getCause() instanceof ConstraintViolationException) {
                    return resources.getString("message.command.playlist.update.exists");
                } else {
                    logger.error("Failed to update playlist", e);
                    return resources.getString("message.command.playlist.update.failed");
                }
            }

            return resources.getString("message.command.playlist.update.successful");
        }
    }

    private int getMaxPageNumber(long guildId, int itemsOnPage) {
        if (guildId < 0 || itemsOnPage <= 0) {
            throw new IllegalArgumentException();
        }
        PlaylistRepository repository = new PlaylistRepositoryImpl();

        return PaginationUtils.maxPage(itemsOnPage, repository.count(new PlaylistSpecificationByGuildId(guildId)));
    }

    private List<PlaylistEntity> getPlaylistsFromDB(int page, int itemsOnPage, long guildId) {
        if (page <= 0 || itemsOnPage <= 0 || guildId < 0) {
            logger.error("Received invalid args: page={}, itemsOnPage={}, guildId={}", page, itemsOnPage, guildId);
            throw new IllegalArgumentException();
        }
        PlaylistRepository repository = new PlaylistRepositoryImpl();

        QuerySettings querySettings = new QuerySettings();
        querySettings.setFirstResult((page - 1) * itemsOnPage);
        querySettings.setMaxResult(itemsOnPage);

        return repository.query(new PlaylistSpecificationByGuildId(guildId), "playlist-entity.without-links", querySettings);
    }

    private List<Row> mapEntitiesToRows(List<PlaylistEntity> entities) {
        return entities
                .parallelStream()
                .map(entity -> new MarkedRow(entity.getName()))
                .collect(Collectors.toList());
    }

    private List<String> buildLinks(List<MusicLinkEntity> entities) {
        List<String> links = new ArrayList<>();
        for (MusicLinkEntity musicLinkEntity : entities) {
            VideoInfo info = new VideoInfo(musicLinkEntity.getIdentifier(), musicLinkEntity.getSource(), musicLinkEntity.getTitle());
            String link;
            if ((link = LinkUtils.buildLink(info)) != null) {
                links.add(link);
            }
        }
        return links;
    }

}
