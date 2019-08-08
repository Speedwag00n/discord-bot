package ilia.nemankov.togrofbot.database.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "music_link")
@Data
@NoArgsConstructor
public class MusicLinkEntity implements Serializable {

    private static final long serialVersionUID = 1318950329814538445L;

    @Id
    @Column(name = "identifier")
    private String identifier;

    @Id
    @ManyToOne (optional=false, cascade=CascadeType.ALL)
    @JoinColumn (name="playlist_id")
    private PlaylistEntity playlist;

    @Id
    @Column(name = "source")
    private String source;

    @Column(name = "title")
    private String title;

}
