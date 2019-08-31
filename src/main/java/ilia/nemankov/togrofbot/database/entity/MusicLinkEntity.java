package ilia.nemankov.togrofbot.database.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.concurrent.Immutable;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@NamedEntityGraph(
    name = "music-link-entity",
    attributeNodes = {
        @NamedAttributeNode("identifier"),
        @NamedAttributeNode("source"),
        @NamedAttributeNode("title"),
        @NamedAttributeNode("creationDatetime")
    }
)
@Entity
@Table(name = "music_link")
@Immutable
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

    @Column(name = "creation_datetime")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDatetime;

}
