package ilia.nemankov.togrofbot.database.repository.impl;

import ilia.nemankov.togrofbot.database.entity.MusicLinkEntity;
import ilia.nemankov.togrofbot.database.repository.MusicLinkRepository;
import ilia.nemankov.togrofbot.database.specification.Specification;
import ilia.nemankov.togrofbot.util.HibernateUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.Root;

public class MusicLinkRepositoryImpl implements MusicLinkRepository {

    @Override
    public void addMusicLink(MusicLinkEntity entity) {
        Session session = HibernateUtils.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();

        try {
            session.save(entity);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    @Override
    public boolean removeMusicLink(MusicLinkEntity entity) {
        Session session = HibernateUtils.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();

        Query query = session.createQuery("DELETE MusicLinkEntity WHERE identifier = :paramIdentifier and playlist = :paramPlaylist and source = :paramSource");
        query.setParameter("paramIdentifier", entity.getIdentifier());
        query.setParameter("paramPlaylist", entity.getPlaylist());
        query.setParameter("paramSource", entity.getSource());

        try {
            int deleted = query.executeUpdate();
            transaction.commit();
            return deleted != 0;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    @Override
    public long removeMusicLinks(Specification<MusicLinkEntity> specification) {
        Session session = HibernateUtils.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();

        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaDelete<MusicLinkEntity> delete = builder.createCriteriaDelete(specification.getType());

        Root<MusicLinkEntity> root = delete.from(specification.getType());
        delete.where(specification.getPredicate(builder, root));

        TypedQuery<Long> query = session.createQuery(delete);
        try {
            long result = query.executeUpdate();
            transaction.commit();
            return result;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

}
