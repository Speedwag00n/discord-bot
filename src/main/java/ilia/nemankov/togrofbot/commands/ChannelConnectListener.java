package ilia.nemankov.togrofbot.commands;

import ilia.nemankov.togrofbot.audio.AudioLoaderInfo;
import ilia.nemankov.togrofbot.audio.GuildMusicManager;
import ilia.nemankov.togrofbot.audio.GuildMusicManagerProvider;
import ilia.nemankov.togrofbot.database.entity.PresentationEntity;
import ilia.nemankov.togrofbot.database.entity.VideoInfo;
import ilia.nemankov.togrofbot.database.repository.PresentationRepository;
import ilia.nemankov.togrofbot.database.repository.impl.PresentationRepositoryImpl;
import ilia.nemankov.togrofbot.database.specification.impl.PresentationSpecificationByGuildId;
import ilia.nemankov.togrofbot.database.specification.impl.PresentationSpecificationByUserId;
import ilia.nemankov.togrofbot.database.specification.impl.composite.AndSpecification;
import ilia.nemankov.togrofbot.util.LinkUtils;
import ilia.nemankov.togrofbot.util.MessageUtils;
import ilia.nemankov.togrofbot.util.VoiceUtils;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.List;

public class ChannelConnectListener extends ListenerAdapter {

    @Override
    public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
        introduceUser(event.getGuild(), event.getMember());
    }

    @Override
    public void onGuildVoiceMove(GuildVoiceMoveEvent event) {
        introduceUser(event.getGuild(), event.getMember());
    }

    private void introduceUser(Guild guild, Member member) {
        GuildMusicManagerProvider provider = GuildMusicManagerProvider.getInstance();
        GuildMusicManager musicManager = provider.getGuildMusicManager(guild);

        if (musicManager.getTrackScheduler().getPlayingNow() == null) {
            PresentationRepository repository = new PresentationRepositoryImpl();
            List<PresentationEntity> entities = repository.query(
                    new AndSpecification<>(
                            new PresentationSpecificationByUserId(member.getUser().getIdLong()),
                            new PresentationSpecificationByGuildId(guild.getIdLong())
                    ),
                    null
            );
            if (entities.isEmpty()) {
                return;
            }
            PresentationEntity presentation = entities.get(0);
            if (!presentation.isActive()) {
                return;
            }
            if (presentation.getTitle() != null) {
                AudioLoaderInfo audioLoaderInfo = new AudioLoaderInfo();
                audioLoaderInfo.setGuild(guild);
                VoiceChannel voiceChannel = member.getVoiceState().getChannel();
                if (voiceChannel != null) {
                    if (presentation.getDuration() != 0) {
                        musicManager.getTrackScheduler().setLimitMillis(presentation.getDuration());
                    }
                    audioLoaderInfo.setVoiceChannel(voiceChannel);
                    VideoInfo videoInfo = new VideoInfo(presentation.getIdentifier(), presentation.getSource(), presentation.getTitle());
                    audioLoaderInfo.addLink(LinkUtils.buildLink(videoInfo));

                    VoiceUtils.playEmotion(audioLoaderInfo);
                }
            }
            if (presentation.getMessage() != null || !presentation.getMessage().equals("")) {
                MessageUtils.sendTextResponse(guild.getDefaultChannel(), presentation.getMessage());
            }
        }
    }

}
