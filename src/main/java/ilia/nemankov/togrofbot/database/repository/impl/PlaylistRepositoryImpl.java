package ilia.nemankov.togrofbot.database.repository.impl;

import ilia.nemankov.togrofbot.database.entity.PlaylistEntity;
import ilia.nemankov.togrofbot.database.repository.PlaylistRepository;
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

public class PlaylistRepositoryImpl implements PlaylistRepository {

    @Override
    public void addPlaylist(PlaylistEntity entity) {
        Session session = HibernateUtils.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();

        session.save(entity);

        transaction.commit();
        session.close();
    }

    @Override
    public int removePlaylist(PlaylistEntity entity) {
        Session session = HibernateUtils.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();

        Query query = session.createQuery("DELETE PlaylistEntity WHERE name = :paramName and guildId = :paramGuildId");
        query.setParameter("paramName", entity.getName());
        query.setParameter("paramGuildId", entity.getGuildId());

        int deleted = query.executeUpdate();

        transaction.commit();
        session.close();

        return deleted;
    }

    @Override
    public void updatePlaylist(PlaylistEntity entity) {
        Session session = HibernateUtils.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();

        session.update(entity);

        transaction.commit();
        session.close();
    }

    @Override
    public List<PlaylistEntity> query(HibernateSpecification specification) {
        return this.query(specification, null);
    }

    @Override
    public List<PlaylistEntity> query(HibernateSpecification specification, QuerySettings settings) {
        Session session = HibernateUtils.getSessionFactory().openSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<PlaylistEntity> criteria = builder.createQuery(PlaylistEntity.class);

        Root<PlaylistEntity> root = criteria.from(PlaylistEntity.class);
        criteria.select(root).where(specification.getPredicate(builder, root));

        TypedQuery<PlaylistEntity> query = session.createQuery(criteria);
        if (settings != null) {
            HibernateUtils.applySettings(query, settings);
        }

        return query.getResultList();
    }

}
