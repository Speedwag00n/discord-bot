package ilia.nemankov.togrofbot.database.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "presentation")
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class PresentationEntity implements Serializable {

    @Id
    @Column(name = "guild_id")
    private long guildId;

    @Id
    @Column(name = "user_id")
    private long userId;

    @Column(name = "title")
    private String title;

    @Column(name = "identifier")
    private String identifier;

    @Column(name = "source")
    private String source;

    @Column(name = "duration")
    private int duration;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "message")
    private String message;

}
