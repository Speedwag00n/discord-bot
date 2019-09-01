package ilia.nemankov.togrofbot.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.core.entities.Guild;

import java.util.HashMap;

@Slf4j
public class GuildMusicManagerProvider {

    private static GuildMusicManagerProvider instance;
    static {
        instance = new GuildMusicManagerProvider();
        log.debug("Created {} class instance", GuildMusicManagerProvider.class.getSimpleName());
    }

    private HashMap<Long, GuildMusicManager> managers;
    private AudioPlayerManager playerManager = new DefaultAudioPlayerManager();

    private GuildMusicManagerProvider() {
        managers = new HashMap<>();

        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
    }

    public static GuildMusicManagerProvider getInstance() {
        return instance;
    }

    public synchronized GuildMusicManager getGuildMusicManager(Guild guild) {
        long guildId = Long.parseLong(guild.getId());
        GuildMusicManager musicManager = managers.get(guildId);

        if (musicManager == null) {
            musicManager = new GuildMusicManager(playerManager);
            managers.put(guildId, musicManager);
            log.debug("Created {} for Guild with id {}", musicManager.getClass().getSimpleName(), guild.getId());
        }

        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

        return musicManager;
    }

    public AudioPlayerManager getPlayerManager() {
        return playerManager;
    }

}
