package ilia.nemankov.togrofbot.database.repository.impl;

import ilia.nemankov.togrofbot.database.entity.MusicLinkEntity;
import ilia.nemankov.togrofbot.database.repository.ItemNotPresentedException;
import ilia.nemankov.togrofbot.database.repository.MusicLinkRepository;
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

public class MusicLinkRepositoryImpl implements MusicLinkRepository {

    @Override
    public void addMusicLink(MusicLinkEntity entity) {
        Session session = HibernateSessionFactory.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();

        session.save(entity);

        transaction.commit();
        session.close();
    }

    @Override
    public void removeMusicLink(MusicLinkEntity entity) throws ItemNotPresentedException {
        Session session = HibernateSessionFactory.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();

        //TODO check exists ot not

        Query query = session.createQuery("DELETE PlaylistEntity WHERE name = :paramName and guildId = :paramGuildId");
        query.setParameter("paramName", entity.getPlaylist().getId());
        query.setParameter("paramGuildId", entity.getLink());

        query.executeUpdate();

        transaction.commit();
        session.close();
    }

    @Override
    public void updateMusicLink(MusicLinkEntity entity) {
        Session session = HibernateSessionFactory.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();

        session.update(entity);

        transaction.commit();
        session.close();
    }

    @Override
    public List<MusicLinkEntity> query(HibernateSpecification specification) {
        Session session = HibernateSessionFactory.getSessionFactory().openSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<MusicLinkEntity> criteria = builder.createQuery(MusicLinkEntity.class);

        Root<MusicLinkEntity> root = criteria.from(MusicLinkEntity.class);
        criteria.select(root).where(specification.getPredicate(builder, root));

        TypedQuery<MusicLinkEntity> query = session.createQuery(criteria);

        return query.getResultList();
    }

}
