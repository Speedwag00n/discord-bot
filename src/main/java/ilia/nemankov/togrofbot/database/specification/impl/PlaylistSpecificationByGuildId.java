package ilia.nemankov.togrofbot.database.specification.impl;

import ilia.nemankov.togrofbot.database.entity.PlaylistEntity;
import ilia.nemankov.togrofbot.database.specification.AbstractSpecification;
import lombok.AllArgsConstructor;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@AllArgsConstructor
public class PlaylistSpecificationByGuildId extends AbstractSpecification<PlaylistEntity> {

    private Long guildId;

    @Override
    public boolean isSatisfiedBy(PlaylistEntity entity) {
        return entity.getGuildId() == guildId.longValue();
    }

    @Override
    public Predicate getPredicate(CriteriaBuilder builder, Root<PlaylistEntity> root) {
        return builder.equal(root.get("guildId"), guildId);
    }

}
