package ilia.nemankov.togrofbot.database.repository.impl;

import ilia.nemankov.togrofbot.database.entity.MusicLinkEntity;
import ilia.nemankov.togrofbot.database.repository.MusicLinkRepository;
import ilia.nemankov.togrofbot.util.HibernateUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.persistence.Query;

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

}
