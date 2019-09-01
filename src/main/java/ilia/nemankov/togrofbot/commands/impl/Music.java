package ilia.nemankov.togrofbot.commands.impl;

import ilia.nemankov.togrofbot.audio.GuildMusicManager;
import ilia.nemankov.togrofbot.audio.GuildMusicManagerProvider;
import ilia.nemankov.togrofbot.audio.MusicAudioLoader;
import ilia.nemankov.togrofbot.audio.TrackScheduler;
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
import ilia.nemankov.togrofbot.database.specification.impl.PlaylistSpecificationByNameAndGuildId;
import ilia.nemankov.togrofbot.settings.SettingsProvider;
import ilia.nemankov.togrofbot.util.LinkUtils;
import ilia.nemankov.togrofbot.util.pagination.PaginationUtils;
import ilia.nemankov.togrofbot.util.pagination.header.impl.DefaultHeader;
import ilia.nemankov.togrofbot.util.pagination.row.IndexedRow;
import ilia.nemankov.togrofbot.util.pagination.row.impl.DefaultIndexedRow;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.hibernate.exception.ConstraintViolationException;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Slf4j
public class Music extends AbstractCommand {

    private static final String[] variants = new String[] {"music", "m"};

    @Override
    public String[] getVariants() {
        return variants;
    }

    public Music() {
        List<CommandItem> commandItems = new ArrayList<>();
        commandItems.add(new MusicAddByLink());
        commandItems.add(new MusicRemoveByIndex());
        commandItems.add(new MusicRemoveByLink());
        commandItems.add(new MusicShowFirstPage());
        commandItems.add(new MusicShowSpecifiedPage());
        setCommandItems(commandItems);
    }

    private class MusicAddByLink extends CommandItem {

        public MusicAddByLink() {
            super(new ArgumentsTemplate("add", new StringArgumentMatcher(), new StringArgumentMatcher()));
        }

        @Override
        public CommandVariantDescription getDescription() {
            ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());
            CommandVariantDescription description = new CommandVariantDescription(
                    resources.getString("description.command.music.add_by_link.args"),
                    resources.getString("description.command.music.add_by_link.desc")
            );
            return description;
        }

        @Override
        public String execute(GuildMessageReceivedEvent event, List<Argument> arguments) {
            ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());
            String playlist = arguments.get(1).getArgument();
            String link = arguments.get(2).getArgument();

            VideoInfo videoInfo = LinkUtils.parseLink(link);
            if (videoInfo == null) {
                return resources.getString("error.command.music.not_found");
            }

            PlaylistRepository playlistRepository = new PlaylistRepositoryImpl();
            List<PlaylistEntity> playlistEntities = playlistRepository.query(new PlaylistSpecificationByNameAndGuildId(playlist, event.getGuild().getIdLong()), "playlist-entity.without-links");

            if (playlistEntities.isEmpty()) {
                return resources.getString("message.command.playlist.not_found");
            }
            MusicLinkEntity entity = new MusicLinkEntity();
            entity.setPlaylist(playlistEntities.get(0));
            entity.setIdentifier(videoInfo.getIdentifier());
            entity.setSource(videoInfo.getSource());
            entity.setTitle(videoInfo.getTitle());
            entity.setCreationDatetime(new Date());
            try {
                MusicLinkRepository musicLinkRepository = new MusicLinkRepositoryImpl();
                musicLinkRepository.addMusicLink(entity);
            } catch (Throwable e) {
                if (e.getCause() instanceof ConstraintViolationException) {
                    return resources.getString("message.command.music.add.exists");
                } else {
                    log.error("Failed to add track", e);
                    return resources.getString("error.command.music.add.failed");
                }
            }

            GuildMusicManagerProvider provider = GuildMusicManagerProvider.getInstance();
            GuildMusicManager musicManager = provider.getGuildMusicManager(event.getGuild());
            TrackScheduler scheduler = musicManager.getTrackScheduler();

            if (scheduler.getPlayingNow() != null && playlist.equals(scheduler.getPlaylist())) {
                provider.getPlayerManager().loadItem(link, new MusicAudioLoader(scheduler));
                log.debug("Adding track pushed to playing playlist queue");
            }

