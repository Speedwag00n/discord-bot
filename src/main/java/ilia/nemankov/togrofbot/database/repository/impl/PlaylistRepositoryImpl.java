package ilia.nemankov.togrofbot.database.repository.impl;

import ilia.nemankov.togrofbot.database.entity.PlaylistEntity;
import ilia.nemankov.togrofbot.database.repository.PlaylistRepository;
import ilia.nemankov.togrofbot.database.specification.HibernateSpecification;
import ilia.nemankov.togrofbot.util.HibernateSessionFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;

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
    public void removePlaylist(PlaylistEntity account) {
        Session session = HibernateSessionFactory.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();

        session.delete(account);

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
