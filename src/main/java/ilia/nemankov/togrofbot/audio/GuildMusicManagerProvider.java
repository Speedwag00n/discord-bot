package ilia.nemankov.togrofbot.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import net.dv8tion.jda.core.entities.Guild;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class GuildMusicManagerProvider {

    private static final Logger logger = LoggerFactory.getLogger(GuildMusicManagerProvider.class);

    private static GuildMusicManagerProvider instance;
    static {
        instance = new GuildMusicManagerProvider();
        logger.debug("Created {} class instance", GuildMusicManagerProvider.class.getSimpleName());
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
            logger.debug("Created {} for Guild with id {}", musicManager.getClass().getSimpleName(), guild.getId());
        }

        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

        return musicManager;
    }

    public AudioPlayerManager getPlayerManager() {
        return playerManager;
    }

}
