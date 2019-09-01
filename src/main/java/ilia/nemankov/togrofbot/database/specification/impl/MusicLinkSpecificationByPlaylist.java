package ilia.nemankov.togrofbot.database.specification.impl;

import ilia.nemankov.togrofbot.database.entity.MusicLinkEntity;
import ilia.nemankov.togrofbot.database.entity.PlaylistEntity;
import ilia.nemankov.togrofbot.database.specification.AbstractSpecification;
import lombok.AllArgsConstructor;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

@AllArgsConstructor
public class MusicLinkSpecificationByPlaylist extends AbstractSpecification<MusicLinkEntity> {

    private PlaylistEntity playlist;

    @Override
    public boolean isSatisfiedBy(MusicLinkEntity entity) {
        return entity.getPlaylist().equals(playlist);
    }

    @Override
    public Predicate getPredicate(CriteriaBuilder builder, Root<MusicLinkEntity> root) {
        return builder.equal(root.get("playlist"), playlist);
    }

}