            return MessageFormat.format(
                    resources.getString("message.command.music.add.successful"),
                    videoInfo.getTitle(),
                    playlist
            );
        }
    }

    private class MusicShowFirstPage extends CommandItem {

        public MusicShowFirstPage() {
            super(new ArgumentsTemplate("show", new StringArgumentMatcher()));
        }

        @Override
        public CommandVariantDescription getDescription() {
            ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());
            CommandVariantDescription description = new CommandVariantDescription(
                    resources.getString("description.command.music.show_first_page.args"),
                    resources.getString("description.command.music.show_first_page.desc")
            );
            return description;
        }

        @Override
        public String execute(GuildMessageReceivedEvent event, List<Argument> arguments) {
            ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());
            String playlist = arguments.get(1).getArgument();

            int itemsOnPage = SettingsProvider.getInstance().getDefaultPageSize();
            List<MusicLinkEntity> entities = getMusicLinksFromDB(0, itemsOnPage, event.getGuild().getIdLong(), playlist);

            int maxPageNumber = PaginationUtils.maxPage(itemsOnPage, entities.size());

            if (entities == null) {
                return resources.getString("message.command.playlist.not_found");
            }
            if (entities.isEmpty()) {
                return MessageFormat.format(
                        resources.getString("message.command.music.show.empty"),
                        playlist
                );
            }

            List<IndexedRow> titles = mapEntitiesToRows(entities);
            return PaginationUtils.buildPage(new DefaultHeader(1, maxPageNumber), titles, null).toString();
        }
    }

    private class MusicShowSpecifiedPage extends CommandItem {

        public MusicShowSpecifiedPage() {
            super(new ArgumentsTemplate("show", new StringArgumentMatcher(), new NumberArgumentMatcher()));
        }

        @Override
        public CommandVariantDescription getDescription() {
            ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());
            CommandVariantDescription description = new CommandVariantDescription(
                    resources.getString("description.command.music.show_specified_page.args"),
                    resources.getString("description.command.music.show_specified_page.desc")
            );
            return description;
        }

        @Override
        public String execute(GuildMessageReceivedEvent event, List<Argument> arguments) {
            ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());
            String playlist = arguments.get(1).getArgument();
            int page = ((NumberArgument)arguments.get(2)).getNumberArgument().intValue();

            if (page <= 0) {
                return MessageFormat.format(
                        resources.getString("message.pagination.page.not_found"),
                        page
                );
            }

            int itemsOnPage = SettingsProvider.getInstance().getDefaultPageSize();
            List<MusicLinkEntity> entities = getMusicLinksFromDB((page - 1) * itemsOnPage, itemsOnPage, event.getGuild().getIdLong(), playlist);

            int maxPageNumber = PaginationUtils.maxPage(itemsOnPage, entities.size());

            if (entities == null) {
                return resources.getString("message.command.playlist.not_found");
            }
            if (entities.isEmpty()) {
                return MessageFormat.format(
                        resources.getString("message.command.music.show.empty"),
                        playlist
                );
            }
            if (!PaginationUtils.isPageExist(page, itemsOnPage, entities.size())) {
                return MessageFormat.format(
                        resources.getString("message.pagination.page.not_found"),
                        page
                );
            }

            List<IndexedRow> titles = mapEntitiesToRows(entities);
            return PaginationUtils.buildPage(new DefaultHeader(page, maxPageNumber), titles, null).toString();
        }
    }

    private class MusicRemoveByLink extends CommandItem {

        public MusicRemoveByLink() {
            super(new ArgumentsTemplate("remove", new StringArgumentMatcher(), new StringArgumentMatcher()));
        }

        @Override
        public CommandVariantDescription getDescription() {
            ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());
            CommandVariantDescription description = new CommandVariantDescription(
                    resources.getString("description.command.music.remove_by_link.args"),
                    resources.getString("description.command.music.remove_by_link.desc")
            );
            return description;
        }

        @Override
        public String execute(GuildMessageReceivedEvent event, List<Argument> arguments) {
            ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());
            String playlist = arguments.get(1).getArgument();
            String link = arguments.get(2).getArgument();

            PlaylistRepository playlistRepository = new PlaylistRepositoryImpl();
            List<PlaylistEntity> playlistEntities = playlistRepository.query(new PlaylistSpecificationByNameAndGuildId(playlist, event.getGuild().getIdLong()), "playlist-entity.without-links");
            if (playlistEntities.isEmpty()) {
                return resources.getString("message.command.music.remove.playlist_not_found");
            }

            VideoInfo videoInfo = LinkUtils.parseLink(link);
            if (videoInfo == null) {
                return resources.getString("message.command.music.remove.incorrect_link");
            }

            MusicLinkEntity entity = new MusicLinkEntity();
            entity.setIdentifier(videoInfo.getIdentifier());
            entity.setPlaylist(playlistEntities.get(0));
            entity.setSource(videoInfo.getSource());

            MusicLinkRepository musicLinkRepository = new MusicLinkRepositoryImpl();
            if (musicLinkRepository.removeMusicLink(entity) == 0) {
                return resources.getString("message.command.music.remove.track_not_found");
            } else {
                return MessageFormat.format(
                        resources.getString("message.command.music.remove.successful"),
                        playlist
                );
            }
        }
    }

    private class MusicRemoveByIndex extends CommandItem {

        public MusicRemoveByIndex() {
            super(new ArgumentsTemplate("remove", new StringArgumentMatcher(), new NumberArgumentMatcher()));
        }

        @Override
        public CommandVariantDescription getDescription() {
            ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());
            CommandVariantDescription description = new CommandVariantDescription(
                    resources.getString("description.command.music.remove_by_index.args"),
                    resources.getString("description.command.music.remove_by_index.desc")
            );
            return description;
        }

        @Override
        public String execute(GuildMessageReceivedEvent event, List<Argument> arguments) {
            ResourceBundle resources = ResourceBundle.getBundle("lang.lang", SettingsProvider.getInstance().getLocale());
            String playlist = arguments.get(1).getArgument();
            int index = ((NumberArgument)arguments.get(2)).getNumberArgument().intValue();

            if (index <= 0) {
                return resources.getString("message.command.music.remove.incorrect_index");
            }
            List<MusicLinkEntity> entities = getMusicLinksFromDB((index - 1), 1, event.getGuild().getIdLong(), playlist);
            if (entities == null) {
                return resources.getString("message.command.music.remove.playlist_not_found");
            }
            if (entities.size() == 0) {
                return resources.getString("message.command.music.remove.track_not_found");
            }

            MusicLinkRepository musicLinkRepository = new MusicLinkRepositoryImpl();
            if (musicLinkRepository.removeMusicLink(entities.get(0)) == 0) {
                return resources.getString("message.command.music.remove.track_not_found");
            } else {
                return MessageFormat.format(
                        resources.getString("message.command.music.remove.successful"),
                        playlist
                );
            }
        }
    }

    private List<MusicLinkEntity> getMusicLinksFromDB(int from, int count, long guildId, String playlistName) {
        if (from < 0 || count <= 0 || guildId < 0) {
            log.error("Received invalid args: from={}, count={}, guildId={}", from, count, guildId);
            throw new IllegalArgumentException();
        }
        PlaylistRepository playlistRepository = new PlaylistRepositoryImpl();

        List<PlaylistEntity> playlistEntities = playlistRepository.query(new PlaylistSpecificationByNameAndGuildId(playlistName, guildId), "playlist-entity.without-links");
        if (playlistEntities.isEmpty()) {
            return null;
        }

        MusicLinkRepository musicLinkRepository = new MusicLinkRepositoryImpl();

        QuerySettings querySettings = new QuerySettings();
        querySettings.setFirstResult(from);
        querySettings.setMaxResult(count);
        return musicLinkRepository.query(new MusicLinkSpecificationByPlaylist(playlistEntities.get(0)), "music-link-entity");
    }

    private List<IndexedRow> mapEntitiesToRows(List<MusicLinkEntity> entities) {
        List<IndexedRow> indexedRows = entities.parallelStream()
                .map(entity -> new DefaultIndexedRow(entity.getTitle()))
                .collect(Collectors.toList());
        PaginationUtils.setIndexes(1, indexedRows);
        return indexedRows;
    }

}
