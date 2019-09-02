package ilia.nemankov.togrofbot.database.specification.impl;

import ilia.nemankov.togrofbot.database.entity.AliasEntity;
import ilia.nemankov.togrofbot.database.specification.AbstractSpecification;
import lombok.AllArgsConstructor;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@AllArgsConstructor
public class AliasSpecificationByName extends AbstractSpecification<AliasEntity> {

    private String name;

    @Override
    public boolean isSatisfiedBy(AliasEntity entity) {
        return entity.getName().equals(name);
    }

    @Override
    public Predicate getPredicate(CriteriaBuilder builder, Root<AliasEntity> root) {
        return builder.and(builder.equal(root.<String>get("name"), name));
    }

}
