package ilia.nemankov.togrofbot.database.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Table (name = "playlist")
@Data
@NoArgsConstructor
public class PlaylistEntity {

    @Id
    @Column(name = "playlist_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "playlist_name")
    private String name;

    @Column(name = "guild_id")
    private long guildId;

    @OneToMany (mappedBy="playlist", fetch=FetchType.LAZY)
    private List<MusicLinkEntity> links;

}
