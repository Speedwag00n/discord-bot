package ilia.nemankov.togrofbot.database.specification.impl;

import ilia.nemankov.togrofbot.database.entity.MusicLinkEntity;
import ilia.nemankov.togrofbot.database.entity.PlaylistEntity;
import ilia.nemankov.togrofbot.database.specification.HibernateSpecification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class MusicLinkSpecificationByPlaylist implements HibernateSpecification<MusicLinkEntity> {

    private PlaylistEntity playlist;

    public MusicLinkSpecificationByPlaylist(PlaylistEntity playlist) {
        this.playlist = playlist;
    }

    @Override
    public Predicate getPredicate(CriteriaBuilder builder, Root<MusicLinkEntity> root) {
        return builder.equal(root.get("playlist"), playlist);
    }

}
