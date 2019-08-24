package ilia.nemankov.togrofbot.database.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NamedEntityGraph(
   name = "alias-entity",
   attributeNodes = {
       @NamedAttributeNode("id"),
       @NamedAttributeNode("name"),
       @NamedAttributeNode("guildId"),
       @NamedAttributeNode("command")
   }
)
@Entity
@Table(name = "alias")
@Data
@NoArgsConstructor
public class AliasEntity {

    @Id
    @Column(name = "alias_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "alias_name")
    private String name;

    @Column(name = "guild_id")
    private long guildId;

    @Column(name = "command")
    private String command;

}
