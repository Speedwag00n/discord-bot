package ilia.nemankov.togrofbot.database.repository.impl;

import ilia.nemankov.togrofbot.database.entity.PlaylistEntity;
import ilia.nemankov.togrofbot.database.repository.ItemNotPresentedException;
import ilia.nemankov.togrofbot.database.repository.PlaylistRepository;
import ilia.nemankov.togrofbot.database.specification.HibernateSpecification;
import ilia.nemankov.togrofbot.util.HibernateSessionFactory;
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
    public void addPlaylist(PlaylistEntity account) {
        Session session = HibernateSessionFactory.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();

        session.save(account);

        transaction.commit();
        session.close();
    }

    @Override
    public void removePlaylist(PlaylistEntity account) throws ItemNotPresentedException {
        Session session = HibernateSessionFactory.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();

        Query query = session.createQuery("FROM PlaylistEntity WHERE name = :paramName and guildId = :paramGuildId");
        query.setParameter("paramName", account.getName());
        query.setParameter("paramGuildId", account.getGuildId());

        if (query.getResultList().size() == 0) {
            throw new ItemNotPresentedException();
        }

        query = session.createQuery("DELETE PlaylistEntity WHERE name = :paramName and guildId = :paramGuildId");
        query.setParameter("paramName", account.getName());
        query.setParameter("paramGuildId", account.getGuildId());

        query.executeUpdate();

        transaction.commit();
        session.close();
    }

    @Override
    public void updatePlaylist(PlaylistEntity account) {
        Session session = HibernateSessionFactory.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();

        session.update(account);

        transaction.commit();
        session.close();
    }

    @Override
    public List<PlaylistEntity> query(HibernateSpecification specification) {
        Session session = HibernateSessionFactory.getSessionFactory().openSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<PlaylistEntity> criteria = builder.createQuery(PlaylistEntity.class);

        Root<PlaylistEntity> root = criteria.from(PlaylistEntity.class);
        criteria.select(root).where(specification.getPredicate(builder, root));

        TypedQuery<PlaylistEntity> query = session.createQuery(criteria);

        return query.getResultList();
    }

}
