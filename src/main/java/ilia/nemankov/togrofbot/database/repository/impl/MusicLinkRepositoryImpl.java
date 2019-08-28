package ilia.nemankov.togrofbot.database.repository.impl;

import ilia.nemankov.togrofbot.database.entity.MusicLinkEntity;
import ilia.nemankov.togrofbot.database.repository.MusicLinkRepository;
import ilia.nemankov.togrofbot.database.repository.QuerySettings;
import ilia.nemankov.togrofbot.database.specification.HibernateSpecification;
import ilia.nemankov.togrofbot.util.HibernateUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

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
    public int removeMusicLink(MusicLinkEntity entity) {
        Session session = HibernateUtils.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();

        Query query = session.createQuery("DELETE MusicLinkEntity WHERE identifier = :paramIdentifier and playlist = :paramPlaylist and source = :paramSource");
        query.setParameter("paramIdentifier", entity.getIdentifier());
        query.setParameter("paramPlaylist", entity.getPlaylist());
        query.setParameter("paramSource", entity.getSource());

        try {
            int result = query.executeUpdate();
            transaction.commit();
            return result;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    @Override
    public long count(HibernateSpecification specification) {
        return this.count(specification, null);
    }

    @Override
    public long count(HibernateSpecification specification, QuerySettings settings) {
        Session session = HibernateUtils.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();

        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Long> criteria = builder.createQuery(Long.class);

        Root<MusicLinkEntity> root = criteria.from(MusicLinkEntity.class);
        criteria.select(builder.count(root)).where(specification.getPredicate(builder, root));
        criteria.orderBy(builder.asc(root.get("creationDatetime")));

        TypedQuery<Long> query = session.createQuery(criteria);
        if (settings != null) {
            HibernateUtils.applySettings(query, settings);
        }

        try {
            long result = query.getSingleResult();
            transaction.commit();
            return result;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    @Override
    public List<MusicLinkEntity> query(HibernateSpecification specification, String graphName) {
        return this.query(specification, graphName, null);
    }

    @Override
    public List<MusicLinkEntity> query(HibernateSpecification specification, String graphName, QuerySettings settings) {
        Session session = HibernateUtils.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();

        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<MusicLinkEntity> criteria = builder.createQuery(MusicLinkEntity.class);

        Root<MusicLinkEntity> root = criteria.from(MusicLinkEntity.class);
        criteria.select(root).where(specification.getPredicate(builder, root));

        TypedQuery<MusicLinkEntity> query = session.createQuery(criteria);
        query.setHint("javax.persistence.fetchgraph", session.getEntityGraph(graphName));
        if (settings != null) {
            HibernateUtils.applySettings(query, settings);
        }

        try {
            List<MusicLinkEntity> result = query.getResultList();
            transaction.commit();
            return result;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

}
