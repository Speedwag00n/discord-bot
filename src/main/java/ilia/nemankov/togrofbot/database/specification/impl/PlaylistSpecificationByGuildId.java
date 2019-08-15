package ilia.nemankov.togrofbot.database.specification.impl;

import ilia.nemankov.togrofbot.database.entity.PlaylistEntity;
import ilia.nemankov.togrofbot.database.specification.HibernateSpecification;
import org.hibernate.criterion.Restrictions;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class PlaylistSpecificationByGuildId implements HibernateSpecification<PlaylistEntity> {

    private Long guildId;

    public PlaylistSpecificationByGuildId(Long guildId) {
        this.guildId = guildId;
    }

    @Override
    public Predicate getPredicate(CriteriaBuilder builder, Root<PlaylistEntity> root) {
        return builder.equal(root.get("guildId"), guildId);
    }

}
