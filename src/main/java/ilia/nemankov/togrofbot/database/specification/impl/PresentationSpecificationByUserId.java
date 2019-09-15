package ilia.nemankov.togrofbot.database.specification.impl;

import ilia.nemankov.togrofbot.database.entity.PresentationEntity;
import ilia.nemankov.togrofbot.database.specification.AbstractSpecification;
import lombok.AllArgsConstructor;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@AllArgsConstructor
public class PresentationSpecificationByUserId extends AbstractSpecification<PresentationEntity> {

    private Long userId;

    @Override
    public boolean isSatisfiedBy(PresentationEntity entity) {
        return entity.getGuildId() == userId.longValue();
    }

    @Override
    public Predicate getPredicate(CriteriaBuilder builder, Root<PresentationEntity> root) {
        return builder.equal(root.get("userId"), userId);
    }

}
