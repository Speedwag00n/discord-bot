package ilia.nemankov.togrofbot.database.specification.impl;

import ilia.nemankov.togrofbot.database.entity.PlaylistEntity;
import ilia.nemankov.togrofbot.database.specification.HibernateSpecification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class PlaylistSpecificationByNameAndGuildId implements HibernateSpecification<PlaylistEntity> {

    private String name;
    private Long guildId;

    public PlaylistSpecificationByNameAndGuildId(String name, Long guildId) {
        this.name = name;
        this.guildId = guildId;
    }

    @Override
    public Predicate getPredicate(CriteriaBuilder builder, Root<PlaylistEntity> root) {
        return builder.and(builder.equal(root.<String>get("name"), name), builder.equal(root.<Long>get("guildId"), guildId));
    }

}
