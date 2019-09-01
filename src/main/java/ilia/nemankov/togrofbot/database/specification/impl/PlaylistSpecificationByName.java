package ilia.nemankov.togrofbot.database.specification.impl;

import ilia.nemankov.togrofbot.database.entity.PlaylistEntity;
import ilia.nemankov.togrofbot.database.specification.AbstractSpecification;
import lombok.AllArgsConstructor;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@AllArgsConstructor
public class PlaylistSpecificationByName extends AbstractSpecification<PlaylistEntity> {

    private String name;

    @Override
    public boolean isSatisfiedBy(PlaylistEntity entity) {
        return entity.getName().equals(name);
    }

    @Override
    public Predicate getPredicate(CriteriaBuilder builder, Root<PlaylistEntity> root) {
        return builder.and(builder.equal(root.<String>get("name"), name));
    }

}
