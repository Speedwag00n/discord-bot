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
import ilia.nemankov.togrofbot.database.repository.impl.MusicLinkRepositoryImpl;
import ilia.nemankov.togrofbot.database.repository.impl.PlaylistRepositoryImpl;
import ilia.nemankov.togrofbot.database.specification.impl.PlaylistSpecificationByNameAndGuildId;
import ilia.nemankov.togrofbot.settings.SettingsProvider;
import ilia.nemankov.togrofbot.util.LinkUtils;
import ilia.nemankov.togrofbot.util.pagination.PageNotFoundException;
import ilia.nemankov.togrofbot.util.pagination.PaginationUtils;
import ilia.nemankov.togrofbot.util.pagination.header.impl.DefaultHeader;
import ilia.nemankov.togrofbot.util.pagination.row.Row;
import ilia.nemankov.togrofbot.util.pagination.row.impl.DefaultIndexedRow;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class Music extends AbstractCommand {

    private static final Logger logger = LoggerFactory.getLogger(Music.class);

    private static final String[] variants = new String[] {"music", "m"};

    @Override
    public String[] getVariants() {
        return variants;
    }

    public Music() {
        List<CommandItem> commandItems = new ArrayList<>();
        commandItems.add(new MusicAddByLink());
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
            try {
                VideoInfo videoInfo = LinkUtils.parseLink(link);
                if (videoInfo != null) {
                    PlaylistRepository playlistRepository = new PlaylistRepositoryImpl();
                    List<PlaylistEntity> playlistEntities = playlistRepository.query(new PlaylistSpecificationByNameAndGuildId(playlist, event.getGuild().getIdLong()));

                    if (!playlistEntities.isEmpty()) {
                        MusicLinkEntity entity = new MusicLinkEntity();
                        entity.setPlaylist(playlistEntities.get(0));
                        entity.setIdentifier(videoInfo.getIdentifier());
                        entity.setSource(videoInfo.getSource());
                        entity.setTitle(videoInfo.getTitle());
                        entity.setCreationDatetime(new Date());

                        MusicLinkRepository musicLinkRepository = new MusicLinkRepositoryImpl();
                        musicLinkRepository.addMusicLink(entity);

                        GuildMusicManagerProvider provider = GuildMusicManagerProvider.getInstance();
                        GuildMusicManager musicManager = provider.getGuildMusicManager(event.getGuild());
                        TrackScheduler scheduler = musicManager.getTrackScheduler();

                        if (scheduler.getPlayingNow() != null && playlist.equals(scheduler.getPlaylist())) {
                            provider.getPlayerManager().loadItem(link, new MusicAudioLoader(scheduler));
                            logger.debug("Adding track pushed to playing playlist queue");
                        }

                        return MessageFormat.format(
                                resources.getString("message.command.music.add.successful"),
                                videoInfo.getTitle(),
                                playlist
                        );
                    } else {
                        return resources.getString("message.command.playlist.not_found");
                    }
                } else {
                    return resources.getString("error.command.music.not_found");
                }
            } catch (Throwable e) {
                if (e.getCause() instanceof ConstraintViolationException) {
                    return resources.getString("message.command.music.add.exists");
                } else {
                    return resources.getString("error.command.music.add.failed");
                }
            }
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

            PlaylistRepository repository = new PlaylistRepositoryImpl();
            List<PlaylistEntity> entities = repository.query(new PlaylistSpecificationByNameAndGuildId(playlist, event.getGuild().getIdLong()));

            if (entities.isEmpty()) {
                return resources.getString("message.command.playlist.create.exists");
            }
            List<Row> titles = entities.get(0).getLinks()
                    .parallelStream()
                    .map(entity -> new DefaultIndexedRow(entity.getTitle()))
                    .collect(Collectors.toList());
            try {
                return PaginationUtils.buildPage(1, new DefaultHeader(), titles, null).toString();
            } catch (PageNotFoundException e) {
                logger.error("Error caused by building first page for command {}", this.getClass().getSimpleName(), e);
                return MessageFormat.format(
                        resources.getString("message.command.playlist.show.failed"),
                        1
                );
            }
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
            PlaylistRepository repository = new PlaylistRepositoryImpl();
            List<PlaylistEntity> entities = repository.query(new PlaylistSpecificationByNameAndGuildId(playlist, event.getGuild().getIdLong()));

            if (entities.isEmpty()) {
                return resources.getString("message.command.playlist.create.exists");
            }
            List<Row> titles = entities.get(0).getLinks()
                    .parallelStream()
                    .map(entity -> new DefaultIndexedRow(entity.getTitle()))
                    .collect(Collectors.toList());
            int page = ((NumberArgument)arguments.get(2)).getNumberArgument().intValue();
            try {
                return PaginationUtils.buildPage(page, new DefaultHeader(), titles, null).toString();
            } catch (PageNotFoundException e) {
                return MessageFormat.format(
                        resources.getString("message.pagination.page.not_found"),
                        page
                );
            }
        }
    }

}
