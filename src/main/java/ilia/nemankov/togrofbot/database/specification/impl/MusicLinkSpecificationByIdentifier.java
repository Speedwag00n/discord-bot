package ilia.nemankov.togrofbot.database.specification.impl;

import ilia.nemankov.togrofbot.database.entity.MusicLinkEntity;
import ilia.nemankov.togrofbot.database.specification.AbstractSpecification;
import lombok.AllArgsConstructor;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@AllArgsConstructor
public class MusicLinkSpecificationByIdentifier extends AbstractSpecification<MusicLinkEntity> {

    private String identifier;

    @Override
    public boolean isSatisfiedBy(MusicLinkEntity entity) {
        return entity.getPlaylist().equals(identifier);
    }

    @Override
    public Predicate getPredicate(CriteriaBuilder builder, Root<MusicLinkEntity> root) {
        return builder.equal(root.get("identifier"), identifier);
    }

}
