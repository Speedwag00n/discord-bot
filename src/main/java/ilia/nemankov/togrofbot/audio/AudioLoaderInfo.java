package ilia.nemankov.togrofbot.audio;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor
public class AudioLoaderInfo {

    @Getter
    @Setter
    private VoiceChannel voiceChannel;

    @Getter
    @Setter
    private Guild guild;

    @Getter
    @Setter
    private TextChannel communicationChannel;

    @Setter
    private List<String> links = new ArrayList<>();

    public List<String> getLinks() {
        return Collections.unmodifiableList(links);
    }

    public void addLink(String link) {
        this.links.add(link);
    }

}
