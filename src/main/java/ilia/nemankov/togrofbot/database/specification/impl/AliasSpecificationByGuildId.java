package ilia.nemankov.togrofbot.database.specification.impl;

import ilia.nemankov.togrofbot.database.entity.AliasEntity;
import ilia.nemankov.togrofbot.database.specification.AbstractSpecification;
import lombok.AllArgsConstructor;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@AllArgsConstructor
public class AliasSpecificationByGuildId extends AbstractSpecification<AliasEntity> {

    private Long guildId;

    @Override
    public boolean isSatisfiedBy(AliasEntity entity) {
        return entity.getGuildId() == guildId.longValue();
    }

    @Override
    public Predicate getPredicate(CriteriaBuilder builder, Root<AliasEntity> root) {
        return builder.equal(root.get("guildId"), guildId);
    }

}
